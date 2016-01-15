package com.example.olesya.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener{
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
        citiesSpinner.setSelection(CURRENT);
        context = this;
        rq = new Helper();
        dialog = new ProgressDialog(context);
        rq.createCitiesDictionary();
        getLocation();

        listView = (ListView) findViewById(R.id.weatherListView);

        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "item selected");
                if (position != CURRENT) {
                    httpRequest(parent.getItemAtPosition(position).toString());
                }
                else
                {
                    getLocation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            locationManager.removeUpdates(this);}
        catch (SecurityException e){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void httpRequest(String city)
    {
        RequestQueue queue;

        String server_addr = "http://api.openweathermap.org/data/2.5/forecast?units=metric";
        if(city == null)
        {
            server_addr += "&lat=" + current.getLatitude() + "&lon="+ current.getLongitude();
        }
        else {
            String cityId = rq.getCityId(city);
            server_addr += "&id=" + cityId;
        }
        server_addr += "&appid=" + APIKEY;
        queue = Volley.newRequestQueue(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        results = new ArrayList<weatherItem>();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                server_addr,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.cancel();
                        Log.i(TAG, "got response");
                        results = (ArrayList<weatherItem>)rq.jsonToArray(response);
                        weatherAdapter wAdapter= new weatherAdapter(context, results);
                        wAdapter.addAll(results);
                        listView.setAdapter(wAdapter);
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

    public void getLocation()
    {
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
                    dialog.show();
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener);
                    }
                    catch (SecurityException e) {
                    }
                }
                else
                {
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
            locationManager.removeUpdates(this);}
        catch (SecurityException e){}
    }

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
