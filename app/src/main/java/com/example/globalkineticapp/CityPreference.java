package com.example.globalkineticapp;

import android.app.Activity;
import android.content.SharedPreferences;
/*City Preference
Developer: S.M Makura
Date: November 2021
Preference class to choose default city to show
 */
public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Midrand as the default city
    public String getCity(){
        if(prefs != null)
            return prefs.getString("city", "Midrand, ZA");

        return "city_not_initialized";

    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

}