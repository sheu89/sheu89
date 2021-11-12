package com.example.globalkineticapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/*Weather Fragment class
Developer: S.M Makura
Date: November 2021
Weather Fragment class responsible for variety of things mainly getting the weather details of the current location of user
 */

public class WeatherFragment extends Fragment implements LocationListener {

    Typeface weatherFont;

    private TextView cityField;
    private TextView weatherIcon;
    private TextView updatedField;
    private TextView currentTemperatureField;
    private TextView detailsField;
    private Fragment frag;
    private Button getloc;
    LocationManager locationManager;
    String provider;
    static double lng, lat;

    int MY_PERMISSION = 0;

    Handler handler;


    public WeatherFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_weather_fragment, container, false);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        getloc = (Button)rootView.findViewById(R.id.buttonFragment);
        weatherIcon.setTypeface(weatherFont);
        getloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        return rootView;


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getting coordinates
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_PERMISSION);
        }

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());

    }

    //get current location of user
    void getLocation() {
           try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                lat = locationGPS.getLatitude();
                 lng = locationGPS.getLongitude();
                String latitude = String.valueOf(lat);
                String longitude = String.valueOf(lng);
                //call method to get weather info for current location
                getWeatherDataForCurrent(latitude, longitude);
            } else {
                Toast.makeText(getActivity(), "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }

        catch(SecurityException e) {
            e.printStackTrace();
        }


    }

    //method to get weather info of current location
    private void getWeatherDataForCurrent(final String lat, final String longit){
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "Getting Weather Information...", "Please wait...", true);

        new Thread(){
            public void run(){

                final JSONObject json = RemoteFetch.getJSON2(getActivity(), lat, longit);
               // System.out.println(json);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }}, 2000);
                        }

                    });
                } else {

                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                        }}, 2000);
                }
            }
        }.start();
    }
    //method to get weather info of specified city
    private void updateWeatherData(final String city){
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "Getting Weather Information...", "Please wait...", true);

        new Thread(){
            public void run(){
             final JSONObject json = RemoteFetch.getJSON(getActivity(), city);

             if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });

                 handler.postDelayed(new Runnable() {
                     public void run() {
                         dialog.dismiss();
                     }}, 2000);
                }
            }
        }.start();

    }

    //method to render Weather data to display
    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);


        }catch(Exception e){
            Log.e("SimpleWeather", "Field not present in JSON Received");
        }
    }

    //method to set weather icons
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        updateWeatherData(city);
    }


    //method to get location should it change
    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        Log.i(TAG, String.valueOf(location.getLatitude()));
        Log.i(TAG, String.valueOf(location.getLongitude()));


    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();

    }
}