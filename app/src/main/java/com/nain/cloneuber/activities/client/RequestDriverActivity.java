package com.nain.cloneuber.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.models.ClientBooking;
import com.nain.cloneuber.models.FCMBody;
import com.nain.cloneuber.models.FCMResponse;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientBookingProvider;
import com.nain.cloneuber.providers.GeoFireProvider;
import com.nain.cloneuber.providers.GoogleApiProvider;
import com.nain.cloneuber.providers.NotificationProvider;
import com.nain.cloneuber.providers.TokenProvider;
import com.nain.cloneuber.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDriverActivity extends AppCompatActivity {

    private LottieAnimationView mAnimation;
    private TextView mTexViewLookingFor;
    private Button mButtonCancelRequest;

    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private LatLng mOriginLatLng;

    private double mRadius = 0.1;
    private boolean mDriverFound = false; // para saber si ya se encontro un conductor
    private String mIdDriverFound = "";
    private LatLng mDriverLatLng; // almacenara la latitud y longitud del driver

    private GeoFireProvider mGeoFireProvider;

    // para enviar notificaciones
    private NotificationProvider notificationProvider;

    // para obtener el token a travez de un id
    private TokenProvider tokenProvider;

    // para almacenar la info del client en firebase
    private ClientBookingProvider clientBookingProvider;
    private AuthProvider authProvider;

    private String mExtraOrigin;
    private String mExtraDestination;

    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private LatLng mDestinationLatLng;
    private GoogleApiProvider mGoogleApiProvider;

    // para el escuchador
    private ValueEventListener mlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);

        mAnimation = findViewById(R.id.animation);
        mTexViewLookingFor = findViewById(R.id.textViewLookingFor);
        mButtonCancelRequest = findViewById(R.id.btncancelRequest);

        mAnimation.playAnimation();

        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        clientBookingProvider = new ClientBookingProvider();
        authProvider = new AuthProvider();

        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng", 0);
        mDestinationLatLng = new LatLng(mExtraDestinationLat,mExtraDestinationLng);

        mGeoFireProvider = new GeoFireProvider("active_drivers");
        tokenProvider = new TokenProvider();

        notificationProvider = new NotificationProvider();

        mGoogleApiProvider = new GoogleApiProvider(RequestDriverActivity.this);
        getclosesDriver();
    }

    private void getclosesDriver() {
        mGeoFireProvider.getActionsDrivers(mOriginLatLng, mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!mDriverFound) {
                    mDriverFound = true;
                    mIdDriverFound = key; // este metodo te trae el id
                    mDriverLatLng = new LatLng(location.latitude, location.longitude);
                    mTexViewLookingFor.setText(R.string.txt_driver_encontrado);
                    CreateClientBooking(); // alamcenamos la notificacion y enviamos la notificacion
//                    Log.d("DRIVER", "ID: " + mIdDriverFound);
                    return;
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            // INRESA CUANDO TERMINA LA BUSQUEDA DEL CONDUCTOR EN EL RADIO DE 0.1 KILOMETROS
            @Override
            public void onGeoQueryReady() {
                if(!mDriverFound) {
                    mRadius = mRadius + 0.1f;

                    if(mRadius > 5) { // si el radio es mayor a 5 km entonces acava este metodo
                        mTexViewLookingFor.setText(R.string.txt_not_driver);
                        Toast.makeText(RequestDriverActivity.this, R.string.txt_not_driver, Toast.LENGTH_LONG).show();
                        // no encontro ningun conductor
                        return;
                    }else {
                        // si no que vuelva a ejecutar este metodo
                        getclosesDriver();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void CreateClientBooking(){
        // posicion de del tiempo y distancia del usuario al conductor
        mGoogleApiProvider.getDirections(mOriginLatLng, mDriverLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // ver que te trae el json para comprender => googleAppiProvider
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes"); // string de la propiedad del json
                    JSONObject route = jsonArray.getJSONObject(0); // para obtener todos los datos de la ruta todo el json de esa position
                    JSONObject polylines = route.getJSONObject("overview_polyline"); // position esfecifica del json
                    String poins = polylines.getString("points"); // asi se llama la propiedad del json

                    // datos del json te trae la distancia
                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    // getJSONObject => obtener un ojeto especifico
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");

                    // obtenemos las propiedades del json
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");
                    senNotification(durationText, distanceText);

                } catch (Exception e) {
                    Log.d("Error", "Error encontrado " + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void senNotification(final String time, final String km) {
        tokenProvider.getTokens(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            // contiene la informacion del user que esta dentro del nodo
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    // verificamos que si venga porque sino se cierra el App

                    String token  = dataSnapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>(); // en un mapa de string puedes mandar varios valores
                    map.put("title","SOLICITUD DE SERVICIO A " + time + " DE TU POSICIÓN ");
                    map.put("body","Un cliente esta solicitando un servicio a una distacnia de " + km + "\n" +
                            "Recoger en: " + mExtraOrigin + "\n" +
                            "Destino: " + mExtraDestination);
                    map.put("idClient", authProvider.getId());
                    map.put("origin", mExtraOrigin);
                    map.put("destination", mExtraDestination);
                    map.put("min", time);
                    map.put("distance", km);
                    FCMBody fcmBody= new FCMBody(token, "high","4500s",map);
                    notificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            // respuesta del servidor
                            if(response.body() != null){
                                if(response.body().getSuccess() == 1) {
                                    // almacenamos en la base de datos de fireabse los datos
                                    ClientBooking clientBooking = new ClientBooking(
                                            authProvider.getId(),
                                            mIdDriverFound,
                                            mExtraDestination,
                                            mExtraOrigin,
                                            time,
                                            km,
                                            "create",
                                            mExtraOriginLat,
                                            mExtraOriginLng,
                                            mExtraDestinationLat,
                                            mExtraDestinationLng
                                    );

                                    clientBookingProvider.create(clientBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                         // este sera un escuchador que cuando se realize algun cambio en la database ejecutara un accion
                                         checkStatusClientBooking();
                                        }
                                    });

                                    // Toast.makeText(RequestDriverActivity.this, R.string.txt_send_notification, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(RequestDriverActivity.this, R.string.txt_not_notification, Toast.LENGTH_LONG).show();
                                }
                            }else {
                                // si no trae información
                                Toast.makeText(RequestDriverActivity.this, R.string.txt_not_notification, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            // en caso de rror en la peticion
                            Log.d("Error", "Error: " + t.getMessage());
                        }
                    });
                }else {
                    Toast.makeText(RequestDriverActivity.this, R.string.txt_not_notification, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkStatusClientBooking(){
        // funciona para obtener la ubicacion en tiempo real => esto se ejecutara infinitamente com el mListener lo contralamos
        mlistener = clientBookingProvider.getstatus(authProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // verificamos si existe los datos en fireabse database
                if(dataSnapshot.exists()) {
                    String status = dataSnapshot.getValue().toString(); // defrente getValue por que en el provider ya apuntamos al status
                    if (status.equals("accept")) {
                        Intent intent = new Intent(RequestDriverActivity.this, MapClientBookingActivity.class);
                        startActivity(intent);
                        finish(); // para que esta actividad finalize => para que no podamos volver hacia atras
                    }
                    else if(status.equals("cancel")){
                        Toast.makeText(RequestDriverActivity.this, R.string.txt_not_Accept_travel_driver, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RequestDriverActivity.this, MapClientActivity.class);
                        startActivity(intent);
                        finish(); // para que esta actividad finalize => para que no podamos volver hacia atras
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // se ejecuta cuando finalizamos una actividad
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // para que pare el escuchador => para que no se quede escuchando los cambios
        if(mlistener != null){
            clientBookingProvider.getstatus(authProvider.getId()).removeEventListener(mlistener);
        }
    }
}
