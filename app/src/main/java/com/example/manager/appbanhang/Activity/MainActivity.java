package com.example.manager.appbanhang.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.manager.appbanhang.Adapter.LoaiSpAdapter;
import com.example.manager.appbanhang.Adapter.MauSanPhamAdapter;
import com.example.manager.appbanhang.Model.LoaiSp;
import com.example.manager.appbanhang.Model.MauSanPham;
import com.example.manager.appbanhang.Model.User;
import com.example.appbanhang.R;
import com.example.manager.appbanhang.retrofit.ApiBanHang;
import com.example.manager.appbanhang.retrofit.RetrofitClient;
import com.example.manager.appbanhang.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<MauSanPham> mangMauSp;
    MauSanPhamAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if (Paper.book().read("user") != null) {
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        getToken();
        Anhxa();
        ActionBar();

        if (isConnected(this)){
            ActionViewFlipper();
            getLoaiSanPham();
            getMauSp();
            getEventClick();
        }else{
            Toast.makeText(getApplicationContext(),"khong co internet",Toast.LENGTH_LONG).show();
        }
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(),s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                       messageModel -> {

                                       },
                                       throwable -> {
                                           Log.d("log", throwable.getMessage());
                                       }
                                    ));
                        }
                    }
                });
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent giay = new Intent(getApplicationContext(), GiayActivity.class);
                        giay.putExtra("loai",1);
                        startActivity(giay);
                        break;
                    case 2:
                        Intent dep = new Intent(getApplicationContext(), DepActivity.class);
                        dep.putExtra("loai",2);
                        startActivity(dep);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        Intent quanli = new Intent(getApplicationContext(), QuanliActivity.class);
                        startActivity(quanli);
                        finish();
                        break;
                    case 7:
                        Intent thongke = new Intent(getApplicationContext(), ThongKeActivity.class);
                        startActivity(thongke);
                        finish();
                        break;
                    case 8:
                        // xoa key user
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }

    private void getMauSp() {
        compositeDisposable.add(apiBanHang.getMauSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mauSanPhamModel -> {
                            if (mauSanPhamModel.isSuccess()){
                                mangMauSp = mauSanPhamModel.getResult();
                                spAdapter = new MauSanPhamAdapter(getApplicationContext(), mangMauSp);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);
                            }

                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),"Khong ket noi duoc voi server"+throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()){
                                mangloaisp = loaiSpModel.getResult();
                                mangloaisp.add(new LoaiSp("Quản lí","data:image/png;base64,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"));
                                mangloaisp.add(new LoaiSp("Thống kê","data:image/png;base64,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"));
                                mangloaisp.add(new LoaiSp("Đăng xuất","data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAaVBMVEX///8BAQHq6uqEhISwsLBYWFirq6sEBAT29vZhYWHk5OTX19dkZGT7+/vs7Ozd3d1cXFw0NDQbGxs6OjpCQkKmpqYgICArKyucnJwSEhIlJSWYmJiCgoKKiopra2ttbW12dnZPT09BQUEyNdlFAAACdklEQVR4nO3c7W7aQBBG4bUTiBPDNpCP0jYkbe//IqMQKN1KTQY0i+c15/yzZIl5tF5+YOPUt6Mu9ymncZdTO/QIlWsRyodQP4T6IdQPoX4I9UOoH0L9EOqHUD+E+iHUD6F+CPVDqB9C/RDqh1A/hPoh1A+hfgj1Q6gfQv0Q6odQP4T6IdQPoX4I9UOoH0L9EOqHUD+E9r48TA9v7vThH+QnnDZH1Dl9+Af5CS+PAF4hdAihvXMRPs6uTU1khbfGs7OscGI8+wahWwjtISxD6BdCewjLEPqF0B7CMoR+IbSHsOwMhdPF15XTJGVRhKu3w5nTKEXDCve/eb8fXjvN8ndDCbuH8r7Fu7DGKg4l/LdJNWIUYW5qEaMIU/7W1NmLYYQpL+usYhxhah+rEAMJU7uscaFGEu73oucqhhKm7rs/8RTC/OPpwtb6uXG/UE8gvGmO6cVprFMIZ0cJm95prhMIl59Q/tOl01wnED5/QtFfw/Hvw9StjF+lF+ufO6DUd+kh3bkDYwm7xRZofaLDUiRht1vBX04jbQok7O5rAAMJ53WAgYQ74L3TPLvCCGsBwwiftsDfTtPsG0o47zf9Od4C7/wf/I71m/eiwpPtUe5brDfAGreiogjfNuJtlXttYYTVQmgPYRlCvxDaQ1iG0C+E9hCWIfQLoT2EZQj94j+k9s7lf8CHhdAjhPbGLxz/u03G/36aqCHUD6F+CPVDqB9C/RDqh1A/hPoh1A+hfgj1Q6gfQv0Q6odQP4T6IdQPoX4I9UOoH0L9EOqHUD+E+iHUD6F+CPVDqB9C/RDqh1A/hPoh1A+hfucgzEOPULmc+tyOudy/AtXzIXnvD+GwAAAAAElFTkSuQmCC"));
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        },
                        throwable -> {
                            // Xử lý lỗi tại đây
                            Toast.makeText(getApplicationContext(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }


    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://thietke6d.com/wp-content/uploads/2021/05/banner-quang-cao-giay-3.webp");
        mangquangcao.add("https://bizweb.dktcdn.net/100/339/085/themes/699262/assets/slider_2_image.png?1702807072310");
        mangquangcao.add("https://bizweb.dktcdn.net/100/339/085/themes/699262/assets/slider_3_image.png?1702807072310");
        for (int i =0; i<mangquangcao.size(); i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);

    }

    private void ActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
    }
    private void Anhxa(){
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //khoi tao list
        mangloaisp = new ArrayList<>();
        mangMauSp = new ArrayList<>();
        if (Paper.book().read("giohang") != null){
            Utils.manggiohang = Paper.book().read("giohang");
        }
        if (Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else{
            int totalItem = 0;
            for (int i=0; i<Utils.manggiohang.size(); i++){
                totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);

            }
        });
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i=0; i<Utils.manggiohang.size(); i++){
            totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                network = connectivityManager.getActiveNetwork();
            }
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
