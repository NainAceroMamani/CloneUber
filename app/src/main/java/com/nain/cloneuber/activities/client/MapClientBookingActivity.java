package com.nain.cloneuber.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.nain.cloneuber.R;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.GeoFireProvider;

import java.util.ArrayList;
import java.util.List;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private AuthProvider mAtuchProvider;

    private final static int LOCATION_REQUEST_CODE = 1; // saber si deberia solicitar permisos o no del gps

    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker; // marcador para la img

    // para almacenar la positon del cliente
    private LatLng mCurrentLatlng;
    private GeoFireProvider mGeofireProvider;

    // para añadir marcadores de conductores
    private List<Marker> mDriversMarkers = new ArrayList<>();
    private boolean mIsFirstTime = true; // para que solo entre una vez

    // para el buscador
    private PlacesClient mPlaces;

    // para guardar el lugar
    private String mOrigin;         // nombre del lugar
    private LatLng mOriginLatLong;  // latitud y longitud del lugar

    // para guardar el destino
    private String mDestination;         // nombre del lugar
    private LatLng mDestinationLatLong;  // latitud y longitud del lugar


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this); //  es ese fragmento cargamos el mapa de google

        mAtuchProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("drivers_working");

        // buscador
        if(!Places.isInitialized()){
            // saber si no esta inicializado
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key)); // el api key de google
        }
        // instanciamos el buscador
        mPlaces = Places.createClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // habilitar el api de google
        mMap = googleMap;
        // tipo de mapa
        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.estilos_mapa));
        if(!success) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true); // para mostrar el zoom
    }
}
