package com.duan.travelshare.model;

import java.io.Serializable;

public class Save implements Serializable {
    private String uID, idPhong;

    public Save() {
    }

    public Save(String uID, String idPhong) {
        this.uID = uID;
        this.idPhong = idPhong;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(String idPhong) {
        this.idPhong = idPhong;
    }
}
