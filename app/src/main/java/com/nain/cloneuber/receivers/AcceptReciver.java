package com.nain.cloneuber.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nain.cloneuber.providers.ClientBookingProvider;

public class AcceptReciver extends BroadcastReceiver {

    private ClientBookingProvider mclientBookingProvider;

    // se ejecutara cuando se presione sobre aceptar
    @Override
    public void onReceive(Context context, Intent intent) {
        // intent llega los datos que enviamos idClient del MyFirebaseMassage como putExtra
        // obtenemos el id del cliente
        String idClient = intent.getExtras().getString("idClient");

        mclientBookingProvider = new ClientBookingProvider();
        mclientBookingProvider.updateStatus(idClient, "accept");

        // para que desaparesca automaticamente
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.cancel(2); // el id de la notifiacation con boton es el 2
    }
}
