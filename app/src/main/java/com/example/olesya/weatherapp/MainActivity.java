package com.example.olesya.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Spinner citiesSpinner;
    private static final int CURRENT = 9;
    private Helper rq;
    private ArrayAdapter arrayAdapter;
    private ArrayList<weatherItem> list;
    private ListView listView;
    private RequestQueue queue;
    private static final String TAG = "MyDebug", APIKEY ="16d4c9fd39543f0a79094be205a4a130";
    private Map<String, String> dictionary;
    private ArrayList<weatherItem> results;
    private boolean gotResponse;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        citiesSpinner = (Spinner)findViewById(R.id.citiesSpinner);
        citiesSpinner.setSelection(CURRENT);
        rq = new Helper(this);
        context = this;

        listView = (ListView) findViewById(R.id.weatherListView);

        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "item selected");
                if(position != CURRENT) {
                    http_request(parent.getItemAtPosition(position).toString());

                   // indexes =
                   // arrayAdapter = new ArrayAdapter(this, R.layout.item_weather, indexes);
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

    public void http_request(String city)
    {
        String cityId = rq.getCityId(city);
        String server_addr = "http://api.openweathermap.org/data/2.5/forecast?units=metric&id=" + cityId;
        server_addr += "&appid="+ APIKEY;
        queue = Volley.newRequestQueue(context);
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        results = new ArrayList<weatherItem>();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                server_addr,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.cancel();
                        Log.i(TAG, "got response");
                        gotResponse = true;
                        results = rq.jsonToArray(response);
                        Log.i(TAG, "list" + results);
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

}
