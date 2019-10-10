package com.nevilleedwardrichardson.cityweather;

import android.os.Parcelable;

import java.io.Serializable;

public class Weather implements Serializable {

    // Variables and Objects

    private String city;        // Name of city the weather is for
    private String temperature; // Current temperature
    private String description; // Description of weather
    private int id;             // Id to assocate to displaying type of weather for images
    private String lastUpdated; // Date and time of last update
    private String error;       // Any error that occurred getting the data

    // Create a new playing card

    public Weather() {

        ;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getError() {

        if (error == null) {

            error = "";

        }

        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
