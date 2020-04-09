package com.nain.cloneuber.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nain.cloneuber.R;
import com.nain.cloneuber.channel.NotificationHelper;
import com.nain.cloneuber.receivers.AcceptReciver;

import java.util.Map;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {
    // este archivo tambien  no cambia reutilizable

    private final static int NOTIFICATION_CODE = 100;

    // para poder enviar notificaciones de dispositivo a dispositivo
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    // este metodo recibe las notificaciones push
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        // creamos mapa de valores
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");   // obtenemos el titulo de la notificacion
        String body = data.get("body");     // obtenemos el cuerpo de la notificacion

        if(title != null) {
            // si el sdk es mayor qye android oreo
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // verificamos si en la notificacion esta la palabra SOLICIUTD DE SERVICIO.....
                if(title.contains("SOLICITUD DE SERVICIO")) {
                    String idClient = data.get("idClient");
                    showNotificationApiOreoActions(title, body, idClient);
                }else {
                    showNotificationApiOreo(title, body);
                }
            }else {
                // verificamos si en la notificacion esta la palabra SOLICIUTD DE SERVICIO.....
                if(title.contains("SOLICITUD DE SERVICIO")) {
                    String idClient = data.get("idClient");
                    showNotificationApiAction(title, body, idClient);
                }else {
                    showNotificationApi(title, body);
                }
            }
        }
    }

    private void showNotificationApi(String title, String body) {
        //intent => accion dque va a jecutar
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(),0,new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // sonido de la notificacion

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationolApi(title,body, intent, sound);
        notificationHelper.getManager().notify(1,builder.build()); // primer paramtro id de la notificacion
    }

    private void showNotificationApiAction(String title, String body, String idClient) {
        // Notification con boton
        // acceptIntent donde se ejecutara el metodo cuando le de aceptar
        Intent acceptIntent = new Intent(this, AcceptReciver.class);
        acceptIntent.putExtra("idClient", idClient);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        // Propiedades del Boton
        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar", // nombre del boton
                acceptPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // sonido de la notificacion

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationolApiAction(title,body, sound, acceptAction);
        notificationHelper.getManager().notify(2,builder.build()); // primer paramtro id de la notificacion sera dos para que no sobreescriba la notification 1
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreo(String title, String body) {
        //intent => accion dque va a jecutar
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(),0,new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // sonido de la notificacion

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        // Ya no utlizamos el NotificationCompat => SOLO NOTIFICATION
        Notification.Builder builder = notificationHelper.getNotification(title,body, intent, sound);
        notificationHelper.getManager().notify(1,builder.build()); // primer paramtro id de la notificacion
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreoActions(String title, String body, String idClient) {
        // Notification con boton
        // acceptIntent donde se ejecutara el metodo cuando le de aceptar
        Intent acceptIntent = new Intent(this, AcceptReciver.class);
        acceptIntent.putExtra("idClient", idClient);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        // Propiedades del Boton
        Notification.Action acceptAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar", // nombre del boton
                acceptPendingIntent
        ).build();

        //intent => accion dque va a ejecutar
//        PendingIntent intent = PendingIntent.getActivity(getBaseContext(),0,new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // sonido de la notificacion

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        // Ya no utlizamos el NotificationCompat => SOLO NOTIFICATION
        Notification.Builder builder = notificationHelper.getNotificationAction(title,body, sound, acceptAction);
        notificationHelper.getManager().notify(2,builder.build()); // primer paramtro id de la notificacion
    }
}
