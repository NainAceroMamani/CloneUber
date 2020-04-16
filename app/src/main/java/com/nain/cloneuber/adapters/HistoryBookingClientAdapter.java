package com.nain.cloneuber.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.client.HistoryBookingDetailClientActivity;
import com.nain.cloneuber.models.HistoryBooking;
import com.nain.cloneuber.providers.DriverProvider;
import com.squareup.picasso.Picasso;

// para el cardView creamos ViewHolder y se lo pasamos
// primer parametro el modelo HistoryBooking , segundo el View Holder
public class HistoryBookingClientAdapter extends FirebaseRecyclerAdapter<HistoryBooking, HistoryBookingClientAdapter.ViewHolder> {

    private DriverProvider mDriverProvider;
    private Context mContex;

    // configuracion para mostrar la informacion
    public HistoryBookingClientAdapter(FirebaseRecyclerOptions<HistoryBooking> options, Context context){
        super(options);
        mDriverProvider = new DriverProvider();
        mContex = context;

    }

    // estableceremos los valores del card View
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull HistoryBooking historyBooking) {
        // enviamos el id del HistoryBooking
        final String id = getRef(position).getKey(); // te devuelve el id de cada uo de los historiales de viaje

        // holder acceder a cada campo
        holder.textViewOrigen.setText(historyBooking.getOrigin());
        holder.textViewDestino.setText(historyBooking.getDestination());
        holder.textViewCalificacion.setText(String.valueOf(historyBooking.getCalificationClient()));
        mDriverProvider.getDriver(historyBooking.getIdDriver()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    holder.textViewName.setText(name);
                    if(dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(mContex).load(image).into(holder.imageViewHistoryBooking);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // el view es toda la tarjeta
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContex, HistoryBookingDetailClientActivity.class);
                intent.putExtra("idHistoryBooking", id);
                mContex.startActivity(intent);
            }
        });
    }

    // instaciamos el layout en este caso Card_History_Booking.xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_booking, parent, false);
        return new ViewHolder(view);
    }

    // aqui vamos la instanciar cada vista que tenemos en nuestra tarjeta => nombre,origen,destino,caificacion
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName, textViewOrigen, textViewDestino, textViewCalificacion;
        private ImageView imageViewHistoryBooking;
        private View mView;

        public ViewHolder(View view) {
            super(view);
            // el view es toda la tarjeta
            mView = view;
            // findViewById solo es para actividades no estamos dentro de una actividad
            textViewName = view.findViewById(R.id.textViewName);
            textViewOrigen = view.findViewById(R.id.textViewOrigin);
            textViewDestino = view.findViewById(R.id.textViewDestination);
            textViewCalificacion = view.findViewById(R.id.textViewCalification);
            imageViewHistoryBooking = view.findViewById(R.id.imageViewHistoryBooking);

        }
    }

}
