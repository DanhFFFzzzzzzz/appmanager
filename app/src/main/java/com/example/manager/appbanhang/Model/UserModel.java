package com.example.manager.appbanhang.Model;

import java.util.List;

public class UserModel {
    private boolean success;
    private String message;
    private List<User> result;

    // Getter cho success
    public boolean isSuccess() {
        return success;
    }

    // Setter cho success
    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Getter cho message
    public String getMessage() {
        return message;
    }

    // Setter cho message
    public void setMessage(String message) {
        this.message = message;
    }

    // Getter cho result (danh sách User)
    public List<User> getResult() {
        return result;
    }

    // Setter cho result
    public void setResult(List<User> result) {
        this.result = result;
    }

    // Phương thức toString() để hiển thị thông tin UserModel
    @Override
    public String toString() {
        return "UserModel{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
