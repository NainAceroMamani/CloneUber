package com.nain.cloneuber.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.client.HistoryBookingDetailClientActivity;
import com.nain.cloneuber.models.HistoryBooking;
import com.nain.cloneuber.providers.ClientProvider;
import com.nain.cloneuber.providers.DriverProvider;
import com.nain.cloneuber.providers.HistoryBookingProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryBookingDetailDriverActivity extends AppCompatActivity {

    private TextView mTextViewName,mTextViewOrigin,mTextViewDestination,mTextViewCalification;
    private RatingBar mRatingBarCalification;
    private CircleImageView mCircleImage, mCircleImageBack;

    private String mExtraId;
    private HistoryBookingProvider mHistoryBookingProvider;
    private ClientProvider clientProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking_detail_driver);

        mTextViewName = findViewById(R.id.textViewNameBookingDetail);
        mTextViewOrigin = findViewById(R.id.textViewOriginHistoryBookingDetail);
        mTextViewDestination = findViewById(R.id.textViewDestinationHistoryBookingDetail);
        mTextViewCalification = findViewById(R.id.textViewCalificationHistoryBookingDetail);
        mRatingBarCalification = findViewById(R.id.ratingBarHistoryBookingDetail);
        mCircleImage = findViewById(R.id.circleImageHistoryBookingDetail);
        mHistoryBookingProvider = new HistoryBookingProvider();
        clientProvider = new ClientProvider();

        mCircleImageBack = findViewById(R.id.circleImageBack);

        mExtraId = getIntent().getStringExtra("idHistoryBooking");
        getHistoryBooking();

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // terminamos la actividad
            }
        });
    }

    private void getHistoryBooking(){
        mHistoryBookingProvider.getHistoryBooking(mExtraId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // en este objeto almacenamos todos los datos
                    HistoryBooking historyBooking = dataSnapshot.getValue(HistoryBooking.class);
                    mTextViewOrigin.setText(historyBooking.getOrigin());
                    mTextViewDestination.setText(historyBooking.getDestination());
                    mTextViewCalification.setText("Tu calificación: " + historyBooking.getCalificationDriver()); // calificacion del cliente al driver

                    if(dataSnapshot.hasChild("calificationClient"))
                        mRatingBarCalification.setRating((float) historyBooking.getCalificationClient());

                    clientProvider.getClient(historyBooking.getIdClient()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String name = dataSnapshot.child("name").getValue().toString();
                                mTextViewName.setText(name.toUpperCase()); // convertimos a mayuculas
                                if(dataSnapshot.hasChild("image")) {
                                    String image = dataSnapshot.child("image").getValue().toString();
                                    Picasso.with(HistoryBookingDetailDriverActivity.this).load(image).into(mCircleImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

