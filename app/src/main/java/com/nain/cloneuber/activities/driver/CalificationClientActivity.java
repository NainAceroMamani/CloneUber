package com.nain.cloneuber.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.client.CalificationDriveractivity;
import com.nain.cloneuber.activities.client.MapClientActivity;
import com.nain.cloneuber.models.ClientBooking;
import com.nain.cloneuber.models.HistoryBooking;
import com.nain.cloneuber.providers.ClientBookingProvider;
import com.nain.cloneuber.providers.HistoryBookingProvider;

import java.util.Date;

public class CalificationClientActivity extends AppCompatActivity {

    private TextView mTextViewOrigin,mTextViewDestination;
    private RatingBar mRatinBar;
    private Button mButtonCalification;

    private ClientBookingProvider mclientBookingProvider;

    private String mExtraClientId;

    private HistoryBooking mhistoryBooking;
    private HistoryBookingProvider mhistoryBookingProvider;

    private float mCalification = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calification_client);

        mTextViewDestination = findViewById(R.id.textViewDestinationCalification);
        mTextViewOrigin = findViewById(R.id.textViewOriginCalification);
        mRatinBar = findViewById(R.id.RatinBarCalification);
        mButtonCalification = findViewById(R.id.btnCalification);
        mRatinBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // nos devuelve la eleccion del usuario
            @Override
            public void onRatingChanged(RatingBar ratingBar, float calificacion, boolean fromUser) {
                mCalification = calificacion;
            }
        });

        mhistoryBookingProvider = new HistoryBookingProvider();

        mclientBookingProvider = new ClientBookingProvider();

        mExtraClientId = getIntent().getStringExtra("idClient");

        mButtonCalification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calificate();
            }
        });

        getClientBooking();
    }

    private void getClientBooking(){
        // addListenerForSingleValueEvent => solo trae los datos una vez
        mclientBookingProvider.getClientBooking(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
//                    String origen = dataSnapshot.child("origin").getValue().toString();
                    // en este caso nos traeremos toda la informacion ... todos los nodos
                    ClientBooking clientBooking = dataSnapshot.getValue(ClientBooking.class);
                    mTextViewOrigin.setText(clientBooking.getOrigin());
                    mTextViewDestination.setText(clientBooking.getDestination());
                    mhistoryBooking = new HistoryBooking(
                      clientBooking.getIdHistoryBooking(),
                      clientBooking.getIdClient(),
                      clientBooking.getIdDriver(),
                      clientBooking.getDestination(),
                      clientBooking.getOrigin(),
                      clientBooking.getTime(),
                      clientBooking.getKm(),
                      clientBooking.getStatus(),
                      clientBooking.getOriginLat(),
                      clientBooking.getOriginLng(),
                      clientBooking.getDestinationLat(),
                      clientBooking.getDestinationLng()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void calificate() {
        if(mCalification > 0) {
            // actualizamos el historial en la Bd
            mhistoryBooking.setCalificationClient(mCalification); // le pasamos la calificación la modelo
            mhistoryBooking.setTimestamp(new Date().getTime()); // almacenamos la hora y fecha actual en el modelo
            // tenemos dos opciones si esta creado el registro solo actualizaremos sino lo creamos
            // addListenerForSingleValueEvent => solo se ejecuta una vez
            mhistoryBookingProvider.getHistoryBooking(mhistoryBooking.getIdHistoryBooking()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        // addOnSuccessListener si se ejecuto correctamente que me lleve a otra pantalla
                        mhistoryBookingProvider.updateCalificationClient(mhistoryBooking.getIdHistoryBooking(), mCalification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificationClientActivity.this, R.string.txt_success_calification, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CalificationClientActivity.this, MapDriverActivity.class);
                                startActivity(intent);
                                finish(); // par que no se pueda volver hacia atras => finalizamos la actividad
                            }
                        });
                    }else {
                        mhistoryBookingProvider.create(mhistoryBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificationClientActivity.this, R.string.txt_success_calification, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CalificationClientActivity.this, MapDriverActivity.class);
                                startActivity(intent);
                                finish(); // par que no se pueda volver hacia atras => finalizamos la actividad
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, R.string.txt_error_calification, Toast.LENGTH_LONG).show();
        }
    }
}
