package com.nain.cloneuber.providers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nain.cloneuber.R;
import com.nain.cloneuber.retrofit.IGoogleApi;
import com.nain.cloneuber.retrofit.retrofitClient;

import java.util.Date;

import retrofit2.Call;

public class GoogleApiProvider {

    private Context context;

    public GoogleApiProvider(Context context) {
        this.context = context;
    }

    // 60*60*1000 => una hora
    public Call<String> getDirections(LatLng originLntLng, LatLng destinationLntLng) {
        String baseUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                    + "origin=" + originLntLng.latitude + "," + originLntLng.longitude + "&"
                    + "destination=" + destinationLntLng.latitude + "," + destinationLntLng.longitude + "&"
                    + "departure_time=" + (new Date().getTime() + (60*60*1000)) + "&"
                    + "traffic_model=best_guess&"
                    + "key=" + context.getResources().getString(R.string.google_maps_key);

        // pasamos la interfas
        return retrofitClient.getClient(baseUrl).create(IGoogleApi.class).getDirections(baseUrl + query);
    }
}
