package com.nain.cloneuber.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.LoginActivity;
import com.nain.cloneuber.includes.MyToolbar;
import com.nain.cloneuber.models.Driver;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.DriverProvider;

import dmax.dialog.SpotsDialog;

public class RegisterDriverActivity extends AppCompatActivity {

    ImageButton btnRegister;
    TextView tvLogin;

    CardView cv1, cv2;
    RelativeLayout mrlImageButton, mrlRegister;

    AuthProvider mAuthProvider;
    DriverProvider mDriverProvider;

    //Views
    Button mbtnRegisterSave;
    EditText mTxtInputNombre, mTxtInputEmail, mTxtInputPassword, mTxtInputMarca, mTxtInputPlaca;

    AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        MyToolbar.show(this, "", true);

        mAuthProvider = new AuthProvider();
        mDriverProvider = new DriverProvider();

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        mbtnRegisterSave = findViewById(R.id.btnRegisterSave);
        mTxtInputNombre = findViewById(R.id.txtInputNombre);
        mTxtInputEmail = findViewById(R.id.txtInputEmail);
        mTxtInputPassword = findViewById(R.id.txtInputPassword);
        mTxtInputMarca = findViewById(R.id.txtInputMarca);
        mTxtInputPlaca = findViewById(R.id.txtInputPlaca);

        mrlImageButton = findViewById(R.id.rlImageButton);
        mrlRegister = findViewById(R.id.rlRegister);

        cv1 = findViewById(R.id.cv);
        cv2 = findViewById(R.id.cv2);

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
        mDialog = new SpotsDialog.Builder().setContext(RegisterDriverActivity.this).setMessage(R.string.txt_message_login).build();
    }

    private void Clickregister() {
        if(cv1.getVisibility() == View.VISIBLE) {
            cv1.setVisibility(View.GONE);
            cv2.setVisibility(View.VISIBLE);
            mbtnRegisterSave.setText(R.string.txt_enviar);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mrlImageButton.getLayoutParams();
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mrlRegister.getLayoutParams();
            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) tvLogin.getLayoutParams();

            params1.addRule(RelativeLayout.BELOW, R.id.cv2);
            params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.cv2);
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, R.id.cv2);
            params.addRule(RelativeLayout.ALIGN_TOP, R.id.cv2);
            params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.cv2);
            params3.addRule(RelativeLayout.BELOW, R.id.cv2);

            mrlImageButton.setLayoutParams(params);
            mrlRegister.setLayoutParams(params1);
            tvLogin.setLayoutParams(params3);
        } else {
            save();
        }
    }

    private void save() {
        final String name = mTxtInputNombre.getText().toString(); // final para poder pasar como parametro en el metodo saveUser()
        final String email = mTxtInputEmail.getText().toString();
        final String password = mTxtInputPassword.getText().toString();
        final String marca = mTxtInputMarca.getText().toString();
        final String placa = mTxtInputPlaca.getText().toString();

        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !marca.isEmpty() && !placa.isEmpty()) {
            if(password.length() >= 6) {
                mDialog.show();
                // registramos en Autentication de fireabse
                register(name,email, marca, placa, password);
            } else {
                Toast.makeText(this, R.string.error_password, Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, R.string.error_validate_register , Toast.LENGTH_LONG).show();
        }
    }

    private void register(final String name, final String email, final String marca , final String placa , String password) {
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if(task.isSuccessful()){
                    // metodo para guardar el dattabase de fireabse
                    // obtenemos el identificador de firebase Authentication
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Driver driver = new Driver(id,name,email, marca, placa);
                    create(driver);
                } else {
                    Toast.makeText(RegisterDriverActivity.this, R.string.error_register_user, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void create(Driver driver){
        mDriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RegisterDriverActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterDriverActivity.this, MapDriverActivity.class);
                    // nos aseguramos que counado precione el boton de atras no me lleve al registro sino se quede en el mapa o se cierre le app
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    Toast.makeText(RegisterDriverActivity.this, getString(R.string.error_register_user), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void gotoLogin() {
        Intent intent = new Intent(RegisterDriverActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
