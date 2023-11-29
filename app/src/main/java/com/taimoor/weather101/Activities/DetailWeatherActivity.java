package com.taimoor.weather101.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.taimoor.weather101.Common.Common;
import com.taimoor.weather101.Model.ForecastResponse;
import com.taimoor.weather101.Model.HourlyForecastResponse;
import com.taimoor.weather101.Model.WeatherResponse;
import com.taimoor.weather101.R;
import com.taimoor.weather101.Retrofit.ApiClient;
import com.taimoor.weather101.Retrofit.ApiInterface;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DetailWeatherActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ConstraintLayout detailWeatherPanel;
    TextView pressure, humidity, dewPoint, uvIndex, feelsLike, windSpeed, cityName, date, temperature, MinTemperature, MaxTemperature, feelLikeTxt, descTxt, minTxt, maxTxt, feelLikeTxt_forecast, dewPointTxt;
    CircleImageView feelLikeImg, descImg, feelLikeImg_forecast,dewPointImg;
    CompositeDisposable compositeDisposable;
    ApiInterface mService;
    String tempUnit, windUnit,pressureUnit;


    HourlyForecastResponse forecastResponse;

    int position;
    String isCalling =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_weather);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Setting up units
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        tempUnit = sharedPreferences.getString("key_temp_unit", "");
        pressureUnit = sharedPreferences.getString("key_pressure_unit", "");
        windUnit = sharedPreferences.getString("key_wind_unit", "");


        progressBar = findViewById(R.id.progress_bar);
        detailWeatherPanel = findViewById(R.id.detail_weather);
        pressure = findViewById(R.id.pressure_detail);
        humidity = findViewById(R.id.humidity_detail);
        dewPoint = findViewById(R.id.due_point_detail);
        uvIndex = findViewById(R.id.uv_index_detail);
        feelsLike = findViewById(R.id.feels_like_detail);
        windSpeed = findViewById(R.id.wind_speed_detail);
        cityName = findViewById(R.id.location_detail);
        date = findViewById(R.id.date_detail);
        MinTemperature = findViewById(R.id.min_temperature_detail);
        MaxTemperature = findViewById(R.id.max_temperature_detail);
        temperature = findViewById(R.id.temperature_detail);
        descImg = findViewById(R.id.desc_img);
        descTxt = findViewById(R.id.desc_txt);
        minTxt = findViewById(R.id.textView);
        maxTxt = findViewById(R.id.textView2);
        feelLikeTxt = findViewById(R.id.feelLike_txt);
        feelLikeImg = findViewById(R.id.feelLike_img);
        feelLikeImg_forecast = findViewById(R.id.feelLike_img_forecast);
        feelLikeTxt_forecast = findViewById(R.id.feelLike_txt_forecast);
        dewPointImg = findViewById(R.id.circleImageView6);
        dewPointTxt = findViewById(R.id.textView13);

        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = ApiClient.getInstance();
        mService = retrofit.create(ApiInterface.class);

        loadCityName();


        Bundle bundle = getIntent().getExtras();
        isCalling = bundle.getString("isCalling");
        position = bundle.getInt("position");

        if (isCalling.equals("Hourly")){
            MinTemperature.setVisibility(View.GONE);
            MaxTemperature.setVisibility(View.GONE);
            descImg.setVisibility(View.GONE);
            descTxt.setVisibility(View.GONE);
            minTxt.setVisibility(View.GONE);
            maxTxt.setVisibility(View.GONE);

            temperature.setVisibility(View.VISIBLE);
            feelLikeImg.setVisibility(View.VISIBLE);
            feelLikeTxt.setVisibility(View.VISIBLE);

            loadHourlyDetail();

        }else if (isCalling.equals("Daily")){
            loadDailyDetail();

        }else {
            dewPointTxt.setVisibility(View.GONE);
            dewPointImg.setVisibility(View.GONE);
            feelLikeTxt_forecast.setVisibility(View.VISIBLE);
            feelLikeImg_forecast.setVisibility(View.VISIBLE);

            loadForecastDetail();
        }

    }

    private void loadForecastDetail() {
            compositeDisposable.add(mService.getForecastWeatherByLatLng(
                    String.valueOf(Common.current_location.getLatitude()),
                    String.valueOf(Common.current_location.getLongitude()),
                    Common.APP_ID,
                    "metric")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ForecastResponse>() {
                        @Override
                        public void accept(ForecastResponse forecastResponse) throws Exception {
                            //Load Icon
                            Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                    .append(forecastResponse.list.get(position).weather.get(0).getIcon())
                                    .append(".png").toString()).into(feelLikeImg_forecast);

                            if (tempUnit.equals("celsius")){
                                MinTemperature.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).getMain().getTemp_min())).append("° C"));
                                MaxTemperature.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).getMain().getTemp_max())).append("° C"));
                                feelLikeTxt_forecast.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).getMain().getFeels_like())).append("° C"));
                            }else if (tempUnit.equals("fahrenheit")){
                                MinTemperature.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(forecastResponse.list.get(position).getMain().getTemp_min())).append("° F"));
                                MaxTemperature.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(forecastResponse.list.get(position).getMain().getTemp_max())).append("° F"));
                                feelLikeTxt_forecast.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(forecastResponse.list.get(position).getMain().getFeels_like())).append("° F"));
                            }else {
                                MinTemperature.setText(new StringBuilder(Common.convertCelsiusToKelvin(forecastResponse.list.get(position).getMain().getTemp_min())).append("° K"));
                                MaxTemperature.setText(new StringBuilder(Common.convertCelsiusToKelvin(forecastResponse.list.get(position).getMain().getTemp_max())).append("° K"));
                                feelLikeTxt_forecast.setText(new StringBuilder(Common.convertCelsiusToKelvin(forecastResponse.list.get(position).getMain().getFeels_like())).append("° K"));
                            }

                            if (pressureUnit.equals("mb")){
                                pressure.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).getMain().getPressure())).append(" mb"));
                            }else if (pressureUnit.equals("pa")){
                                pressure.setText(new StringBuilder(Common.convertMillibarToPascal(forecastResponse.list.get(position).getMain().getPressure())).append(" pa"));
                            }else {
                                pressure.setText(new StringBuilder(Common.convertMillibarToAtm(forecastResponse.list.get(position).getMain().getPressure())).append(" atm"));
                            }

                            if (windUnit.equals("m/s")){
                                windSpeed.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).getWind().getSpeed())).append(" m/s"));
                            }else if (windUnit.equals("kph")){
                                windSpeed.setText(new StringBuilder(Common.convertMeterPerSecondToKph(forecastResponse.list.get(position).getWind().getSpeed())).append(" kph"));
                            }else{
                                windSpeed.setText(new StringBuilder(Common.convertMeterPerSecondToMph(forecastResponse.list.get(position).getWind().getSpeed())).append(" mph"));
                            }


                            date.setText(new StringBuilder(Common.convertUnixToDate(forecastResponse.list.get(position).getDt())));
                            humidity.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).getMain().getHumidity())).append("%"));
                            feelsLike.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).getWeather().get(0).getMain())));
                            progressBar.setVisibility(View.GONE);
                            detailWeatherPanel.setVisibility(View.VISIBLE);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(DetailWeatherActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
            );

    }

    private void loadHourlyDetail() {
        compositeDisposable.add(mService.getDailyForecast(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric",
                "minutely,daily")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HourlyForecastResponse>() {
                    @Override
                    public void accept(HourlyForecastResponse hourlyForecastResponse) throws Exception {

                        if (tempUnit.equals("celsius")){
                            temperature.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.hourly.get(position).getTemp())).append("° C"));
                            feelsLike.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.hourly.get(position).getFeels_like())).append("° C"));
                        }else if (tempUnit.equals("fahrenheit")){
                            temperature.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(hourlyForecastResponse.hourly.get(position).getTemp())).append("° F"));
                            feelsLike.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(hourlyForecastResponse.hourly.get(position).getFeels_like())).append("° F"));
                        }else{
                            temperature.setText(new StringBuilder(Common.convertCelsiusToKelvin(hourlyForecastResponse.hourly.get(position).getTemp())).append("° K"));
                            feelsLike.setText(new StringBuilder(Common.convertCelsiusToKelvin(hourlyForecastResponse.hourly.get(position).getFeels_like())).append("° K"));
                        }


                        if (pressureUnit.equals("mb")){
                            pressure.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.hourly.get(position).getPressure())).append(" mb"));
                        }else if (pressureUnit.equals("pa")){
                            pressure.setText(new StringBuilder(Common.convertMillibarToPascal(hourlyForecastResponse.hourly.get(position).getPressure())).append(" pa"));
                        }else{
                            pressure.setText(new StringBuilder(Common.convertMillibarToAtm(hourlyForecastResponse.hourly.get(position).getPressure())).append(" atm"));
                        }


                        if (windUnit.equals("m/s")){
                            windSpeed.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.hourly.get(position).getWind_speed())).append(" m/s"));
                        }else if (windUnit.equals("kph")){
                            windSpeed.setText(new StringBuilder(Common.convertMeterPerSecondToKph(hourlyForecastResponse.hourly.get(position).getWind_speed())).append(" kph"));
                        }else{
                            windSpeed.setText(new StringBuilder(Common.convertMeterPerSecondToMph(hourlyForecastResponse.hourly.get(position).getWind_speed())).append(" mph"));
                        }


                        date.setText(new StringBuilder(Common.convertUnixToHourAndDay(hourlyForecastResponse.hourly.get(position).getDt())));
                        humidity.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.hourly.get(position).getHumidity())).append("%"));
                        uvIndex.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.hourly.get(position).getUvi())).append(" of 10"));
                        dewPoint.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.hourly.get(position).getDew_point())).append("°"));
                        progressBar.setVisibility(View.GONE);
                        detailWeatherPanel.setVisibility(View.VISIBLE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(DetailWeatherActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadCityName() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude())
                , Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResponse>() {
                    @Override
                    public void accept(WeatherResponse weatherResponse) throws Exception {
                        cityName.setText(weatherResponse.getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(DetailWeatherActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadDailyDetail() {

        compositeDisposable.add(mService.getDailyForecast(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric",
                "minutely,hourly")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HourlyForecastResponse>() {
                    @Override
                    public void accept(HourlyForecastResponse hourlyForecastResponse) throws Exception {

                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(hourlyForecastResponse.daily.get(position).weather.get(0).getIcon())
                                .append(".png").toString()).into(descImg);

                        if (tempUnit.equals("celsius")){
                            MinTemperature.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.daily.get(position).getTemp().getMin())).append("° C"));
                            MaxTemperature.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.daily.get(position).getTemp().getMax())).append("° C"));
                        }else if (tempUnit.equals("fahrenheit")){
                            MinTemperature.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(hourlyForecastResponse.daily.get(position).getTemp().getMin())).append("° F"));
                            MaxTemperature.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(hourlyForecastResponse.daily.get(position).getTemp().getMax())).append("° F"));
                        }else{
                            MinTemperature.setText(new StringBuilder(Common.convertCelsiusToKelvin(hourlyForecastResponse.daily.get(position).getTemp().getMin())).append("° K"));
                            MaxTemperature.setText(new StringBuilder(Common.convertCelsiusToKelvin(hourlyForecastResponse.daily.get(position).getTemp().getMax())).append("° K"));
                        }


                        if (pressureUnit.equals("mb")){
                            pressure.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.daily.get(position).getPressure())).append(" mb"));
                        }else if (pressureUnit.equals("pa")){
                            pressure.setText(new StringBuilder(Common.convertMillibarToPascal(hourlyForecastResponse.daily.get(position).getPressure())).append(" pa"));
                        }else{
                            pressure.setText(new StringBuilder(Common.convertMillibarToAtm(hourlyForecastResponse.daily.get(position).getPressure())).append(" atm"));
                        }


                        if (windUnit.equals("m/s")){
                            windSpeed.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.daily.get(position).getWind_speed())).append(" m/s"));
                        }else if (windUnit.equals("kph")){
                            windSpeed.setText(new StringBuilder(Common.convertMeterPerSecondToKph(hourlyForecastResponse.daily.get(position).getWind_speed())).append(" kph"));
                        }else{
                            windSpeed.setText(new StringBuilder(Common.convertMeterPerSecondToMph(hourlyForecastResponse.daily.get(position).getWind_speed())).append(" Mph"));
                        }

                        date.setText(new StringBuilder(Common.convertUnixToDateAndDay(hourlyForecastResponse.daily.get(position).getDt())));
                      feelsLike.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.daily.get(position).getWeather().get(0).getMain())));
                        humidity.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.daily.get(position).getHumidity())).append("%"));
                        uvIndex.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.daily.get(position).getUvi())).append(" of 10"));
                        dewPoint.setText(new StringBuilder(String.valueOf(hourlyForecastResponse.daily.get(position).getDew_point())).append("°"));

                        progressBar.setVisibility(View.GONE);
                        detailWeatherPanel.setVisibility(View.VISIBLE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(DetailWeatherActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}