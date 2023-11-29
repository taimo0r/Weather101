package com.taimoor.weather101.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.taimoor.weather101.Adapter.HourlyForecastAdapter;
import com.taimoor.weather101.Adapter.dailyForecastAdapter;
import com.taimoor.weather101.Common.Common;
import com.taimoor.weather101.Model.HourlyForecastResponse;
import com.taimoor.weather101.Model.WeatherResponse;
import com.taimoor.weather101.R;
import com.taimoor.weather101.Retrofit.ApiClient;
import com.taimoor.weather101.Retrofit.ApiInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class TodayFragment extends Fragment {

    ImageView imageView;
    TextView cityName, humidity, sunset, sunrise, pressure, windSpeed, geoCoords, temperature, dateTime, feelsLike;
    SwipeRefreshLayout refreshLayout;

    LinearLayout weatherPanel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    ApiInterface mService;

    RecyclerView hourlyRecycler, dailyRecycler;

    String location, tempUnit, windUnit, pressureUnit;


    static TodayFragment instance;


    public static TodayFragment getInstance() {

        if (instance == null)
            instance = new TodayFragment();
        return instance;

    }

    public TodayFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = ApiClient.getInstance();
        mService = retrofit.create(ApiInterface.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        //Setting up units
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        tempUnit = sharedPreferences.getString("key_temp_unit", "");
        pressureUnit = sharedPreferences.getString("key_pressure_unit", "");
        windUnit = sharedPreferences.getString("key_wind_unit", "");

        imageView = (ImageView) view.findViewById(R.id.img_weather);
        cityName = (TextView) view.findViewById(R.id.city_name_txt);
        humidity = (TextView) view.findViewById(R.id.humidity_txt);
        sunrise = (TextView) view.findViewById(R.id.sunrise_txt);
        sunset = (TextView) view.findViewById(R.id.sunset_txt);
        pressure = (TextView) view.findViewById(R.id.pressure_txt);
        windSpeed = (TextView) view.findViewById(R.id.wind_txt);
        temperature = (TextView) view.findViewById(R.id.temperature_txt);
        dateTime = (TextView) view.findViewById(R.id.date_time_txt);
        geoCoords = (TextView) view.findViewById(R.id.geo_coord_txt);
        feelsLike = (TextView) view.findViewById(R.id.feel_like_txt);
        weatherPanel = (LinearLayout) view.findViewById(R.id.weather_panel);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Setting up units
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

                tempUnit = sharedPreferences.getString("key_temp_unit", "");
                pressureUnit = sharedPreferences.getString("key_pressure_unit", "");
                windUnit = sharedPreferences.getString("key_wind_unit", "");


                getWeatherData();
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Weather Updated", Toast.LENGTH_SHORT).show();
            }
        });

        hourlyRecycler = view.findViewById(R.id.hourly_recycler);
        hourlyRecycler.setHasFixedSize(true);
        hourlyRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        dailyRecycler = view.findViewById(R.id.daily_recycler);
        dailyRecycler.setHasFixedSize(true);
        dailyRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        getWeatherData();


        return view;
    }

    private void getWeatherData() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude())
                , Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResponse>() {
                    @Override
                    public void accept(WeatherResponse weatherResponse) throws Exception {
                        //Load Image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResponse.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(imageView);

                        //Load Information
                        location = weatherResponse.getName();
                        cityName.setText(weatherResponse.getName());

                        if (tempUnit.equals("celsius")) {
                            temperature.setText(new StringBuilder(String.valueOf(weatherResponse.getMain().getTemp())).append("° C").toString());
                        }else if (tempUnit.equals("fahrenheit")){
                            temperature.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(weatherResponse.getMain().getTemp())).append("° F").toString());
                        }else {
                            temperature.setText(new StringBuilder(Common.convertCelsiusToKelvin(weatherResponse.getMain().getTemp())).append("° K").toString());
                        }

                        if (pressureUnit.equals("mb")){
                            pressure.setText(new StringBuilder(String.valueOf(weatherResponse.getMain().getPressure())).append(" mb").toString());
                        }else if (pressureUnit.equals("pa")){
                            pressure.setText(new StringBuilder(Common.convertMillibarToPascal(weatherResponse.getMain().getPressure())).append(" pa").toString());
                        }else {
                            pressure.setText(new StringBuilder(Common.convertMillibarToAtm(weatherResponse.getMain().getPressure())).append(" atm").toString());
                        }


                        if (windUnit.equals("m/s")) {
                            windSpeed.setText(new StringBuilder("Speed: ").append(weatherResponse.getWind().getSpeed()).append(" m/s, Deg: ").append(weatherResponse.getWind().getDeg()));
                        }else if (windUnit.equals("kph")){
                            windSpeed.setText(new StringBuilder("Speed: ").append(Common.convertMeterPerSecondToKph(weatherResponse.getWind().getSpeed())).append(" kph, Deg: ").append(weatherResponse.getWind().getDeg()));
                        }else {
                            windSpeed.setText(new StringBuilder("Speed: ").append(Common.convertMeterPerSecondToMph(weatherResponse.getWind().getSpeed())).append(" mph, Deg: ").append(weatherResponse.getWind().getDeg()));
                        }

                        if (tempUnit.equals("celsius")) {
                            feelsLike.setText(new StringBuilder(String.valueOf(weatherResponse.getMain().getFeels_like())).append("° C").toString());
                        }else if (tempUnit.equals("fahrenheit")){
                            feelsLike.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(weatherResponse.getMain().getFeels_like())).append("° F").toString());
                        }else {
                            feelsLike.setText(new StringBuilder(Common.convertCelsiusToKelvin(weatherResponse.getMain().getFeels_like())).append("° K").toString());
                        }

                        dateTime.setText(Common.convertUnixToDate(weatherResponse.getDt()));

                        humidity.setText(new StringBuilder(String.valueOf(weatherResponse.getMain().getHumidity())).append("%").toString());
                        sunrise.setText(Common.convertUnixToHour(weatherResponse.getSys().getSunrise()));
                        sunset.setText(Common.convertUnixToHour(weatherResponse.getSys().getSunset()));
                        geoCoords.setText(weatherResponse.getCoord().toString());


                        getHourlyForecast();

                        getDailyForecast();

                        //Display Panel
                        loading.setVisibility(View.GONE);
                        weatherPanel.setVisibility(View.VISIBLE);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

        );

    }

    private void getDailyForecast() {
        compositeDisposable.add(mService.getDailyForecast(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID, "metric", "minutely,hourly")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HourlyForecastResponse>() {
                    @Override
                    public void accept(HourlyForecastResponse hourlyForecastResponse) throws Exception {
                        displayDailyForecast(hourlyForecastResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void displayDailyForecast(HourlyForecastResponse hourlyForecastResponse) {
        dailyForecastAdapter adapter = new dailyForecastAdapter(getContext(), hourlyForecastResponse);
        dailyRecycler.setAdapter(adapter);
    }

    private void getHourlyForecast() {
        compositeDisposable.add(mService.getHourlyForecast(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID, "metric", "minutely,daily")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HourlyForecastResponse>() {
                    @Override
                    public void accept(HourlyForecastResponse hourlyForecastResponse) throws Exception {
                        displayHourlyForecast(hourlyForecastResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    private void displayHourlyForecast(HourlyForecastResponse hourlyForecastResponse) {
        HourlyForecastAdapter adapter = new HourlyForecastAdapter(getContext(), hourlyForecastResponse);
        hourlyRecycler.setAdapter(adapter);
    }


    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

}