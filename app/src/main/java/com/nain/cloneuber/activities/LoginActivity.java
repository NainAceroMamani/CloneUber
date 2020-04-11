package com.nain.cloneuber.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.client.MapClientActivity;
import com.nain.cloneuber.activities.client.RegisterActivity;
import com.nain.cloneuber.activities.driver.MapDriverActivity;
import com.nain.cloneuber.activities.driver.RegisterDriverActivity;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.DriverProvider;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    TextView mTextInputEmail, mTextInputPassword;
    Button mButtonLogin;
    ImageButton btnLogin;
    TextView tvRegister;

    AuthProvider mAuthProvider;

    SharedPreferences mPref;
    AlertDialog mDialog;

    DriverProvider mdriverProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInputEmail = findViewById(R.id.txtInputEmail);
        mTextInputPassword = findViewById(R.id.txtInputPassword);
        mButtonLogin = findViewById(R.id.btnSendLogin);

        mAuthProvider = new AuthProvider();

        tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegister();
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegister();
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // instanciamos el mDialog
        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage(R.string.txt_message_login).build();

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);

        mdriverProvider = new DriverProvider();
    }

    private void gotoRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            if(password.length() >= 6) {
                // enviamos los datos a firebase y mostramos el dialogo de espere
                mDialog.show();
                mAuthProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // que aremos cuando nos devuelva la respuesta
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.message_success_login, Toast.LENGTH_SHORT).show();
                            String user = mPref.getString("user", "");
                            if(user.equals("client")) {
                                Intent intent = new Intent(LoginActivity.this, MapClientActivity.class);
                                // nos aseguramos que counado precione el boton de atras no me lleve al registro sino se quede en el mapa
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else if(user.equals("driver")) {
                                String idDriver = mAuthProvider.getId();
                                mdriverProvider.getDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            String marca = dataSnapshot.child("getVehicleBrand").getValue().toString();
                                            String placa = dataSnapshot.child("getVehiclePlate").getValue().toString();

                                            if(marca != null && placa != null) {
                                                Intent intent = new Intent(LoginActivity.this, MapDriverActivity.class);
                                                // nos aseguramos que counado precione el boton de atras no me lleve al registro sino se quede en el mapa
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }else {
                                            final SharedPreferences.Editor editor = mPref.edit();
                                            editor.putString("user", "client");
                                            editor.apply();
                                            Intent intent = new Intent(LoginActivity.this, MapClientActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.error_credentials, Toast.LENGTH_LONG).show();
                        }
                        // cuando se ejecuto eliminamos el dialog
                        mDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, R.string.error_password, Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(LoginActivity.this, R.string.error_esEmpty, Toast.LENGTH_LONG).show();
        }
    }
}
