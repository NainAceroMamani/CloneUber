package com.nain.cloneuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    TextView mTextInputEmail, mTextInputPassword;
    Button mButtonLogin;
    ImageButton btnLogin;
    TextView tvRegister;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInputEmail = findViewById(R.id.txtInputEmail);
        mTextInputPassword = findViewById(R.id.txtInputPassword);
        mButtonLogin = findViewById(R.id.btnSendLogin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
    }

    private void gotoRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            if(password.length() > 6) {
                // enviamos los datos a firebase y mostramos el dialogo de espere
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // que aremos cuando nos devuelva la respuesta
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.message_success_login, Toast.LENGTH_SHORT).show();
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
