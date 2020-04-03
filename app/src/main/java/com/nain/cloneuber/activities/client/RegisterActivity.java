package com.nain.cloneuber.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.LoginActivity;
import com.nain.cloneuber.activities.driver.MapDriverActivity;
import com.nain.cloneuber.activities.driver.RegisterDriverActivity;
import com.nain.cloneuber.includes.MyToolbar;
import com.nain.cloneuber.models.Client;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientProvider;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    ImageButton btnRegister;
    TextView tvLogin;

    AuthProvider mAuthProvider;
    ClientProvider mClientProvider;

    //Views
    Button mbtnRegisterSave;
    EditText mTxtInputNombre, mTxtInputEmail, mTxtInputPassword;

    AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MyToolbar.show(this, "", true);

        mAuthProvider = new AuthProvider();
        mClientProvider = new ClientProvider();
        
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
                Clickregister();
            }
        });

        // instanciamos el mDialog
        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage(R.string.txt_message_login).build();
    }

    private void Clickregister() {
        final String name = mTxtInputNombre.getText().toString(); // final para poder pasar como parametro en el metodo saveUser()
        final String email = mTxtInputEmail.getText().toString();
        final String password = mTxtInputPassword.getText().toString();

        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            if(password.length() >= 6) {
                mDialog.show();
                // registramos en Autentication de fireabse
               register(name,email, password);
            } else {
                Toast.makeText(this, R.string.error_password, Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, R.string.error_validate_register , Toast.LENGTH_LONG).show();
        }
    }

    private void register(final String name, final String email, String password) {
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if(task.isSuccessful()){
                    // metodo para guardar el dattabase de fireabse
                    // obtenemos el identificador de firebase Authentication
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Client client = new Client(id,name,email);
                    create(client);
                } else {
                    Toast.makeText(RegisterActivity.this, R.string.error_register_user, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void create(Client client){
        mClientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MapClientActivity.class);
                    // nos aseguramos que counado precione el boton de atras no me lleve al registro sino se quede en el mapa o se cierre el app
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    Toast.makeText(RegisterActivity.this, getString(R.string.error_register_user), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void gotoLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
/*
    private void saveUser(String id,String name, String email) {
        // traemos el valor de las SharedPreferences (driver,client)
        String selectedUser = mPref.getString("user", "");
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        if(selectedUser.equals("driver")) {
            // child => nodo hijo // value =>  objeto // luego crear modelo para el usuario
            // push crea el id unico para el usuario // lo quitamos ya que solo era prueba id vendra de Auth que genra firebase
            mDatabase.child("Users").child("Drivers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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
            // push crea el id unico para el usuario // lo quitamos ya que solo era prueba id vendra de Auth que genra firebase
            mDatabase.child("Users").child("Clients").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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
*/
}
