package com.nain.cloneuber.activities.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.client.HistoryBookingClientActivity;
import com.nain.cloneuber.adapters.HistoryBookingClientAdapter;
import com.nain.cloneuber.adapters.HistoryBookingDriverAdapter;
import com.nain.cloneuber.includes.MyToolbar;
import com.nain.cloneuber.models.HistoryBooking;
import com.nain.cloneuber.providers.AuthProvider;

public class HistoryBookingDriverActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private HistoryBookingDriverAdapter mAdapter;
    private AuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking_driver);
        MyToolbar.show(this, "Historial de viajes", true);

        mRecyclerView = findViewById(R.id.recyclerViewHistoryBooking);
        // para mostrar de manera vertial => sino no se mostrara
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    // ciclo de vida de android

    @Override
    protected void onStart() {
        super.onStart();
        authProvider = new AuthProvider();
        // query traemos todos los dtos de fireabse databse que esten asociados al id del cliente autentificado
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("HistoryBooking")
                .orderByChild("idDriver")
                .equalTo(authProvider.getId());
        // opciones para pasarle al adapter => HistoryBooking modelo donde se almacenara los datos
        FirebaseRecyclerOptions<HistoryBooking> options = new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query, HistoryBooking.class)
                .build();
        // instanciamos el adapter
        mAdapter = new HistoryBookingDriverAdapter(options, HistoryBookingDriverActivity.this);
        // se lo pasamos al carView
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening(); // para que escuche los cambios en tiempo real de firebase databse
    }

    // cuando se minimize la aplicacion dejamos de escuchar
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
