package com.duan.travelshare.model;

import java.io.Serializable;

public class ChiTietChuChoThue implements Serializable {
    private String gmail, nameCCT, addressCCT, birthdayCCT, phoneCCT;

    public ChiTietChuChoThue() {
    }

    public ChiTietChuChoThue(String gmail, String nameCCT, String addressCCT, String birthdayCCT, String phoneCCT) {
        this.gmail = gmail;
        this.nameCCT = nameCCT;
        this.addressCCT = addressCCT;
        this.birthdayCCT = birthdayCCT;
        this.phoneCCT = phoneCCT;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getNameCCT() {
        return nameCCT;
    }

    public void setNameCCT(String nameCCT) {
        this.nameCCT = nameCCT;
    }

    public String getAddressCCT() {
        return addressCCT;
    }

    public void setAddressCCT(String addressCCT) {
        this.addressCCT = addressCCT;
    }

    public String getBirthdayCCT() {
        return birthdayCCT;
    }

    public void setBirthdayCCT(String birthdayCCT) {
        this.birthdayCCT = birthdayCCT;
    }

    public String getPhoneCCT() {
        return phoneCCT;
    }

    public void setPhoneCCT(String phoneCCT) {
        this.phoneCCT = phoneCCT;
    }
}

