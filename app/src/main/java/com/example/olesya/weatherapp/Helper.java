package com.example.olesya.weatherapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by olesya on 08-Jan-16.
 */
public class Helper {

    private RequestQueue queue;
    private Context context;
    private static final String TAG = "MyDebug", APIKEY ="16d4c9fd39543f0a79094be205a4a130";
    private Map<String, String> dictionary;
    private ArrayList<weatherItem> results;
    private boolean gotResponse;

    public Helper(Context context)
    {
        this.context = context;
        createCitiesDictionary();
        gotResponse = false;
    }

    private void createCitiesDictionary()
    {
        dictionary = new HashMap<String, String>();
        dictionary.put("Jerusalem, IL", "281184");
        dictionary.put("Tel Aviv, IL", "293397");
        dictionary.put("Haifa, IL", "294801");
        dictionary.put("Ekaterinburg, Russia", "1486209");
        dictionary.put("Berlin, DE", "2950158");
        dictionary.put("Stockholm, SE", "2673730");
        dictionary.put("Kathmandu, Nepal", "1283240");
        dictionary.put("Pokhara, Nepal", "1282898");
        dictionary.put("Amsterdam, NL", "2759794");
    }
    public String getCityId(String city)
    {
        Log.i(TAG, "in get city, city id is" + dictionary.get(city));
        return dictionary.get(city);
    }

    public ArrayList jsonToArray(JSONObject json)
    {
        int i;
        String server = "http://openweathermap.org/img/w/";
        ArrayList<weatherItem> indexes = new ArrayList<weatherItem>();
        Log.i(TAG, "in jsonToarray");
        try {

            JSONArray list = json.getJSONArray("list");
            for(i=0;i<list.length();i++) {
                Log.i(TAG, "in jsonToarray, for loop");
                JSONObject obj = list.getJSONObject(i);
                String dt = obj.getString("dt_txt");
                String date = dt.split(" ")[0];
                String time = dt.split(" ")[1];
                JSONObject obj2 = obj.getJSONObject("main");
                String temp = obj2.getString("temp");
                JSONArray list2 = obj.getJSONArray("weather");
                String desc = list2.getJSONObject(0).getString("description");
                String url = server + list2.getJSONObject(0).getString("icon");

                weatherItem item = new weatherItem(date, time, temp+"C", desc, url);
                indexes.add(item);
            }
        }
        catch (JSONException e)
        {
            try{
                Log.i(TAG, e.getMessage());
            }
            catch (Exception ex)
            {
                Log.i(TAG, "error accured on parsing response");
            }
        }
        return indexes;
    }


}
