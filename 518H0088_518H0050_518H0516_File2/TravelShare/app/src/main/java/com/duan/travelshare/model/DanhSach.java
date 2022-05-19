package com.duan.travelshare.model;

import java.io.Serializable;

public class DanhSach implements Serializable {
    private String idDanhSach,gmail,idPhong;

    public DanhSach() {
    }

    public DanhSach(String idDanhSach, String gmail, String idPhong) {
        this.idDanhSach = idDanhSach;
        this.gmail = gmail;
        this.idPhong = idPhong;
    }

    public String getIdDanhSach() {
        return idDanhSach;
    }

    public void setIdDanhSach(String idDanhSach) {
        this.idDanhSach = idDanhSach;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(String idPhong) {
        this.idPhong = idPhong;
    }
}
