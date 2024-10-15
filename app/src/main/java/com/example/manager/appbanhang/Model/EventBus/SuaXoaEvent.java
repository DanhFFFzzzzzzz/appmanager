package com.example.manager.appbanhang.Model.EventBus;

import com.example.manager.appbanhang.Adapter.MauSanPhamAdapter;
import com.example.manager.appbanhang.Model.MauSanPham;

public class SuaXoaEvent {
    MauSanPham mauSanPham;

    public SuaXoaEvent(MauSanPham mauSanPham) {
        this.mauSanPham = mauSanPham;
    }

    public MauSanPham getMauSanPham() {
        return mauSanPham;
    }

    public void setMauSanPham(MauSanPham mauSanPham) {
        this.mauSanPham = mauSanPham;
    }
}
