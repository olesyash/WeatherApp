package com.example.olesya.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by olesya on 08-Jan-16.
 */
public class httpRequest {

    private RequestQueue queue;
    private Context context;
    private static final String TAG = "MyDebug", APIKEY ="16d4c9fd39543f0a79094be205a4a130";
    private Map<String, String> dictionary;

    public httpRequest(Context context)
    {
        this.context = context;
        createCitiesDictionary();
    }

    private void createCitiesDictionary()
    {
        dictionary = new HashMap<String, String>();
        dictionary.put("Jerusalem", "281184");
        dictionary.put("Tel Aviv", "293397");
        dictionary.put("Haifa", "294801");
        dictionary.put("Ekaterinburg", "1486209");
        dictionary.put("Berlin", "2950158");
        dictionary.put("Stockholm", "2673730");
        dictionary.put("Kathmandu", "1283240");
        dictionary.put("Pokhara", "1282898");
        dictionary.put("Amsterdam", "2759794");
    }
    public String getCityId(String city)
    {
        Log.i(TAG, "in get city, city id is" + dictionary.get(city));
        return dictionary.get(city);
    }
    public void http_request(String city)
    {
        String cityId = getCityId(city);
        String server_addr = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityId;
        server_addr += "&appid="+ APIKEY;
        queue = Volley.newRequestQueue(context);
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                server_addr,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String s = response.getString("city");
                            Log.i(TAG, "response is: " + s);
//                            JSONArray array = response.getJSONArray("value");
//                            JSONObject obj = array.getJSONObject(0);
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
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Got error in response error listener");
                        try {
                            Log.i(TAG, error.getMessage());
                        }
                        catch (Exception e)
                        {
                            Log.i(TAG, "coould not get message");
                        }
                    }
                }
        );
        dialog.cancel();
        request.setTag("REQUEST");
        queue.add(request);

    }
}
