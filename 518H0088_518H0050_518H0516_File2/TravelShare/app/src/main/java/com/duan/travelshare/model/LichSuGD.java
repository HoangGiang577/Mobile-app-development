package com.duan.travelshare.model;

import java.io.Serializable;

public class LichSuGD implements Serializable {
    private String idGD,date,gmail,idPhong,statusGD;

    public LichSuGD() {
    }

    public LichSuGD(String idGD, String date, String gmail, String idPhong, String statusGD) {
        this.idGD = idGD;
        this.date = date;
        this.gmail = gmail;
        this.idPhong = idPhong;
        this.statusGD = statusGD;
    }

    public String getIdGD() {
        return idGD;
    }

    public void setIdGD(String idGD) {
        this.idGD = idGD;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getStatusGD() {
        return statusGD;
    }

    public void setStatusGD(String statusGD) {
        this.statusGD = statusGD;
    }
}
