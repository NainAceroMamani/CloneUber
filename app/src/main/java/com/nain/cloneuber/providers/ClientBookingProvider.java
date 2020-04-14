package com.nain.cloneuber.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nain.cloneuber.models.ClientBooking;

import java.util.HashMap;
import java.util.Map;

public class ClientBookingProvider {

    private DatabaseReference mDatabase;

    public ClientBookingProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("clientBooking");
    }

    public Task<Void> create(ClientBooking clientBooking){
        return mDatabase.child(clientBooking.getIdClient()).setValue(clientBooking);
    }

    // actualizamos el estado de creado a aceptado en firebase database
    public Task<Void> updateStatus(String idClientBooking, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        return mDatabase.child(idClientBooking).updateChildren(map);
    }

    // asemos referencia a la fireabase database => idClientBooking == idClient
    public DatabaseReference getstatus(String idClientBooking){
        return mDatabase.child(idClientBooking).child("status");
    }

    // para trazar la ruta del conductor al cliente
    public DatabaseReference getClientBooking(String idClientBooking){
        return mDatabase.child(idClientBooking);
    }

    // para generar un id unico para almacenar el historial de viajes
    public Task<Void> updateIdHistoryBooking(String idClientBooking) {
        String idPush = mDatabase.push().getKey(); // creamos un identificador unico para almacenar el historial
        Map<String, Object> map = new HashMap<>();
        map.put("idHistoryBooking", idPush);
        return mDatabase.child(idClientBooking).updateChildren(map);
    }

    public Task<Void> delete(String idClientBooking){
        // eliminar info de la base de datos
        return mDatabase.child(idClientBooking).removeValue();
    }
}
