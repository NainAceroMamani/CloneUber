package com.nain.cloneuber.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.includes.MyToolbar;
import com.nain.cloneuber.models.ClientBooking;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientBookingProvider;
import com.nain.cloneuber.providers.ClientProvider;

public class UpdateProfileActivity extends AppCompatActivity {

    private ImageView mImageViewProfile;
    private Button mButtonUpdate;
    private TextView mTextViewName;

    private ClientProvider clientProvider;
    private AuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        MyToolbar.show(this, "Actualizar Perfil", true);

        mImageViewProfile = findViewById(R.id.imageViewProfile);
        mTextViewName = findViewById(R.id.txtInputNombre);
        mButtonUpdate = findViewById(R.id.btnProfileUpdate);

        clientProvider = new ClientProvider();
        authProvider = new AuthProvider();

        getClientInfo();

        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    // traer la info del cliente autentificado
    private void getClientInfo(){
        // addListenerForSingleValueEvent => solo obtener info una unica vez
        clientProvider.getClient(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    mTextViewName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile() {

    }
}
