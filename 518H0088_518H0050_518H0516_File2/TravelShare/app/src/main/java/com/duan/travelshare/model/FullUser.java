package com.duan.travelshare.model;

import java.io.Serializable;

public class FullUser implements Serializable {
    private String uID, userName, cmndUser, emailUser, birtdayUser, phoneUser, addressUser, linkImage;

    public FullUser() {
    }

    public FullUser(String uID, String userName, String cmndUser, String emailUser, String birtdayUser, String phoneUser, String addressUser, String linkImage) {
        this.uID = uID;
        this.userName = userName;
        this.cmndUser = cmndUser;
        this.emailUser = emailUser;
        this.birtdayUser = birtdayUser;
        this.phoneUser = phoneUser;
        this.addressUser = addressUser;
        this.linkImage = linkImage;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCmndUser() {
        return cmndUser;
    }

    public void setCmndUser(String cmndUser) {
        this.cmndUser = cmndUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getBirtdayUser() {
        return birtdayUser;
    }

    public void setBirtdayUser(String birtdayUser) {
        this.birtdayUser = birtdayUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public String getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(String addressUser) {
        this.addressUser = addressUser;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }
}
