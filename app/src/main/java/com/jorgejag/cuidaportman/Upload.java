package com.jorgejag.cuidaportman;

public class Upload {

    private String coment;
    private String imageUrl;

    public Upload() {

    }

    public Upload(String coment, String imageUrl) {

        if (coment.trim().equals("")) {
            coment = "Sin comentario";
        }

        this.coment = coment;
        this.imageUrl = imageUrl;
    }

    public String getComent() {
        return coment;
    }

    public void setComent(String coment) {
        this.coment = coment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}