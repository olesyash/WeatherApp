package com.example.olesya.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by olesya on 08-Jan-16.
 */
public class weatherAdapter extends ArrayAdapter<weatherItem> {
    Context context;
    public weatherAdapter(Context context, ArrayList<weatherItem> weathers) {
        super(context, 0, weathers);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        weatherItem weather = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        // Lookup view for data population
        TextView wDate = (TextView) convertView.findViewById(R.id.dateTextView);
        TextView wTime = (TextView) convertView.findViewById(R.id.timeTextView);
        TextView wTemp = (TextView) convertView.findViewById(R.id.tempTextView);
        TextView wDesc = (TextView) convertView.findViewById(R.id.descriptionTextView);
        ImageView wImage = (ImageView) convertView.findViewById(R.id.weatherImageView);

        // Populate the data into the template view using the data object
        wDate.setText(weather.date);
        wTime.setText(weather.time);
        wTemp.setText(weather.temperature);
        wDesc.setText(weather.description);
        Picasso.with(context).load(weather.image).into(wImage);

        // Return the completed view to render on screen
        return convertView;
    }
}

