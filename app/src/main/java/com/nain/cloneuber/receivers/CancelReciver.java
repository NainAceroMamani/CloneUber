package com.nain.cloneuber.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nain.cloneuber.providers.ClientBookingProvider;

public class CancelReciver extends BroadcastReceiver {
    // registrar en el android manifest
    // => methos para cancelar Notification
    private ClientBookingProvider mclientBookingProvider;

    // se ejecutara cuando se presione sobre cancelar
    @Override
    public void onReceive(Context context, Intent intent) {
        // intent llega los datos que enviamos idClient del MyFirebaseMassage como putExtra
        // obtenemos el id del cliente
        String idClient = intent.getExtras().getString("idClient");

        mclientBookingProvider = new ClientBookingProvider();
        mclientBookingProvider.updateStatus(idClient, "cancel");

        // para que desaparesca automaticamente
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.cancel(2); // el id de la notifiacation con boton es el 2
    }
}
