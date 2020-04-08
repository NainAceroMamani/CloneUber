package com.nain.cloneuber.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nain.cloneuber.models.ClientBooking;

public class ClientBookingProvider {

    private DatabaseReference mDatabase;

    public ClientBookingProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("clientBooking");
    }

    public Task<Void> create(ClientBooking clientBooking){
        return mDatabase.child(clientBooking.getIdClient()).setValue(clientBooking);
    }
}
