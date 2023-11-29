package com.taimoor.weather101.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static final String APP_ID = "b28d648fa5f1de7f5b51807124e54c7d";
    public static Location current_location = null;

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm EEE dd-MM-yyyy");

        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

    public static String convertUnixToHourAndDay(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm EEE");

        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

    public static String convertUnixToDateAndDay(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM EEE");
        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

    public static String convertCelsiusToFahrenheit(double temp){
        double formatted = (temp * 9/5) + 32;
        String result = String.format("%.2f",formatted);
        return result;

    }

    public static String convertCelsiusToKelvin(double temp){
        double formatted = temp + 273.15;
        String result = String.format("%.2f",formatted);
        return result;
    }

    public static String convertMillibarToAtm(float pressure){
        float formatted = pressure/1013;
        String result = String.format("%.2f", formatted);
        return result;
    }

    public static String convertMillibarToPascal(float pressure){
        float formatted = pressure * 100;
        String result = String.format("%.2f", formatted);
        return result;
    }

    public static String convertMeterPerSecondToKph(double windSpeed){
        double formatted = windSpeed * 3.6;
        String result = String.format("%.2f", formatted);
        return result;
    }

    public static String convertMeterPerSecondToMph(double windSpeed){
        double formatted = windSpeed * 2.237;
        String result = String.format("%.2f", formatted);
        return result;
    }

}
