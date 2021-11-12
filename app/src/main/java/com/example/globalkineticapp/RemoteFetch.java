package com.example.globalkineticapp;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/*Remote Fetch class
Developer: S.M Makura
Date: November 2021
Remote Fetch class which connects to the Openweather API
 */
public class RemoteFetch {
    //String to get weather details based on city
    private static final String OPEN_WEATHER_MAP_API = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    //String to get weather details based on geo location
    private static final String OPEN_WEATHER_MAP_API2 = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    //method to retrieve weather details
    public static JSONObject getJSON(Context context, String city){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder json = new StringBuilder(1024);
           String tmp;
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                return null;
            }
            System.out.println(data);
            return data;

        }catch(Exception e){
            return null;
        }
    }
    //method to retrieve weather details
    public static JSONObject getJSON2(Context context, String lat, String lng){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API2, lat, lng));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder json = new StringBuilder(1024);
            String tmp;
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());
            System.out.println(data);
            if(data.getInt("cod") != 200){
                return null;
            }
            System.out.println(data);
            return data;

        }catch(Exception e){
            return null;
        }
    }

}
