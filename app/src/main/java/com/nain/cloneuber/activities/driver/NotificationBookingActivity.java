package com.nain.cloneuber.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.nain.cloneuber.R;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientBookingProvider;
import com.nain.cloneuber.providers.GeoFireProvider;

public class NotificationBookingActivity extends AppCompatActivity {

    private TextView mTextViewDestination,mTextViewOrigin,mTextViewMin,mTextViewDistance,mTextViewCounter;
    private Button mButtonAccept,mButtonCancel;

    private ClientBookingProvider mclientBookingProvider;
    private AuthProvider mAuthProvider;
    private GeoFireProvider geoFireProvider;

    private String mExtraIdClient;

    private String mExtraOrigin;
    private String mExtraDestination;
    private String mExtraMin;
    private String mExtraDistance;

    private Handler mhandler; // para el contador
    private int mcounter = 10;
    Runnable rounable = new Runnable() {
        @Override
        public void run() {
            mcounter = mcounter -1;
            mTextViewCounter.setText(String.valueOf(mcounter));
            if(mcounter > 0) {
                initTimer();
            }else {
                cancelBooking(); // cancelamos el viaje
            }
        }
    };

    private MediaPlayer mediaPlayer;

    // para inicializar el contador
    private void initTimer() {
        mhandler = new Handler();
        mhandler.postDelayed(rounable, 1000);
    }

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
        mTextViewCounter = findViewById(R.id.textViewCounter);

        mExtraIdClient = getIntent().getStringExtra("idClient");
        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");
        mExtraMin = getIntent().getStringExtra("min");
        mExtraDistance = getIntent().getStringExtra("distance");

        mTextViewDestination.setText(mExtraDestination);
        mTextViewOrigin.setText(mExtraOrigin);
        mTextViewMin.setText(mExtraMin);
        mTextViewDistance.setText(mExtraDistance);

        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true); // para que el sonido se repita varias veces

        // PARA ENCENDER EL CELULAR CUANDO ESTE APAGADO PARA MOSTRAR ESTA ACTIVIDAD
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        initTimer();

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
        if(mhandler != null) {
            // cuando le damos en cancelar nos aseguramos de que el hander deje de contar
            mhandler.removeCallbacks(rounable);
        }
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
        if(mhandler != null) {
            // cuando le damos en cancelar nos aseguramos de que el hander deje de contar
            mhandler.removeCallbacks(rounable);
        }
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

    // otro ciclo de vida se ejecuta cuando minimizamos la aplicacion
    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying()) {
                // verificamos que no este sonando
                mediaPlayer.release();
            }
        }
    }

    //cuando el usuario abandona la actividad
    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying()){
                // verificar si esta sonando
                mediaPlayer.pause();
            }
        }
    }

    // metodo de ciclo de vida => se ejecuta cuando la actividad a sido creada
    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer != null) {
            if(!mediaPlayer.isPlaying()){
                // verificar si esta sonando
                mediaPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mhandler != null) {
            // cuando le damos en cancelar nos aseguramos de que el hander deje de contar
            mhandler.removeCallbacks(rounable);
        }
        if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying()){
                // verificar si esta sonando
                mediaPlayer.pause(); // dejar de sonar
            }
        }
    }
}
