package com.taimoor.weather101.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.taimoor.weather101.Common.Common;
import com.taimoor.weather101.Activities.DetailWeatherActivity;
import com.taimoor.weather101.Model.HourlyForecastResponse;
import com.taimoor.weather101.R;

public class dailyForecastAdapter extends RecyclerView.Adapter<dailyForecastAdapter.ViewHolder> {
    Context context;
    HourlyForecastResponse forecastResponse;
    String tempUnit, windUnit, pressureUnit;


    public dailyForecastAdapter(Context context, HourlyForecastResponse forecastResponse) {
        this.context = context;
        this.forecastResponse = forecastResponse;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_forecast_item,parent,false);

        //Setting up units
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        tempUnit = sharedPreferences.getString("key_temp_unit", "");
        pressureUnit = sharedPreferences.getString("key_pressure_unit", "");
        windUnit = sharedPreferences.getString("key_wind_unit", "");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Load Icon
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(forecastResponse.daily.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.weatherIcon);

        holder.date.setText(new StringBuilder(Common.convertUnixToDateAndDay(forecastResponse.daily.get(position).getDt())));

        if (tempUnit.equals("celsius")) {
            holder.temp.setText(new StringBuilder(String.valueOf(forecastResponse.daily.get(position).getTemp().min)).append("° /").append(forecastResponse.daily.get(position).getTemp().max).append("° C"));
        }else if (tempUnit.equals("fahrenheit")){
            holder.temp.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(forecastResponse.daily.get(position).getTemp().min)).append("° /").append(Common.convertCelsiusToFahrenheit(forecastResponse.daily.get(position).getTemp().min)).append("° F"));
        }else {
            holder.temp.setText(new StringBuilder(Common.convertCelsiusToKelvin(forecastResponse.daily.get(position).getTemp().min)).append("° /").append(Common.convertCelsiusToKelvin(forecastResponse.daily.get(position).getTemp().min)).append("° K"));
        }


        holder.recyclerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String date = Common.convertUnixToDateAndDay(forecastResponse.daily.get(position).getDt());
                String isCalling = "Daily";

                Intent intent = new Intent(context, DetailWeatherActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("isCalling", isCalling);

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return forecastResponse.daily.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date,temp;
        ImageView weatherIcon;
        ConstraintLayout recyclerItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_daily_txt);
            temp = itemView.findViewById(R.id.temp_daily_txt);
            weatherIcon = itemView.findViewById(R.id.img_weather_daily);
            recyclerItem = itemView.findViewById(R.id.recycler_layout);

        }
    }
}
