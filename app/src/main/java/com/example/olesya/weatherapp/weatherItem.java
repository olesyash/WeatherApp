package com.example.olesya.weatherapp;

/**
 * Created by olesya on 08-Jan-16.
 */
public class weatherItem {
    private static final String TAG = "MyDebug";
    public String date, time, temperature, description, image;

    public weatherItem(String d, String t, String tp, String ds, String i)
    {
        this.date = d;
        this.time = t;
        this.temperature = tp;
        this.description = ds;
        this.image = i;
    }

}
