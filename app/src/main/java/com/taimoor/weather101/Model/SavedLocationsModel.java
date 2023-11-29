package com.taimoor.weather101.Model;

public class SavedLocationsModel {

    String locationName;
    String locationTemperature;


    public SavedLocationsModel(String locationName, String locationTemperature) {
        this.locationName = locationName;
        this.locationTemperature = locationTemperature;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationTemperature() {
        return locationTemperature;
    }

    public void setLocationTemperature(String locationTemperature) {
        this.locationTemperature = locationTemperature;
    }
}


