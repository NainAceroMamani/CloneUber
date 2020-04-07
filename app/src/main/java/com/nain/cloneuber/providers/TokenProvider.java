package com.nain.cloneuber.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nain.cloneuber.models.Token;

public class TokenProvider {
    DatabaseReference mDatabase;

    public TokenProvider() {
        // Instancia de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("tokens");
    }

    // insertara el token en la base de datos
    public void create(final String idUser){ // id del usuario logeado
        if (idUser == null) return;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                // nos devolvera un objeto
                Token token = new Token(instanceIdResult.getToken()); // obtenemos el token y se lo pasamos al modelo
                mDatabase.child(idUser).setValue(token); // dentro del id del usuario almacenamos el token
            }
        });
    }
}
