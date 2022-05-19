package com.duan.travelshare.model;

import android.net.Uri;

public class HinhPhong {
    String idHinh;
    Uri linkHinh;

    public HinhPhong() {
    }

    public HinhPhong(String idHinh, Uri linkHinh) {
        this.idHinh = idHinh;
        this.linkHinh = linkHinh;
    }

    public String getIdHinh() {
        return idHinh;
    }

    public void setIdHinh(String idHinh) {
        this.idHinh = idHinh;
    }

    public Uri getLinkHinh() {
        return linkHinh;
    }

    public void setLinkHinh(Uri linkHinh) {
        this.linkHinh = linkHinh;
    }
}
