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
import com.taimoor.weather101.Common.Common;
import com.taimoor.weather101.Activities.DetailWeatherActivity;
import com.taimoor.weather101.Model.HourlyForecastResponse;
import com.taimoor.weather101.R;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    Context context;
    HourlyForecastResponse forecastResponse;
    String tempUnit, windUnit, pressureUnit;

    public HourlyForecastAdapter(Context context, HourlyForecastResponse forecastResponse) {
        this.context = context;
        this.forecastResponse = forecastResponse;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hourly_forecast_item,parent,false);
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
                .append(forecastResponse.hourly.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.weatherIcon);

        holder.date.setText(new StringBuilder(Common.convertUnixToHour(forecastResponse.hourly.get(position).getDt())));
        holder.description.setText(new StringBuilder(forecastResponse.hourly.get(position).weather.get(0).getDescription()));

        if (tempUnit.equals("celsius")) {
            holder.temp.setText(new StringBuilder(String.valueOf(forecastResponse.hourly.get(position).getTemp())).append("° C"));
        }else if (tempUnit.equals("fahrenheit")){
            holder.temp.setText(new StringBuilder(Common.convertCelsiusToFahrenheit(forecastResponse.hourly.get(position).getTemp())).append("° F"));
        }else {
            holder.temp.setText(new StringBuilder(Common.convertCelsiusToKelvin(forecastResponse.hourly.get(position).getTemp())).append("° K"));
        }

        holder.hourlyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context, DetailWeatherActivity.class);
                String isCalling = "Hourly";

                intent.putExtra("isCalling",isCalling);
                intent.putExtra("position",position);

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return forecastResponse.hourly.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date,description,temp;
        ImageView weatherIcon;
        LinearLayout hourlyItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.txt_date_hourly);
            description = itemView.findViewById(R.id.txt_description_hourly);
            temp = itemView.findViewById(R.id.txt_temperature_hourly);
            weatherIcon = itemView.findViewById(R.id.img_icon_hourly);
            hourlyItem = itemView.findViewById(R.id.hourly_recycler_item);

        }
    }
}
