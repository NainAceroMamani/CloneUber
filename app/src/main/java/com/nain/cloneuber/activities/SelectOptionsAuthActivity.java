package com.nain.cloneuber.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.client.RegisterActivity;
import com.nain.cloneuber.activities.driver.RegisterDriverActivity;
import com.nain.cloneuber.includes.MyToolbar;

public class SelectOptionsAuthActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_options_auth);

        // clase creada en includes para mostrar el Toolbar
        MyToolbar.show(this, getString(R.string.txt_option), true);

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);


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
        String typeUser = mPref.getString("user", "");
        if(typeUser.equals("client")) {
            Intent intent = new Intent(SelectOptionsAuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if(typeUser.equals("driver")) {
            Intent intent = new Intent(SelectOptionsAuthActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        }
    }
}
