package com.nain.cloneuber.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.driver.MapDriverBookingActivity;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientBookingProvider;
import com.nain.cloneuber.providers.DriverProvider;
import com.nain.cloneuber.providers.GeoFireProvider;
import com.nain.cloneuber.providers.GoogleApiProvider;
import com.nain.cloneuber.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private AuthProvider mAtuchProvider;

    private Marker mMarkerDriver; // marcador para la img

    private GeoFireProvider mGeofireProvider;

    private boolean mIsFirstTime = true; // para que solo entre una vez

    // para el buscador
    private PlacesClient mPlaces;

    // para guardar el lugar
    private String mOrigin;         // nombre del lugar
    private LatLng mOriginLatLong;  // latitud y longitud del lugar

    // para guardar el destino
    private String mDestination;         // nombre del lugar
    private LatLng mDestinationLatLong;  // latitud y longitud del lugar

    private LatLng mDriverLatLong;  // latitud y longitud del lugar

    private TextView mtextViewClientBooking, mtextViewEmailClientBooking, mtextViewOriginClientBooking, mtextViewDestinationBooking;

    private ClientBookingProvider mclientBookingProvider;

    private GoogleApiProvider mGoogleApiProvider;

    // para decodificar
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolygonOptions;
    private LatLng mOrigenLatLong;

    // para obtener info del dirver
    private DriverProvider mdriverProvider;

    private ValueEventListener mlistener;
    private String mIdDriver; // para contralar los eventos necesitamos el id del driver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this); //  es ese fragmento cargamos el mapa de google

        mAtuchProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("drivers_working");

        mGoogleApiProvider = new GoogleApiProvider(MapClientBookingActivity.this);
        // buscador
        if(!Places.isInitialized()){
            // saber si no esta inicializado
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key)); // el api key de google
        }
        mtextViewClientBooking = findViewById(R.id.textViewDriverBooking);
        mtextViewEmailClientBooking = findViewById(R.id.textViewEmailDriverBooking);
        mtextViewOriginClientBooking = findViewById(R.id.textViewOriginClientBooking);
        mtextViewDestinationBooking = findViewById(R.id.textViewDestinationDriverBooking);

        mdriverProvider = new DriverProvider();

        mclientBookingProvider = new ClientBookingProvider();
        getClientBooking();
    }

    private void getClientBooking(){
        mclientBookingProvider.getClientBooking(mAtuchProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String destino = dataSnapshot.child("destination").getValue().toString();
                    String origen = dataSnapshot.child("origin").getValue().toString();
                    String idDriver = dataSnapshot.child("idDriver").getValue().toString();
                    mIdDriver = idDriver;
                    double destiantionLat = Double.parseDouble(dataSnapshot.child("destinationLat").getValue().toString());
                    double destiantionLng = Double.parseDouble(dataSnapshot.child("destinationLng").getValue().toString());

                    double originLat = Double.parseDouble(dataSnapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(dataSnapshot.child("originLng").getValue().toString());

                    mOrigenLatLong = new LatLng(originLat, originLng);
                    mDestinationLatLong = new LatLng(destiantionLat, destiantionLng);

                    getDriver(idDriver); // pra obtener la info del driver
                    mtextViewOriginClientBooking.setText("Recoger en: " + origen);
                    mtextViewDestinationBooking.setText("Destino: " + destino);
                    mMap.addMarker(new MarkerOptions().position(mOrigenLatLong).title("Recoger Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_red)));
                    getDriverLocation(idDriver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDriver(String idDriver){
        mdriverProvider.getDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    mtextViewClientBooking.setText(name);
                    mtextViewEmailClientBooking.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mlistener != null && mIdDriver != null){
            mGeofireProvider.getDriverLocation(mIdDriver).removeEventListener(mlistener);
        }
    }

    public void getDriverLocation(String idDriver){
        // addValueEventListener escucharemos en tiempo real
        mlistener = mGeofireProvider.getDriverLocation(idDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    double lat = Double.parseDouble(dataSnapshot.child("0").getValue().toString());
                    double lng = Double.parseDouble(dataSnapshot.child("1").getValue().toString());
                    // si ya existe un marcador lo borramos para que nos dibuje en tiempo real
                    mDriverLatLong = new LatLng(lat, lng);
                    if(mMarkerDriver != null) {
                        mMarkerDriver.remove();
                    }
                    // añadimos el marcador de driver
                    mMarkerDriver = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(lat,lng)
                            ).title("Su Conductor")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver))
                    );
                    // solo trazar las ruta una vez
                    if(mIsFirstTime) {
                        mIsFirstTime = false;
                        // para mover la camara
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(mDriverLatLong)
                                        .zoom(17f)
                                        .build()
                        ));
                        drawRoute();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void drawRoute() {
        mGoogleApiProvider.getDirections(mDriverLatLong, mOrigenLatLong).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // ver que te trae el json para comprender => googleAppiProvider
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes"); // string de la propiedad del json
                    JSONObject route = jsonArray.getJSONObject(0); // para obtener todos los datos de la ruta todo el json de esa position
                    JSONObject polylines = route.getJSONObject("overview_polyline"); // position esfecifica del json
                    String poins = polylines.getString("points"); // asi se llama la propiedad del json

                    // ahora esto esta codifico lo decodificamos (esta encryptado)
                    mPolylineList = DecodePoints.decodePoly(poins);

                    // dibujamos la ruta
                    mPolygonOptions = new PolylineOptions();
                    mPolygonOptions.color(Color.WHITE);
                    mPolygonOptions.width(13f);
                    mPolygonOptions.startCap(new SquareCap());
                    mPolygonOptions.jointType(JointType.ROUND);
                    mPolygonOptions.addAll(mPolylineList); // pasar una lista
                    mMap.addPolyline(mPolygonOptions);

                    // datos del json te trae la distancia
                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    // getJSONObject => obtener un ojeto especifico
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");

                    // obtenemos las propiedades del json
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                } catch (Exception e) {
                    Log.d("Error", "Error encontrado " + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
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
