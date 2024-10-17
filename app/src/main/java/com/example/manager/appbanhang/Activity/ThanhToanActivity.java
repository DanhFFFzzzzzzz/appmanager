package com.example.manager.appbanhang.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appbanhang.R;
import com.example.manager.appbanhang.Model.CreateOrder;
import com.example.manager.appbanhang.Model.GioHang;
import com.example.manager.appbanhang.retrofit.ApiBanHang;
import com.example.manager.appbanhang.retrofit.RetrofitClient;
import com.example.manager.appbanhang.utils.Utils;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.json.JSONObject;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txttongtien, txtsodt, txtemail;
    EditText edtdiachi;
    AppCompatButton btndathang, btnzalopay;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long tongtien;
    int totalItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        // ZaloPay SDK Init
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        initView();
        countItem();
        initControl();
    }

    private void countItem() { //Tính tổng số sản phẩm trong giỏ hàng (mangmuahang).
        totalItem = 0;
        for (GioHang item : Utils.mangmuahang) {
            totalItem += item.getSoluong();
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien = getIntent().getLongExtra("tongtien", 0);
        txttongtien.setText(decimalFormat.format(tongtien));
        txtemail.setText(Utils.user_current.getEmail());
        txtsodt.setText(Utils.user_current.getSodienthoai());

        btndathang.setOnClickListener(view -> {
            String str_diachi = edtdiachi.getText().toString().trim();
            if (TextUtils.isEmpty(str_diachi)) {
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
            } else {
                createOrder();  // Gọi phương thức createOrder đã chỉnh sửa
            }
        });

        btnzalopay.setOnClickListener(view -> {
            String str_diachi = edtdiachi.getText().toString().trim();
            if (TextUtils.isEmpty(str_diachi)) {
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            } else {
                createZaloPayOrder();  // Gọi phương thức tạo đơn hàng ZaloPay đã chỉnh sửa
            }
        });
    }

    //Phương thức này thực hiện gọi API để tạo đơn hàng.
    private void createBaseOrder(Runnable successAction, Runnable errorAction) {
        String str_email = Utils.user_current.getEmail();
        String str_sdt = Utils.user_current.getSodienthoai();
        int id = Utils.user_current.getId();

        compositeDisposable.add(apiBanHang.createOder(str_email, str_sdt, String.valueOf(tongtien), id, edtdiachi.getText().toString(), totalItem, new Gson().toJson(Utils.mangmuahang))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            Toast.makeText(getApplicationContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                            clearCart();  // Xóa giỏ hàng
                            successAction.run();  // Gọi hành động thành công
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            errorAction.run();  // Gọi hành động lỗi
                        }
                ));
    }

    // createOrder: Gọi createBaseOrder và xử lý chuyển hướng đến màn hình chính nếu đặt hàng thành công.
    private void createOrder() {
        createBaseOrder(
                () -> {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                () -> {
                    Toast.makeText(getApplicationContext(), "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                }
        );
    }

    // sau khi đặt hàng thành công, nó tiếp tục tạo đơn hàng cho ZaloPay.
    private void createZaloPayOrder() {
        createBaseOrder(
                () -> {
                    // Sau khi đặt hàng thành công, gọi tiếp ZaloPay order
                    try {
                        JSONObject data = new CreateOrder().createOrder(String.valueOf(tongtien));
                        String code = data.getString("return_code");

                        if (code.equals("1")) {  // Kiểm tra mã trả về có hợp lệ không
                            String token = data.getString("zp_trans_token");
                            payOrder(token);  // Gọi phương thức payOrder với token
                        } else {
                            Toast.makeText(getApplicationContext(), "Lỗi khi tạo đơn hàng: " + data.getString("return_message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                () -> {
                    Toast.makeText(getApplicationContext(), "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                }
        );
    }
    //Thực hiện thanh toán thông qua ZaloPay
    private void payOrder(String token) {
        ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                new AlertDialog.Builder(ThanhToanActivity.this)
                        .setTitle("Thanh toán thành công")
                        .setMessage(String.format("Mã giao dịch: %s - TransToken: %s", transactionId, transToken))
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Quay trở lại màn hình chính
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish(); // Đóng Activity hiện tại
                        })
                        .setNegativeButton("Cancel", null).show();
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                new AlertDialog.Builder(ThanhToanActivity.this)
                        .setTitle("Người dùng hủy thanh toán")
                        .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                        .setPositiveButton("OK", (dialog, which) -> {})
                        .setNegativeButton("Cancel", null).show();
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                new AlertDialog.Builder(ThanhToanActivity.this)
                        .setTitle("Thanh toán thất bại")
                        .setMessage(String.format("Mã lỗi ZaloPay: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken))
                        .setPositiveButton("OK", (dialog, which) -> {})
                        .setNegativeButton("Cancel", null).show();
            }
        });
    }

    //Xóa giỏ hàng (mangmuahang) sau khi đặt hàng thành công.
    private void clearCart() {
        for (GioHang gioHang : Utils.mangmuahang) {
            if (Utils.manggiohang.contains(gioHang)) {
                Utils.manggiohang.remove(gioHang);
            }
        }
        Utils.mangmuahang.clear();
        Paper.book().write("giohang", Utils.manggiohang);
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toobar);
        txttongtien = findViewById(R.id.txttongtien);
        txtsodt = findViewById(R.id.txtsodienthoai);
        txtemail = findViewById(R.id.txtemail);
        edtdiachi = findViewById(R.id.edtdiachi);
        btndathang = findViewById(R.id.btndathang);
        btnzalopay = findViewById(R.id.btnzalopay);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}