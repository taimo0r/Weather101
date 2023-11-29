package com.taimoor.weather101.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.taimoor.weather101.Activities.DetailWeatherActivity;
import com.taimoor.weather101.Common.Common;
import com.taimoor.weather101.Model.ForecastResponse;
import com.taimoor.weather101.R;

public class ForecastRecyclerAdapter extends RecyclerView.Adapter<ForecastRecyclerAdapter.MyViewHolder> {

    Context context;
    ForecastResponse forecastResponse;
    String tempUnit, windUnit, pressureUnit;

    public ForecastRecyclerAdapter(Context context, ForecastResponse forecastResponse) {
        this.context = context;
        this.forecastResponse = forecastResponse;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_item,parent,false);
        //Setting up units
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        tempUnit = sharedPreferences.getString("key_temp_unit", "");
        pressureUnit = sharedPreferences.getString("key_pressure_unit", "");
        windUnit = sharedPreferences.getString("key_wind_unit", "");

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Load icon
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(forecastResponse.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.weatherIcon);

        holder.date.setText(new StringBuilder(Common.convertUnixToDate(forecastResponse.list.get(position).dt)));
        holder.description.setText(new StringBuilder(forecastResponse.list.get(position).weather.get(0).getDescription().toUpperCase()));
        holder.humidity.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).main.getHumidity())).append("% Humidity"));

        if (tempUnit.equals("celsius")){
            holder.temperature.setText(new StringBuilder(String.valueOf(forecastResponse.list.get(position).main.getTemp())).append("° C"));
        }else if (tempUnit.equals("fahrenheit")){
            holder.temperature.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(forecastResponse.list.get(position).main.getTemp())).append("° F"));
        }else {
            holder.temperature.setText(new StringBuilder(Common.convertCelsiusToKelvin(forecastResponse.list.get(position).main.getTemp())).append("° K"));
        }

        holder.forecastItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailWeatherActivity.class);
                int position = holder.getAdapterPosition();
                String isCalling = "forecast";

                intent.putExtra("isCalling", isCalling);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return forecastResponse.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
       TextView date,description,temperature,humidity;
       ImageView weatherIcon;
       LinearLayout forecastItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.txt_date);
            description = itemView.findViewById(R.id.txt_description);
            temperature = itemView.findViewById(R.id.txt_temperature);
            humidity = itemView.findViewById(R.id.txt_humidity);
            weatherIcon = itemView.findViewById(R.id.img_icon);
            forecastItem = itemView.findViewById(R.id.forecast_item);


        }
    }
}
