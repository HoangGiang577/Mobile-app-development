package com.duan.travelshare.model;

import java.io.Serializable;

public class User implements Serializable {
    private String uID, email, password, loaiUser, token, active;

    public User() {
    }

    public User(String uID, String email, String password, String loaiUser, String token, String active) {
        this.uID = uID;
        this.email = email;
        this.password = password;
        this.loaiUser = loaiUser;
        this.token = token;
        this.active = active;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoaiUser() {
        return loaiUser;
    }

    public void setLoaiUser(String loaiUser) {
        this.loaiUser = loaiUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
