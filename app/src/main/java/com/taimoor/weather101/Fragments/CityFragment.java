package com.taimoor.weather101.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.taimoor.weather101.Adapter.SavedLocationRecycler;
import com.taimoor.weather101.Common.Common;
import com.taimoor.weather101.Database.AppDatabase;
import com.taimoor.weather101.Database.Locations;
import com.taimoor.weather101.Model.SavedLocationsModel;
import com.taimoor.weather101.Model.WeatherResponse;
import com.taimoor.weather101.R;
import com.taimoor.weather101.Retrofit.ApiClient;
import com.taimoor.weather101.Retrofit.ApiInterface;
import com.taimoor.weather101.onTextClickListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CityFragment extends Fragment implements onTextClickListener {

    private List<String> citiesList;
    private MaterialSearchBar searchBar;
    String tempUnit, windUnit, pressureUnit, city, coordinates, savedLocationTemp;
    double lat,lon;

    ImageView imageView;
    TextView cityName, humidity, sunset, sunrise, pressure, windSpeed, description, geoCoords, temperature, dateTime;

    Button saveLocation;
    Spinner spinner;

    RecyclerView recyclerView;
    SavedLocationRecycler adapter;
    LinearLayout weatherPanel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    ApiInterface mService;

    static CityFragment instance;



    public static CityFragment getInstance() {

        if (instance == null)
            instance = new CityFragment();
        return instance;

    }

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = ApiClient.getInstance();
        mService = retrofit.create(ApiInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_city, container, false);

        //Setting up units
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        tempUnit = sharedPreferences.getString("key_temp_unit", "");
        pressureUnit = sharedPreferences.getString("key_pressure_unit", "");
        windUnit = sharedPreferences.getString("key_wind_unit", "");

        imageView = view.findViewById(R.id.img_weather);
        cityName = view.findViewById(R.id.city_name_txt);
        humidity = view.findViewById(R.id.humidity_txt);
        sunrise = view.findViewById(R.id.sunrise_txt);
        sunset = view.findViewById(R.id.sunset_txt);
        pressure = view.findViewById(R.id.pressure_txt);
        windSpeed = view.findViewById(R.id.wind_txt);
        description = view.findViewById(R.id.description_txt);
        temperature = view.findViewById(R.id.temperature_txt);
        dateTime = view.findViewById(R.id.date_time_txt);
        geoCoords = view.findViewById(R.id.geo_coord_txt);
        weatherPanel = view.findViewById(R.id.weather_panel);
        saveLocation = view.findViewById(R.id.save_city_btn);
        loading = view.findViewById(R.id.loading);
        recyclerView = view.findViewById(R.id.saved_location_recycler);


        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);


        new LoadCities().execute();

        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveNewLocation(city, lat, lon);

           }
        });

        return view;
    }

    private void saveNewLocation(String city, double lat, double lon) {

        AppDatabase db = AppDatabase.getInstance(this.requireContext());

        Locations locations = new Locations();
        locations.LocationName = city;
        locations.latitude = lat;
        locations.longitude = lon;

        db.locationDao().saveLocation(locations);

        Toast.makeText(getContext(), "Location Saved: " + city, Toast.LENGTH_SHORT).show();

        loadLocationList();
    }

    private void loadLocationList() {

        AppDatabase db = AppDatabase.getInstance(this.requireContext());
        List<Locations> allLocations = db.locationDao().getAllLocations();


        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SavedLocationRecycler(requireContext(),allLocations,this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onTextClick(double lat, double lon,String name) {

    }


    private class LoadCities extends SimpleAsyncTask<List<String>> {

        @Override
        protected List<String> doInBackgroundSimple() {

            citiesList = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String readed;
                while ((readed = in.readLine()) != null) {
                    builder.append(readed);
                    citiesList = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>() {
                    }.getType());

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return citiesList;
        }

        @Override
        protected void onSuccess(List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for (String search : listCity) {
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);

                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);
            loading.setVisibility(View.GONE);


        }
    }

    private void getWeatherInformation(String city_name) {
        compositeDisposable.add(mService.getWeatherByCityName(city_name,
                        Common.APP_ID,
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
                        if (tempUnit.equals("celsius")) {
                            temperature.setText(new StringBuilder(String.valueOf(weatherResponse.getMain().getTemp())).append("° C").toString());
                            savedLocationTemp = new StringBuilder(String.valueOf(weatherResponse.getMain().getTemp())).append("° C").toString();
                        } else if (tempUnit.equals("fahrenheit")) {
                            temperature.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(weatherResponse.getMain().getTemp())).append("° F").toString());
                            savedLocationTemp = new StringBuilder(Common.convertCelsiusToFahrenheit(weatherResponse.getMain().getTemp())).append("° F").toString();
                        } else {
                            temperature.setText(new StringBuilder(Common.convertCelsiusToKelvin(weatherResponse.getMain().getTemp())).append("° K").toString());
                            savedLocationTemp = new StringBuilder(Common.convertCelsiusToKelvin(weatherResponse.getMain().getTemp())).append("° K").toString();
                        }

                        if (pressureUnit.equals("mb")) {
                            pressure.setText(new StringBuilder(String.valueOf(weatherResponse.getMain().getPressure())).append(" mb").toString());
                        } else if (pressureUnit.equals("pa")) {
                            pressure.setText(new StringBuilder(Common.convertMillibarToPascal(weatherResponse.getMain().getPressure())).append(" pa").toString());
                        } else {
                            pressure.setText(new StringBuilder(Common.convertMillibarToAtm(weatherResponse.getMain().getPressure())).append(" atm").toString());
                        }


                        if (windUnit.equals("m/s")) {
                            windSpeed.setText(new StringBuilder("Speed: ").append(weatherResponse.getWind().getSpeed()).append(" m/s, Deg: ").append(weatherResponse.getWind().getDeg()));
                        } else if (windUnit.equals("kph")) {
                            windSpeed.setText(new StringBuilder("Speed: ").append(Common.convertMeterPerSecondToKph(weatherResponse.getWind().getSpeed())).append(" kph, Deg: ").append(weatherResponse.getWind().getDeg()));
                        } else {
                            windSpeed.setText(new StringBuilder("Speed: ").append(Common.convertMeterPerSecondToMph(weatherResponse.getWind().getSpeed())).append(" mph, Deg: ").append(weatherResponse.getWind().getDeg()));
                        }

                        city = weatherResponse.getName();
                        cityName.setText(weatherResponse.getName());
                        description.setText(new StringBuilder("Weather in ").append(weatherResponse.getName()).toString());
                        dateTime.setText(Common.convertUnixToDate(weatherResponse.getDt()));
                        humidity.setText(new StringBuilder(String.valueOf(weatherResponse.getMain().getHumidity())).append("%").toString());
                        sunrise.setText(Common.convertUnixToHour(weatherResponse.getSys().getSunrise()));
                        sunset.setText(Common.convertUnixToHour(weatherResponse.getSys().getSunset()));
                        coordinates = weatherResponse.getCoord().toString();
                        lat = weatherResponse.getCoord().getLat();
                        lon = weatherResponse.getCoord().getLon();
                        geoCoords.setText(new StringBuilder(weatherResponse.getCoord().toString()).toString());

                        //Display Panel
                        weatherPanel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

        );
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