package com.duan.travelshare.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ChiTietPhong implements Serializable {
    private String idPhong, tenPhong, giaPhong, diaChiPhong, moTaPhong;
    ArrayList<String> imgPhong;
    String uID;

    public ChiTietPhong() {
    }

    public ChiTietPhong(String idPhong, String tenPhong, String giaPhong, String diaChiPhong, String moTaPhong, ArrayList<String> imgPhong, String uID) {
        this.idPhong = idPhong;
        this.tenPhong = tenPhong;
        this.giaPhong = giaPhong;
        this.diaChiPhong = diaChiPhong;
        this.moTaPhong = moTaPhong;
        this.imgPhong = imgPhong;
        this.uID = uID;
    }

    public String getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(String idPhong) {
        this.idPhong = idPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getGiaPhong() {
        return giaPhong;
    }

    public void setGiaPhong(String giaPhong) {
        this.giaPhong = giaPhong;
    }

    public String getDiaChiPhong() {
        return diaChiPhong;
    }

    public void setDiaChiPhong(String diaChiPhong) {
        this.diaChiPhong = diaChiPhong;
    }

    public String getMoTaPhong() {
        return moTaPhong;
    }

    public void setMoTaPhong(String moTaPhong) {
        this.moTaPhong = moTaPhong;
    }

    public ArrayList<String> getImgPhong() {
        return imgPhong;
    }

    public void setImgPhong(ArrayList<String> imgPhong) {
        this.imgPhong = imgPhong;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}

