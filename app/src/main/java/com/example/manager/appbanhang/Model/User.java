package com.example.manager.appbanhang.Model;

public class User {
    private int id;
    private String email;
    private String pass;
    private String username;
    private String sodienthoai;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Constructor không tham số
    public User() {
    }

    // Constructor có tham số
    public User(int id, String email, String pass, String username, String sodienthoai, String uid) {
        this.id = id;
        this.email = email;
        this.pass = pass;
        this.username = username;
        this.sodienthoai = sodienthoai;
        this.uid = uid;
    }

    // Getter và Setter cho id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter và Setter cho email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và Setter cho pass
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    // Getter và Setter cho username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter và Setter cho sodienthoai
    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    // Phương thức toString() để hiển thị thông tin người dùng
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", pass='" + pass + '\'' +
                ", username='" + username + '\'' +
                ", sodienthoai='" + sodienthoai + '\'' +
                '}';
    }
}
