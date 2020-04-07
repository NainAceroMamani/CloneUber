package com.nain.cloneuber.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeoFireProvider {

    private DatabaseReference mDatabase;
    private GeoFire mGeofire;

    public GeoFireProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("active_drivers");
        mGeofire = new GeoFire(mDatabase);
    }

    // guardamos la unicación
    public void savaLocation(String idDriver, LatLng latLng) {
        mGeofire.setLocation(idDriver, new GeoLocation(latLng.latitude, latLng.longitude));
    }

    // eliminr ubicación de la bd de fireabse
    public void removeLocation(String idDriver){
        mGeofire.removeLocation(idDriver);
    }

    // todos los conductores disponibles de firebase => lanLng del cliente => pasamos por parametro el radiu de busqueda
    public GeoQuery getActionsDrivers(LatLng latLng, double radius) {
        GeoQuery geoQuery = mGeofire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius); // radio a 5 kilometros
        geoQuery.removeAllListeners();
        return geoQuery;
    }

}