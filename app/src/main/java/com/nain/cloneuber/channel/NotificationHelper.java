package com.nain.cloneuber.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.nain.cloneuber.R;

public class NotificationHelper extends ContextWrapper {
    // este codigo no cambia solo el Channel_Id y  el Channel_Name
    private static final String CHANNEL_ID = "com.nain.cloneuber";  // el paquete
    private static final String CHANNEL_NAME = "UberClone";         // el nombre

    private NotificationManager manager = null;

    public NotificationHelper(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            createChennels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O) // NECESARIO PARA LAS VERSIONES DESDE ANDROID OREO
    private void createChennels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true); // activar vibracion la notificacion
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if(manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }


    // metodo para mostrar notificaciones
    @RequiresApi(api = Build.VERSION_CODES.O) // NECESARIO PARA LAS VERSIONES DESDE ANDROID OREO
    public Notification.Builder getNotification(String title, String body, PendingIntent intent , Uri sounUri) {
        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)// PRIMER PARAMETRO ES EL CONTEXTO
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true) // para cuando presione sobre la notificacion se cierre
                        .setSound(sounUri) // para el sonido de la notificacion
                        .setContentIntent(intent) //para agregar eventos
                        .setSmallIcon(R.drawable.ic_car) // icono de la notificacion
                        .setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title)); // para mostrar toda la info de la notificaiom y no se recorte
    }

    // metodo para mostrar notificaciones con boton
    @RequiresApi(api = Build.VERSION_CODES.O) // NECESARIO PARA LAS VERSIONES DESDE ANDROID OREO
    public Notification.Builder getNotificationAction(String title, String body, Uri sounUri, Notification.Action accepAction, Notification.Action cancelAction) {
        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)// PRIMER PARAMETRO ES EL CONTEXTO
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true) // para cuando presione sobre la notificacion se cierre
                .setSound(sounUri) // para el sonido de la notificacion
                .addAction(accepAction) // para añadir la accion
                .addAction(cancelAction) // para canelar la notification => max 3 actions
                .setSmallIcon(R.drawable.ic_car) // icono de la notificacion
                .setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title)); // para mostrar toda la info de la notificaiom y no se recorte
    }

    // metodo para mostrar notificaciones
    public NotificationCompat.Builder getNotificationolApi(String title, String body, PendingIntent intent , Uri sounUri) {
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)// PRIMER PARAMETRO ES EL CONTEXTO
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true) // para cuando presione sobre la notificacion se cierre
                .setSound(sounUri) // para el sonido de la notificacion
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_car) // icono de la notificacion
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title)); // para mostrar toda la info de la notificaiom y no se recorte
    }

    // metodo para mostrar notificaciones con boton
    public NotificationCompat.Builder getNotificationolApiAction(String title, String body, Uri sounUri, NotificationCompat.Action accepAction, NotificationCompat.Action cancelAction) {
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)// PRIMER PARAMETRO ES EL CONTEXTO
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true) // para cuando presione sobre la notificacion se cierre
                .setSound(sounUri) // para el sonido de la notificacion
                .addAction(accepAction) // para añadir la accion
                .addAction(cancelAction) // para cancelar notification => max 3 acciones
                .setSmallIcon(R.drawable.ic_car) // icono de la notificacion
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title)); // para mostrar toda la info de la notificaiom y no se recorte
    }
}
