package com.jorgejag.cuidaportman;

public class Comment {

    private String comment;

    public Comment(String comment) {
        this.comment = comment;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return this.comment;
    }
}
