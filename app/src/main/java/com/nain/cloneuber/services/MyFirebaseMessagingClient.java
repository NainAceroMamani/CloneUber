package com.nain.cloneuber.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.driver.NotificationBookingActivity;
import com.nain.cloneuber.channel.NotificationHelper;
import com.nain.cloneuber.receivers.AcceptReciver;
import com.nain.cloneuber.receivers.CancelReciver;

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
        Map<String, String> data = remoteMessage.getData(); // obtenemos todos los parametros que pasamos
        String title = data.get("title");   // obtenemos el titulo de la notificacion
        String body = data.get("body");     // obtenemos el cuerpo de la notificacion

        if(title != null) {
            // si el sdk es mayor qye android oreo
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // verificamos si en la notificacion esta la palabra SOLICIUTD DE SERVICIO.....
                if(title.contains("SOLICITUD DE SERVICIO")) {
                    String idClient = data.get("idClient");
                    String origin = data.get("origin");
                    String destination = data.get("destination");
                    String min = data.get("min");
                    String distance = data.get("distance");
                    showNotificationApiOreoActions(title, body, idClient);
                    showNotificationActivity(idClient, origin, destination, min, distance);
                }else {
                    showNotificationApiOreo(title, body);
                }
            }else {
                // verificamos si en la notificacion esta la palabra SOLICIUTD DE SERVICIO.....
                if(title.contains("SOLICITUD DE SERVICIO")) {
                    String idClient = data.get("idClient");
                    String origin = data.get("origin");
                    String destination = data.get("destination");
                    String min = data.get("min");
                    String distance = data.get("distance");
                    showNotificationApiAction(title, body, idClient);
                    showNotificationActivity(idClient, origin, destination, min, distance);
                }else {
                    showNotificationApi(title, body);
                }
            }
        }
    }

    private void showNotificationActivity(String idClient,String origin,String destination, String min,String distance){
        // encender el celular haci este bloqueado
        PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn(); // para verificar si la pantalla esta encendida
        if(!isScreenOn){
            // si la pantalla no esta encendida
            PowerManager.WakeLock wakeLock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE ,
                    "AppNmae:MyLock"
            );
            wakeLock.acquire(10000);
        }
        Intent intent = new Intent(getBaseContext(), NotificationBookingActivity.class);
        intent.putExtra("idClient", idClient);
        intent.putExtra("origin", origin);
        intent.putExtra("destination", destination);
        intent.putExtra("min", min);
        intent.putExtra("distance", distance);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // para que no pueda volver atras
        startActivity(intent);
        // añadir los permisos en el manifest
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
        // aceptar

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

        // cancel
        Intent cencelIntent = new Intent(this, CancelReciver.class);
        cencelIntent.putExtra("idClient", idClient);
        PendingIntent cencelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cencelIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        // Propiedades del Boton
        NotificationCompat.Action cancelAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar", // nombre del boton
                cencelPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // sonido de la notificacion

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationolApiAction(title,body, sound, acceptAction, cancelAction);
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
        // aceptar

        // acceptIntent donde se ejecutara el metodo cuando le de aceptar
        Intent acceptIntent = new Intent(this, AcceptReciver.class);
        // dato que le enviamos
        acceptIntent.putExtra("idClient", idClient);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        // Propiedades del Boton
        Notification.Action acceptAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar", // nombre del boton
                acceptPendingIntent
        ).build();

        // cancel
        Intent cancelIntent = new Intent(this, CancelReciver.class);
        // dato que le enviamos
        cancelIntent.putExtra("idClient", idClient);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        // Propiedades del Boton
        Notification.Action cancelAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar", // nombre del boton
                cancelPendingIntent
        ).build();

        //intent => accion dque va a ejecutar
//        PendingIntent intent = PendingIntent.getActivity(getBaseContext(),0,new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // sonido de la notificacion

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        // Ya no utlizamos el NotificationCompat => SOLO NOTIFICATION
        Notification.Builder builder = notificationHelper.getNotificationAction(title,body, sound, acceptAction, cancelAction);
        notificationHelper.getManager().notify(2,builder.build()); // primer paramtro id de la notificacion
    }
}
