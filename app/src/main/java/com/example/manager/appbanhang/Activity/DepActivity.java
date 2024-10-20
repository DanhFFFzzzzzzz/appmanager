package com.example.manager.appbanhang.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.manager.appbanhang.Adapter.GiayAdapter;
import com.example.manager.appbanhang.Model.MauSanPham;
import com.example.appbanhang.R;
import com.example.manager.appbanhang.retrofit.ApiBanHang;
import com.example.manager.appbanhang.retrofit.RetrofitClient;
import com.example.manager.appbanhang.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DepActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page =1;
    int loai;
    GiayAdapter adapterGiay; //adapterGiay để liên kết dữ liệu với RecyclerView
    List<MauSanPham> mauSanPhamList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dep);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        loai = getIntent().getIntExtra("loai",1);

        AnhXa();
        ActionToolBar();
        getData(page);
        addEventLoad();
    }

    private void addEventLoad() { //Thêm sự kiện cho RecyclerView để phát hiện khi người dùng cuộn đến cuối danh sách.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading == false){
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == mauSanPhamList.size()-1){
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() { //Tải thêm dữ liệu khi cuộn đến cuối danh sách và thêm một item "loading" vào danh sách.
        handler.post(new Runnable() {
            @Override
            public void run() {
                // add null
                mauSanPhamList.add(null);
                adapterGiay.notifyItemInserted(mauSanPhamList.size()-1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remove null
                mauSanPhamList.remove(mauSanPhamList.size()-1);
                adapterGiay.notifyItemRemoved(mauSanPhamList.size());
                page = page+1;
                getData(page);
                adapterGiay.notifyDataSetChanged();
                isLoading = false;

            }
        },2000);
    }

    private void getData(int page) { //Gọi API để lấy danh sách sản phẩm theo trang và loại sản phẩm.
        compositeDisposable.add(apiBanHang.getSanPham(page,loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        MauSanPhamModel ->{
                            if (MauSanPhamModel.isSuccess()){
                                if(adapterGiay == null){
                                    mauSanPhamList = MauSanPhamModel.getResult();
                                    adapterGiay = new GiayAdapter(getApplicationContext(), mauSanPhamList);
                                    recyclerView.setAdapter(adapterGiay);
                                }else {
                                    int vitri = mauSanPhamList.size()-1;
                                    int soluongadd = MauSanPhamModel.getResult().size();
                                    for (int i= 0 ; i<soluongadd; i++){
                                        mauSanPhamList.add(MauSanPhamModel.getResult().get(i));
                                    }
                                    adapterGiay.notifyItemRangeInserted(vitri,soluongadd);
                                }


                            }else {
                                Toast.makeText(getApplicationContext(),"Đã hết dữ liệu",Toast.LENGTH_LONG).show();
                                isLoading =true;
                            }

                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),"Khong ket noi server", Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    private void AnhXa() { // Liên kết các thành phần giao diện với biến trong lớp.
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_giay);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        mauSanPhamList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}