package com.example.manager.appbanhang.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.manager.appbanhang.Adapter.GioHangAdapter;
import com.example.manager.appbanhang.Model.EventBus.TinhTongEvent;
import com.example.appbanhang.R;
import com.example.manager.appbanhang.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

public class GioHangActivity extends AppCompatActivity {
    TextView giohangtrong, tongtien;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button btnmuahang;
    GioHangAdapter adapter;
    long tongtiensp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        initView();
        initControl();
        //Nếu Utils.mangmuahang không null, nó sẽ được xóa để đảm bảo dữ liệu mới.
        if (Utils.mangmuahang !=null){
            Utils.mangmuahang.clear();
        }
        tinhTongTien();

    }

    private void tinhTongTien() { //Duyệt qua từng sản phẩm trong giỏ hàng, tính tổng tiền và định dạng nó để hiển thị.
        tongtiensp = 0;
        for (int i = 0; i< Utils.mangmuahang.size(); i++){
            tongtiensp = tongtiensp+ (Utils.mangmuahang.get(i).getGiasp()* Utils.mangmuahang.get(i).getSoluong());

        }
        // sử dụng để định dạng số thành chuỗi theo một mẫu nhất định
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

        tongtien.setText(decimalFormat.format(tongtiensp));
    }

    private void initControl() { // Nếu giỏ hàng trống, hiển thị thông báo; nếu không, thiết lập adapter cho RecyclerView. Khi nhấn nút mua hàng, chuyển sang activity thanh toán và truyền tổng số tiền.
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (Utils.manggiohang.size() == 0) {
            giohangtrong.setVisibility(View.VISIBLE);
        } else {
            adapter = new GioHangAdapter(getApplicationContext(), Utils.manggiohang);
            recyclerView.setAdapter(adapter);
        }
        btnmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                intent.putExtra("tongtien",tongtiensp );
                //Utils.manggiohang.clear();

                startActivity(intent);
            }
        });
    }

    private void initView() {
        giohangtrong = findViewById(R.id.txtgiohangtrong);
        tongtien = findViewById(R.id.txttongtien);
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleviewgiohang);
        btnmuahang = findViewById(R.id.btnmuahang);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Đăng ký và hủy đăng ký sự kiện để lắng nghe các thay đổi trong giỏ hàng.
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe (sticky = true, threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event){
        if (event !=null){
            tinhTongTien();

        }
    }
}
