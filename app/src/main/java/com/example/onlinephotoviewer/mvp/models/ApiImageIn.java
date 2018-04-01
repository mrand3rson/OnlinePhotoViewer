package com.example.onlinephotoviewer.mvp.models;

/**
 * Created by Andrei on 30.03.2018.
 */

public class ApiImageIn {
    private String base64Image;
    private int date;
    private double lat;
    private double lng;

    public ApiImageIn(String base64Image, int date, double lat, double lng) {
        this.base64Image = base64Image;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
    }
}
