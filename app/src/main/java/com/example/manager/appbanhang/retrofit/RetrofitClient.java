package com.example.manager.appbanhang.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;
    public static Retrofit getInstance(String baseUrl){
        if (instance == null){
            // Khởi tạo đối tượng Gson với cấu hình setLenient
            Gson gson = new GsonBuilder()
                    .setLenient() // Cho phép JSON sai định dạng
                    .create();

            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Sử dụng Gson đã cấu hình
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();


        }
        return instance;
    }
}
