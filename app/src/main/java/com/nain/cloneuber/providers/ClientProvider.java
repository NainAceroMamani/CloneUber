package com.nain.cloneuber.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nain.cloneuber.models.Client;

import java.util.HashMap;
import java.util.Map;

public class ClientProvider {

    DatabaseReference mDatabase;

    public ClientProvider() {
        // Instancia de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
    }

    public Task<Void> create(Client client) {
        // cuando lo crea se cre con id por eso mateamos el cliente
        Map<String, Object> map = new HashMap<>();
        map.put("name", client.getName());
        map.put("email", client.getEmail());

        return mDatabase.child(client.getId()).setValue(map);
    }

    // para obtener los datos del cliente
    public DatabaseReference getClient(String idClient) {
        return mDatabase.child(idClient);
    }
}
