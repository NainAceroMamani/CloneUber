package com.nain.cloneuber.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nain.cloneuber.activities.driver.MapDriverBookingActivity;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientBookingProvider;
import com.nain.cloneuber.providers.GeoFireProvider;

public class AcceptReciver extends BroadcastReceiver {

    private ClientBookingProvider mclientBookingProvider;
    private AuthProvider mAuthProvider;
    private GeoFireProvider geoFireProvider;

    // se ejecutara cuando se presione sobre aceptar
    @Override
    public void onReceive(Context context, Intent intent) {

        // lo eliminamos de fireabse database
        mAuthProvider = new AuthProvider();
        geoFireProvider = new GeoFireProvider("active_drivers");
        geoFireProvider.removeLocation(mAuthProvider.getId());

        // intent llega los datos que enviamos idClient del MyFirebaseMassage como putExtra
        // obtenemos el id del cliente
        String idClient = intent.getExtras().getString("idClient");

        mclientBookingProvider = new ClientBookingProvider();
        mclientBookingProvider.updateStatus(idClient, "accept");

        // para que desaparesca automaticamente
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.cancel(2); // el id de la notifiacation con boton es el 2

        // abrimos actividad desde notificación
        Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        context.startActivity(intent1);
    }
}