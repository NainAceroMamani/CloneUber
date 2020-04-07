package com.nain.cloneuber.services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {


    // para poder enviar notificaciones de dispositivo a dispositivo
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    // este metodo recibe las notificaciones push
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
