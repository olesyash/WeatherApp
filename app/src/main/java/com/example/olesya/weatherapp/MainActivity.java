package com.example.olesya.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener{
    //Define variables
    private static final int CURRENT = 9;
    private Helper rq;
    private ListView listView;
    private static final String TAG = "MyDebug", APIKEY ="16d4c9fd39543f0a79094be205a4a130";
    private ArrayList<weatherItem> results;
    private LocationManager locationManager;
    private Location current;
    private LocationListener listener = this;
    private ProgressDialog dialog;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner citiesSpinner;

        citiesSpinner = (Spinner)findViewById(R.id.citiesSpinner);
        citiesSpinner.setSelection(CURRENT); //Set selection to current location
        context = this;
        rq = new Helper();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        rq.createCitiesDictionary(); //Creating dictionary for cities - name: id
        getLocation(); //Use internal function to find out current location

        listView = (ListView) findViewById(R.id.weatherListView);

        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "item selected");
                if (position != CURRENT) {
                    httpRequest(parent.getItemAtPosition(position).toString()); //Get weather by city
                }
                else
                {
                    getLocation(); //Get weather by current location
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            locationManager.removeUpdates(this);}
        catch (SecurityException e){}
    }

    public void httpRequest(String city)
    {
        RequestQueue queue;

        String url = "http://api.openweathermap.org/data/2.5/forecast?units=metric";
        if(city == null) // Get weather by current location
        {
            url += "&lat=" + current.getLatitude() + "&lon="+ current.getLongitude();
        }
        else { //Get weather by city id
            String cityId = rq.getCityId(city);
            url += "&id=" + cityId;
        }
        url += "&appid=" + APIKEY; //add api key
        queue = Volley.newRequestQueue(context); //create request
        dialog.show(); //start progress dialog
        results = new ArrayList<weatherItem>();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.cancel();
                        Log.i(TAG, "got response");
                        results = (ArrayList<weatherItem>)rq.jsonToArray(response); // save results in list of weather objects
                        weatherAdapter wAdapter = new weatherAdapter(context, results);
                        wAdapter.addAll(results);
                        listView.setAdapter(wAdapter); //Show the list
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
                            Log.i(TAG, "could not get message");
                        }
                    }
                }
        );
        request.setTag("REQUEST");
        queue.add(request);
    }

    //Function to find out current location
    private void getLocation()
    {
        dialog.show();
        PermissionManager pm;

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        pm = new PermissionManager(this, new PermissionManager.OnPermissionListener()
        {
            @Override
            public void OnPermissionChanged(boolean permissionGranted) {
                if(permissionGranted)
                {
                    //Get location
                    long minTime = 10000;
                    float minDistance = 100;
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener);
                    }
                    catch (SecurityException e) {
                    }
                }
                else
                {
                    Toast.makeText(context, "Sorry you have no permissions to access location, please add " +
                            "the permissions before using the app",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            current = location;
            httpRequest(null);
            Log.i(TAG, "in location changed, location is" + current);
            locationManager.removeUpdates(this);} //We want to get location once, so stop updates
        catch (SecurityException e){}
    }

    //--------------------------- Unimplemented but must functions  -------------
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
