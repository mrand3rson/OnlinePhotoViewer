package com.example.onlinephotoviewer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Andrei on 31.03.2018.
 */

public class Base64Formatter {
    public static String convertToBase64(Bitmap bmp) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        int dimension = 600;
        bmp = ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);

        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] arrayImage = stream.toByteArray();
        return Base64.encodeToString(arrayImage, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String base64) {
        byte[] arrayImage = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(arrayImage, 0, arrayImage.length);
    }
}
