package com.taimoor.weather101.Retrofit;

import com.taimoor.weather101.Model.ForecastResponse;
import com.taimoor.weather101.Model.HourlyForecastResponse;
import com.taimoor.weather101.Model.WeatherResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("weather")
    Observable<WeatherResponse> getWeatherByLatLng(@Query("lat") String lat,
                                                   @Query("lon") String lng,
                                                   @Query("appid") String appid,
                                                   @Query("units") String units);

    @GET("forecast")
    Observable<ForecastResponse> getForecastWeatherByLatLng(@Query("lat") String lat,
                                                            @Query("lon") String lng,
                                                            @Query("appid") String appid,
                                                            @Query("units") String units);

    @GET("weather")
    Observable<WeatherResponse> getWeatherByCityName(@Query("q") String cityName,
                                                     @Query("appid") String appid,
                                                     @Query("units") String units);


    @GET("onecall")
    Observable<HourlyForecastResponse> getHourlyForecast(@Query("lat") String lat,
                                                         @Query("lon") String lng,
                                                         @Query("appid") String appid,
                                                         @Query("units") String units,
                                                         @Query("exclude") String exclude);

    @GET("onecall")
    Observable<HourlyForecastResponse> getDailyForecast(@Query("lat") String lat,
                                                         @Query("lon") String lng,
                                                         @Query("appid") String appid,
                                                         @Query("units") String units,
                                                         @Query("exclude") String exclude);


    @GET("weather?appid=b28d648fa5f1de7f5b51807124e54c7d&units=metric")
    Call<WeatherResponse> getWeatherData(@Query("q") String name);
}
