package com.nain.cloneuber.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nain.cloneuber.R;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientBookingProvider;
import com.nain.cloneuber.providers.GeoFireProvider;

public class NotificationBookingActivity extends AppCompatActivity {

    private TextView mTextViewDestination,mTextViewOrigin,mTextViewMin,mTextViewDistance;
    private Button mButtonAccept,mButtonCancel;

    private ClientBookingProvider mclientBookingProvider;
    private AuthProvider mAuthProvider;
    private GeoFireProvider geoFireProvider;

    private String mExtraIdClient;

    private String mExtraOrigin;
    private String mExtraDestination;
    private String mExtraMin;
    private String mExtraDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_booking);

        mTextViewDestination = findViewById(R.id.textViewDestination);
        mTextViewOrigin = findViewById(R.id.textViewOrigin);
        mTextViewMin = findViewById(R.id.textViewMin);
        mTextViewDistance = findViewById(R.id.textViewDistance);
        mButtonAccept = findViewById(R.id.btnAcceptBooking);
        mButtonCancel = findViewById(R.id.btnCancelBooking);

        mExtraIdClient = getIntent().getStringExtra("idClient");
        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");
        mExtraMin = getIntent().getStringExtra("min");
        mExtraDistance = getIntent().getStringExtra("distance");

        mTextViewDestination.setText(mExtraDestination);
        mTextViewOrigin.setText(mExtraOrigin);
        mTextViewMin.setText(mExtraMin);
        mTextViewDistance.setText(mExtraDistance);

        mButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBooking();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();
            }
        });
    }

    private void cancelBooking() {

        mclientBookingProvider = new ClientBookingProvider();
        mclientBookingProvider.updateStatus(mExtraIdClient, "cancel");

        // para que desaparesca automaticamente
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2); // el id de la notifiacation con boton es el 2
        Intent intent = new Intent(NotificationBookingActivity.this, MapDriverActivity.class);
        startActivity(intent);
        finish();
    }

    private void acceptBooking() {
        // lo eliminamos de fireabse database
        mAuthProvider = new AuthProvider();
        geoFireProvider = new GeoFireProvider("active_drivers");
        geoFireProvider.removeLocation(mAuthProvider.getId());

        mclientBookingProvider = new ClientBookingProvider();
        mclientBookingProvider.updateStatus(mExtraIdClient, "accept");

        // para que desaparesca automaticamente
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2); // el id de la notifiacation con boton es el 2

        // abrimos actividad desde notificación
        Intent intent1 = new Intent(NotificationBookingActivity.this, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // FLAG_ACTIVITY_CLEAR_TASK => par que el conductor no pueda volver a la pantalla anterior
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idClient", mExtraIdClient); // pasamos como un parametro extra el id del cliente a la actividad MapDriverBookingActivity
        startActivity(intent1);
    }
}
