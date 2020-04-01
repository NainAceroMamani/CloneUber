package com.nain.cloneuber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SelectOptionsAuthActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;
    Toolbar mToolbar; // para menu en la parte superior para volver hacia atras

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_options_auth);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.txt_option);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegister();
            }
        });
    }

    private void gotoLogin() {
        Intent intent = new Intent(SelectOptionsAuthActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void gotoRegister()  {
        Intent intent = new Intent(SelectOptionsAuthActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
