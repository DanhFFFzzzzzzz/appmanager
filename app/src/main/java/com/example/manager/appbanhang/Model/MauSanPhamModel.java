package com.example.manager.appbanhang.Model;

import java.util.List;

public class MauSanPhamModel {
    boolean success;
    String message;
    List<MauSanPham> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MauSanPham> getResult() {
        return result;
    }

    public void setResult(List<MauSanPham> result) {
        this.result = result;
    }
}
