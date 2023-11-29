package com.taimoor.weather101.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.taimoor.weather101.Adapter.ForecastRecyclerAdapter;
import com.taimoor.weather101.Common.Common;
import com.taimoor.weather101.Model.ForecastResponse;
import com.taimoor.weather101.R;
import com.taimoor.weather101.Retrofit.ApiClient;
import com.taimoor.weather101.Retrofit.ApiInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ForecastFragment extends Fragment {

    static ForecastFragment instance;
    CompositeDisposable compositeDisposable;
    ApiInterface mService;

    TextView txtCityName,geoCoord;
    RecyclerView forecastRecycler;

    String tempUnit, windUnit, pressureUnit;



    public static ForecastFragment getInstance(){
        if (instance==null){
            instance = new ForecastFragment();
        }
        return instance;
    }

    public ForecastFragment(){
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = ApiClient.getInstance();
        mService = retrofit.create(ApiInterface.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        //Setting up Units of measurement
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        tempUnit = sharedPreferences.getString("key_temp_unit", "");
        pressureUnit = sharedPreferences.getString("key_pressure_unit", "");
        windUnit = sharedPreferences.getString("key_wind_unit", "");

        txtCityName = view.findViewById(R.id.txt_city_name);
        geoCoord = view.findViewById(R.id.txt_geo_coord);

        forecastRecycler = view.findViewById(R.id.forecast_recycler);
        forecastRecycler.setHasFixedSize(true);
        forecastRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

        getForecast();


        return view;
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

    private void getForecast() {
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
                        displayWeatherForecast(forecastResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("Error", ""+ throwable.getMessage());
                    }
                })
        );
    }

    private void displayWeatherForecast(ForecastResponse forecastResponse) {
        txtCityName.setText(new StringBuilder(forecastResponse.city.name));
        geoCoord.setText(new StringBuilder(forecastResponse.city.coord.toString()));

        ForecastRecyclerAdapter adapter = new ForecastRecyclerAdapter(getContext(),forecastResponse);
        forecastRecycler.setAdapter(adapter);
    }


}