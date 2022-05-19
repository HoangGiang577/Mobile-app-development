package com.duan.travelshare.model;

import java.io.Serializable;

public class QuanLiUser implements Serializable {
    private String user,lockUser;

    public QuanLiUser() {
    }

    public QuanLiUser(String user, String lockUser) {
        this.user = user;
        this.lockUser = lockUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLockUser() {
        return lockUser;
    }

    public void setLockUser(String lockUser) {
        this.lockUser = lockUser;
    }
}
