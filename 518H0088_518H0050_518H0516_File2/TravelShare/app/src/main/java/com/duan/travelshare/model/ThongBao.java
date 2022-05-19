package com.duan.travelshare.model;

import java.io.Serializable;

public class ThongBao implements Serializable {
    private String idGG, idPhong,  idUser, idChu;
    private String ngay, thoiGian, trangThai;

    public ThongBao() {
    }

    public ThongBao(String idGG, String idPhong, String idUser, String idChu,  String ngay, String thoiGian,String trangThai) {
        this.idGG = idGG;
        this.idPhong = idPhong;
        this.idUser = idUser;
        this.idChu = idChu;
        this.trangThai = trangThai;
        this.ngay = ngay;
        this.thoiGian = thoiGian;
    }

    public String getIdGG() {
        return idGG;
    }

    public void setIdGG(String idGG) {
        this.idGG = idGG;
    }

    public String getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(String idPhong) {
        this.idPhong = idPhong;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdChu() {
        return idChu;
    }

    public void setIdChu(String idChu) {
        this.idChu = idChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }
}
