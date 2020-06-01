package com.jorgejag.cuidaportman;

public class Upload {

    private Comment comment;
    private String imageUrl;

    public Upload() {

    }

    public Upload(Comment comment, String imageUrl) {
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
