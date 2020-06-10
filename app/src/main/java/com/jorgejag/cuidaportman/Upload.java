package com.jorgejag.cuidaportman;

public class Upload {

    private String comment;
    private String imageUrl;
    private String timestamp;

    public Upload() {

    }

    public Upload(String comment, String imageUrl) {
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    public Upload(String comment, String imageUrl, String timestamp) {

        if (comment.trim().equals("")) {
            comment = "Sin comentario";
        }

        this.comment = comment;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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