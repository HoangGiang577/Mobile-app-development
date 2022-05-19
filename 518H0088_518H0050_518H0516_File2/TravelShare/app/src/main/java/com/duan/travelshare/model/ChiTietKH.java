package com.duan.travelshare.model;

import java.io.Serializable;

public class ChiTietKH implements Serializable {
    private String gmail,nameKH,addressKH,latMapKH,longMapKH,brithdayKH,phoneKH;

    public ChiTietKH() {
    }

    public ChiTietKH(String gmail, String nameKH, String addressKH, String latMapKH, String longMapKH, String brithdayKH, String phoneKH) {
        this.gmail = gmail;
        this.nameKH = nameKH;
        this.addressKH = addressKH;
        this.latMapKH = latMapKH;
        this.longMapKH = longMapKH;
        this.brithdayKH = brithdayKH;
        this.phoneKH = phoneKH;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getNameKH() {
        return nameKH;
    }

    public void setNameKH(String nameKH) {
        this.nameKH = nameKH;
    }

    public String getAddressKH() {
        return addressKH;
    }

    public void setAddressKH(String addressKH) {
        this.addressKH = addressKH;
    }

    public String getLatMapKH() {
        return latMapKH;
    }

    public void setLatMapKH(String latMapKH) {
        this.latMapKH = latMapKH;
    }

    public String getLongMapKH() {
        return longMapKH;
    }

    public void setLongMapKH(String longMapKH) {
        this.longMapKH = longMapKH;
    }

    public String getBrithdayKH() {
        return brithdayKH;
    }

    public void setBrithdayKH(String brithdayKH) {
        this.brithdayKH = brithdayKH;
    }

    public String getPhoneKH() {
        return phoneKH;
    }

    public void setPhoneKH(String phoneKH) {
        this.phoneKH = phoneKH;
    }
}
