package com.example.manager.appbanhang.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appbanhang.R;
import com.example.manager.appbanhang.retrofit.ApiBanHang;
import com.example.manager.appbanhang.retrofit.RetrofitClient;
import com.example.manager.appbanhang.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {
    private TextView txtdangky, txtresetpass;
    private EditText email, pass;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private AppCompatButton btndangnhap;
    private ApiBanHang apiBanHang;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        initView();
        initControl();
    }

    /**
     * Initialize the view components and read saved data if available.
     */
    private void initView() {
        // Khởi tạo Paper để duy trì dữ liệu
        Paper.init(this);

        // Khởi tạo ứng dụng khách API Retrofit
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        // Liên kết các thành phần UI
        txtdangky = findViewById(R.id.txtdangky);
        txtresetpass = findViewById(R.id.txtresetpass);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        // Đọc email và mật khẩu đã lưu
        String savedEmail = Paper.book().read("email");
        String savedPass = Paper.book().read("pass");

        if (savedEmail != null && savedPass != null) {
            email.setText(savedEmail);
            pass.setText(savedPass);
            if (Paper.book().read("islogin") != null){
                boolean flag = Paper.book().read("islogin");
                if(flag){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //dangNhap(Paper.book().read("email"), Paper.book().read("pass"));

                        }
                    },1000);
                }
            }
        }
    }

    private void dangNhap(String email, String pass) {
        compositeDisposable.add(apiBanHang.dangNhap(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                isLogin = true;
                                Paper.book().write("islogin", isLogin);

                                // Chỉ định người dùng hiện tại
                                Utils.user_current = userModel.getResult().get(0);
                                // lưu lại thông tin người dùng
                                Paper.book().write("user", userModel.getResult().get(0));
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Xử lý lỗi đăng nhập
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            // Xử lý lỗi mạng hoặc lỗi khác
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));

    }

    /**
     * Set up control listeners for UI components.
     */
    private void initControl() {
        // Điều hướng đến DangKyActivity (Đăng ký hoạt động)
        txtdangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DangNhapActivity.this, DangKyActivity.class);
                startActivity(intent);
            }
        });
        // Điều hướng đến ResetPassActivity (Hoạt động đặt lại mật khẩu)
        txtresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DangNhapActivity.this, ResetPassActivity.class);
                startActivity(intent);
            }
        });
        // Xử lý nút Đăng nhập
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });
    }
    private void handleLogin() {
        String strEmail = email.getText().toString().trim();
        String strPass = pass.getText().toString().trim();
        // Xác thực đầu vào
        if (TextUtils.isEmpty(strEmail)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strPass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Password", Toast.LENGTH_SHORT).show();
            return;
        }
        //Lưu email và mật khẩu bằng Paper
        Paper.book().write("email", strEmail);
        Paper.book().write("pass", strPass);
        if (user != null){
            // user da co dang nhap firebase chua signout
            dangNhap(strEmail,strPass);
        }else {
            //user da signout
            //co 2 noi quan ly mat khau
            firebaseAuth.signInWithEmailAndPassword(strEmail, strPass)
                    .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                dangNhap(strEmail,strPass);
                            }
                        }
                    });
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Cập nhật UI với thông tin người dùng hiện tại nếu có
        if (Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null) {
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        // Xóa tất cả các mục dùng một lần để tránh rò rỉ bộ nhớ
        compositeDisposable.clear();
        super.onDestroy();
    }
}
