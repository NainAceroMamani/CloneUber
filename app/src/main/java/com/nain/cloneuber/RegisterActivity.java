package com.nain.cloneuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nain.cloneuber.models.User;

public class RegisterActivity extends AppCompatActivity {

    Toolbar mToolbar; // para menu en la parte superior para volver hacia atras
    ImageButton btnRegister;
    TextView tvLogin;

    SharedPreferences mPref;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    //Views
    Button mbtnRegisterSave;
    EditText mTxtInputNombre, mTxtInputEmail, mTxtInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Instancia de firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Instancia del Share Prefenreces
        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
//        String selectedUser = mPref.getString("user", "");
//        Toast.makeText(this, selectedUser, Toast.LENGTH_SHORT).show();

        mToolbar = findViewById(R.id.bgHeader);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        mbtnRegisterSave = findViewById(R.id.btnRegisterSave);
        mTxtInputNombre = findViewById(R.id.txtInputNombre);
        mTxtInputEmail = findViewById(R.id.txtInputEmail);
        mTxtInputPassword = findViewById(R.id.txtInputPassword);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });
        mbtnRegisterSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String name = mTxtInputNombre.getText().toString(); // final para poder pasar como parametro en el metodo saveUser()
        final String email = mTxtInputEmail.getText().toString();
        String password = mTxtInputPassword.getText().toString();

        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            if(password.length() >= 6) {
                // registramos en Autentication de fireabse
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // metodo para guardar el dattabase de fireabse
                            saveUser(name, email);
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.error_register_user, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.error_password, Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, R.string.error_validate_register , Toast.LENGTH_LONG).show();
        }
    }

    private void saveUser(String name, String email) {
        // traemos el valor de las SharedPreferences (driver,client)
        String selectedUser = mPref.getString("user", "");
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        if(selectedUser.equals("driver")) {
            // child => nodo hijo // value =>  objeto // luego crear modelo para el usuario
            // push crea el id unico para el usuario
            mDatabase.child("Users").child("Drivers").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.error_register_user), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if(selectedUser.equals("client")) {
            // child => nodo hijo // value =>  objeto // luego crear modelo para el usuario
            // push crea el id unico para el usuario
            mDatabase.child("Users").child("Clients").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.error_register_user), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void gotoLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
