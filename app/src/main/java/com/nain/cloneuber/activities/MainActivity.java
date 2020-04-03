package com.nain.cloneuber.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.client.MapClientActivity;
import com.nain.cloneuber.activities.driver.MapDriverActivity;

public class MainActivity extends AppCompatActivity {

    Button mButtonIAmClient;
    Button mButtonIAMDriver;

    SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instanciamos el Share Prefenreces
        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPref.edit();

        mButtonIAmClient = findViewById(R.id.btnIAmClient);
        mButtonIAMDriver = findViewById(R.id.btnIamDriver);

        mButtonIAmClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user", "client");
                editor.apply();
                gotoSelectAuth();
            }
        });

        mButtonIAMDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user", "driver");
                editor.apply();
                gotoSelectAuth();
            }
        });
    }

    // metodo ciclo de vida de android
    @Override
    protected void onStart() {
        super.onStart();
        // para verificar si exite sesion en fireabse
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            String user = mPref.getString("user", "");
            if(user.equals("client")) {
                Intent intent = new Intent(MainActivity.this, MapClientActivity.class);
                // nos aseguramos que counado precione el boton de atras no me lleve al registro sino se quede en el mapa
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if(user.equals("driver")) {
                Intent intent = new Intent(MainActivity.this, MapDriverActivity.class);
                // nos aseguramos que counado precione el boton de atras no me lleve al registro sino se quede en el mapa
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void gotoSelectAuth() {
        Intent intent = new Intent(MainActivity.this, SelectOptionsAuthActivity.class);
        startActivity(intent);
    }
}
