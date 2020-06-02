package com.jorgejag.cuidaportman;

public class Upload {

    private String comment;
    private String imageUrl;

    public Upload() {

    }

    public Upload(String comment, String imageUrl) {

        if (comment.trim().equals("")) {
            comment = "Sin comentario";
        }

        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}