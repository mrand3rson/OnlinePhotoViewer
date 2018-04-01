package com.example.onlinephotoviewer.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Andrei on 30.03.2018.
 */

public class ApiImageOut extends RealmObject implements Parcelable {
    public int getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getUrl() {
        return url;
    }

    @PrimaryKey
    private int id;
    private int date;
    private double lat;
    private double lng;
    private String url;


    public ApiImageOut() {

    }

    public ApiImageOut(int date, int id, double lat, double lng, String url) {
        this.date = date;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.url = url;
    }

    private ApiImageOut(Parcel parcel) {
        date = parcel.readInt();
        id = parcel.readInt();
        lat = parcel.readDouble();
        lng = parcel.readDouble();
        url = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(date);
        parcel.writeInt(id);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeString(url);
    }

    public static final Parcelable.Creator<ApiImageOut> CREATOR = new Parcelable.Creator<ApiImageOut>() {
        public ApiImageOut createFromParcel(Parcel in) {
            return new ApiImageOut(in);
        }

        public ApiImageOut[] newArray(int size) {
            return new ApiImageOut[size];
        }
    };
}
//    date (integer),
//    id (integer),
//    lat (number),
//    lng (number),
//    url (string)