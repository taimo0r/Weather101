package com.taimoor.weather101.Adapter;

import android.content.Context;
import android.hardware.lights.LightState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taimoor.weather101.Database.Locations;
import com.taimoor.weather101.Model.SavedLocationsModel;
import com.taimoor.weather101.R;
import com.taimoor.weather101.onTextClickListener;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationRecycler extends RecyclerView.Adapter<SavedLocationRecycler.ViewHolder> {

    Context context;
    private List<Locations> locations;
    onTextClickListener listener;

    public SavedLocationRecycler(Context context, List<Locations> locations, onTextClickListener listener) {
        this.context = context;
        this.locations = locations;
        this.listener = listener;
    }

    public void setLocationsList(List<Locations> Savedlocations){
      this.locations = Savedlocations;
      notifyDataSetChanged();
  }

    @NonNull
    @Override
    public SavedLocationRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_location_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedLocationRecycler.ViewHolder holder, int position) {
        Locations location = locations.get(position);

        holder.locationName.setText(location.LocationName);
        double latitude = location.latitude;
        double longitude = location.longitude;
        String cityName = location.LocationName;

        holder.locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTextClick(latitude,longitude,cityName);

            }
        });

    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView locationName, locationTemperature;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            locationName = itemView.findViewById(R.id.saved_location_name);

        }
    }
}
