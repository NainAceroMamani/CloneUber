package com.nain.cloneuber.activities.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.includes.MyToolbar;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientProvider;
import com.nain.cloneuber.providers.GeoFireProvider;
import com.nain.cloneuber.providers.TokenProvider;

public class MapDriverBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private AuthProvider mAtuchProvider;

    // propiedades del gps
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation; // para poder iniciar o detener la ubicación
    private final static int LOCATION_REQUEST_CODE = 1; // saber si deberia solicitar permisos o no del gps

    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker; // marcador para la img

    // para almacenar la positon del conductor
    private LatLng mCurrentLatlng;
    private GeoFireProvider mGeofireProvider;

    //escuchara cada vez que el usuario se mueva
    LocationCallback mLocationCallback = new LocationCallback() {
        // sebreescribimos un método
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // contexto de la aplicación
            for(Location location: locationResult.getLocations()) {
                if(getApplicationContext() != null) {

                    mCurrentLatlng = new LatLng(location.getLatitude(), location.getLongitude());

                    // eliminamos el marcador anterior para que no se repita
                    if(mMarker != null) {
                        mMarker.remove();
                    }
                    // añadimos el marcador de driver
                    mMarker = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            ).title("Su posición")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver))
                    );
                    // obtenemos la localización del usuario en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    // posición actual
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(17f)
                                    .build()
                    ));
                    updateLocation();
                }
            }
        }

    };

    private TextView mtextViewClientBooking;
    private TextView mtextViewEmailClientBooking;

    private String mExtraClientId; // para obtener el extra que se esta pasando del AcceptReciver
    private ClientProvider mClientProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver_booking);

        // instanciamos el mFusedLocation
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this); // para poder iniciar o detener la ubicación

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this); //  es ese fragmento cargamos el mapa de google

        mtextViewClientBooking = findViewById(R.id.textViewClientBooking);
        mtextViewEmailClientBooking = findViewById(R.id.textViewEmailClientBooking);

        mAtuchProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("drivers_working");

        mExtraClientId = getIntent().getStringExtra("idClient");
        mClientProvider = new ClientProvider();
        getClientBooking();
    }

    private void getClientBooking() {
        // este metodo addListenerForSingleValueEvent porque solo obtendre la info del usuario una un ica vez y  no en tiempo real
        mClientProvider.getClient(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String email = dataSnapshot.child("email").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    mtextViewClientBooking.setText(name);
                    mtextViewEmailClientBooking.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // guardar la ubicación del driver
    private void updateLocation(){
        // preguntamos si existe la sesion
        if(mAtuchProvider.exitSesion() && mCurrentLatlng != null) {
            mGeofireProvider.savaLocation(mAtuchProvider.getId(), mCurrentLatlng);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // habilitar el api de google
        mMap = googleMap;
        // tipo de mapa
        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.estilos_mapa));
        if(!success) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true); // para mostrar el zoom

        // instaciamos el gps
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); // tiempo en que se estara actualizando la ubicación del usuario
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // para que use el gps con la mayor presión
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();
    }

    // permisos de la aplicación
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE) {
            // preguntar si consedio los permisos
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // preguntar si consedio los permisos
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    // preguntamos si tiene activado su gps
                    if(gpsActived()) {
                        // obtenemos la ubicación actualizada y en tiempo real
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()); // Looper.myLooper() => devuelve el hilo actual
                        mMap.setMyLocationEnabled(true); // ubicacion exacta => cargamos el icono
                    }else {
                        // mostramos el alert Dialog
                        showAlertDialogGPS();
                    }
                } else {
                    checkLocationPermission();
                }
            } else {
                checkLocationPermission();
            }
        }
    }

    // sobreescribimos este metodo -- para que el usuario active su gps cuando inicia su aplicación
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            // significa que si activo su gps
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()); // activamos la posición
            mMap.setMyLocationEnabled(true); // ubicacion exacta => cargamos el icono
        }else {
            // mostramos el alert Dialog
            showAlertDialogGPS();
        }
    }

    // mostrar alert dialog para que vaya a la configuracion para que lo active
    private void showAlertDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicación para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // esperara hasta que el usuario realize una acción -- espara asta que l usuario active el gps
                        // esto se mostrara siempre hasta que active su gps
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    // conocer si el usuario tiene o no gps actuvado
    private boolean gpsActived() {
        // si tiene el gps activado return true
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return isActive;
    }

    // metodo para desconectar gps
    private void disconnect(){
        if(mFusedLocation != null) {
            mFusedLocation.removeLocationUpdates(mLocationCallback); // deja de escuchar los eventos el mLocationCallback

            // eliminamos la posición de la base de datos => validar si existe sesión
            if(mAtuchProvider.exitSesion()) {
                mGeofireProvider.removeLocation(mAtuchProvider.getId());
            }
        } else {
            // significa que no tiene activado el gps o esta sin permisos
            Toast.makeText(this, R.string.txt_error_gps_disconect, Toast.LENGTH_SHORT).show();
        }
    }

    // metodo para el escuchador de nuestra aplicación => lo ejecutamos en el onMapReady
    private void startLocation(){
        // verificamos que la version de android sea mayor a machMelon
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // preguntar si los permisson ya estan consedidos
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                // preguntamos si tiene activado su gps
                if(gpsActived()) {
                    // obtenemos la ubicación actualizada y en tiempo real
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()); // Looper.myLooper() => devuelve el hilo actual
                    mMap.setMyLocationEnabled(true); // ubicacion exacta => cargamos el icono
                }else {
                    // mostramos el alert Dialog
                    showAlertDialogGPS();
                }
            }else {
                // si no estan los permisos consedidos llamamos al Dialog
                checkLocationPermission();
            }

        }else {
            // preguntamos si tiene activado su gps
            if(gpsActived()) {
                // obtenemos la ubicación actualizada y en tiempo real
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()); // Looper.myLooper() => devuelve el hilo actual
                mMap.setMyLocationEnabled(true); // ubicacion exacta => cargamos el icono
            }else {
                // mostramos el alert Dialog
                showAlertDialogGPS();
            }
        }

    }

    // metodo pra validar en caso de que el usuario no acepte los permisos
    private void checkLocationPermission(){
        // preguntar si el usuario no consedio los permisos
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // si denegaste permissos
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // creamos una alerta
                new AlertDialog.Builder(this)
                        .setTitle(R.string.txt_error_location)
                        .setMessage(R.string.txt_message_permission)
                        // creamos un boton con un eventLisener
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // habilitamos los permisos para poder utilizarlo en el celular
                                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[] {
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                }, LOCATION_REQUEST_CODE );
                            }
                        })
                        .create()
                        .show();
            } else {
                // habilitamos los permisos para poder utilizarlo en el celular
                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_REQUEST_CODE );
            }
        }
    }
}
