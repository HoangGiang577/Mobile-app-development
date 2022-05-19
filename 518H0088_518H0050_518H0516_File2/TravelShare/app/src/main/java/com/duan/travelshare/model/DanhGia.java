package com.duan.travelshare.model;

import java.io.Serializable;

public class DanhGia implements Serializable {
    private String idComment,star,comment;

    public DanhGia() {
    }

    public DanhGia(String idComment, String star, String comment) {
        this.idComment = idComment;
        this.star = star;
        this.comment = comment;
    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
