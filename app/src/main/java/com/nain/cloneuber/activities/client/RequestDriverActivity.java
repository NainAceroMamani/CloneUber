package com.nain.cloneuber.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.models.FCMBody;
import com.nain.cloneuber.models.FCMResponse;
import com.nain.cloneuber.providers.GeoFireProvider;
import com.nain.cloneuber.providers.NotificationProvider;
import com.nain.cloneuber.providers.TokenProvider;

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
    private LatLng mDriverLatLng; // almacenara la latitud y longitud del usuario

    private GeoFireProvider mGeoFireProvider;

    // para enviar notificaciones
    private NotificationProvider notificationProvider;

    // para obtener el token a travez de un id
    private TokenProvider tokenProvider;

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

        mGeoFireProvider = new GeoFireProvider();
        tokenProvider = new TokenProvider();

        notificationProvider = new NotificationProvider();

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
                    senNotification(); // enviamos la notificacion
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

    private void senNotification() {
        tokenProvider.getTokens(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            // contiene la informacion del user que esta dentro del nodo
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String token  = dataSnapshot.child("token").getValue().toString();
                Map<String, String> map = new HashMap<>();
                map.put("title","SOLICITUD DE SERVICIO");
                map.put("body","Un cliente esta solicitando un servicio");
                FCMBody fcmBody= new FCMBody(token, "high",map);
                notificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        // respuesta del servidor
                        if(response.body() != null){
                            if(response.body().getSuccess() == 1) {
                                Toast.makeText(RequestDriverActivity.this, R.string.txt_send_notification, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(RequestDriverActivity.this, R.string.txt_not_notification, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                        // en caso de rror en la peticion
                        Log.d("Error", "Error: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
