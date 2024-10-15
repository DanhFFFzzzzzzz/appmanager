package com.example.manager.appbanhang.utils;

import com.example.manager.appbanhang.Model.GioHang;
import com.example.manager.appbanhang.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String BASE_URL="http://192.168.1.4/banhang/";
    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static User user_current = new User();
}
