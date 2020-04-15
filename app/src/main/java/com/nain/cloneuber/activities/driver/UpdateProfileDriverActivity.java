package com.nain.cloneuber.activities.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.nain.cloneuber.R;
import com.nain.cloneuber.activities.client.UpdateProfileActivity;
import com.nain.cloneuber.includes.MyToolbar;
import com.nain.cloneuber.models.Driver;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.DriverProvider;
import com.nain.cloneuber.providers.ImageProvider;
import com.nain.cloneuber.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UpdateProfileDriverActivity extends AppCompatActivity {
    private ImageView mImageViewProfile;
    private Button mButtonUpdate;
    private EditText mTextViewName, mTextViewMarca, mTextViewPlaca;

    private DriverProvider driverProvider;
    private AuthProvider authProvider;

    private File mImageFile; // para abrir la galeria
    private String mImage; // para almacenar la url
    private final int GALERY_REQUEST = 1;

    private ProgressDialog progressDialog;
    private String name;
    private String vehiculeBrand;
    private String vehiculePlate;

    private ImageProvider imageProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_driver);
        MyToolbar.show(this, "Actualizar Perfil", true);

        mImageViewProfile = findViewById(R.id.imageViewProfile);
        mTextViewName = findViewById(R.id.txtInputNombre);
        mButtonUpdate = findViewById(R.id.btnProfileUpdate);
        mTextViewMarca = findViewById(R.id.txtInputMarca);
        mTextViewPlaca = findViewById(R.id.txtInputPlaca);

        driverProvider = new DriverProvider();
        authProvider = new AuthProvider();

        progressDialog = new ProgressDialog(this);

        imageProvider = new ImageProvider("driver_images");

        mImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cuando presione click abrimos la galeria
                openGalery();
            }
        });

        getDriverInfo();

        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void openGalery() {
        Intent galeryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galeryIntent.setType("image/*");
        startActivityForResult(galeryIntent, GALERY_REQUEST); // esto nos devolvera un resultado si el usuario selecciono una imagen o no
    }

    // saber si el usuario seleciono o n o una imagen de la galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALERY_REQUEST && resultCode == RESULT_OK){
            try {
                // para mostrar la imagen en la pantalla
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e) {
                Log.d("ERROR", "Mensaje: " + e.getMessage());
            }
        }
    }

    // traer la info del cliente autentificado
    private void getDriverInfo(){
        // addListenerForSingleValueEvent => solo obtener info una unica vez
        driverProvider.getDriver(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String marca = dataSnapshot.child("VehicleBrand").getValue().toString();
                    String placa = dataSnapshot.child("VehiclePlate").getValue().toString();
                    String image = "";
                    // validar que contenga imagen porque puede ser que el usuario no tenga imagen
                    if(dataSnapshot.hasChild("image")){
                        image = dataSnapshot.child("image").getValue().toString();
                        // mostramos la imagen
                        Picasso.with(UpdateProfileDriverActivity.this).load(image).into(mImageViewProfile);
                    }
                    mTextViewName.setText(name);
                    mTextViewMarca.setText(marca);
                    mTextViewPlaca.setText(placa);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile() {
        name = mTextViewName.getText().toString(); // nombre que ingreso en el input
        vehiculeBrand = mTextViewMarca.getText().toString();
        vehiculePlate = mTextViewPlaca.getText().toString();
        if(!name.equals("") && mImageFile != null){
            // validar que el nombre no este vacio y que si
            progressDialog.setMessage("Espere un momento...");
            progressDialog.setCanceledOnTouchOutside(false); // para que no pueda cancelar este progress bar
            progressDialog.show();

            saveImage();
        }else {
            Toast.makeText(this, R.string.txt_required_img_name, Toast.LENGTH_LONG).show();
        }
    }

    private void saveImage() {
        // comprimir la img
        imageProvider.saveImagen(UpdateProfileDriverActivity.this, mImageFile, authProvider.getId()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    // preguntamos si se subio la imagen correctamente
                    // OBTENEMOS EL LINK PARA PODER MOSTRAR LA IMAGEN
                    imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // uri => la url de la imagen que esta apuntando
                            String image = uri.toString();
                            // incluimos la url de la img a Firebase Database
                            Driver driver = new Driver();
                            driver.setImagen(image);
                            driver.setId(authProvider.getId());
                            driver.setName(name);
                            driver.setVehicleBrand(vehiculeBrand);
                            driver.setVehiclePlate(vehiculePlate);
                            driverProvider.update(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss(); // ocultamos el dialog
                                    // si actualizo correctamente
                                    Toast.makeText(UpdateProfileDriverActivity.this, "Su información se actualizó correctamente", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }else {
                    Toast.makeText(UpdateProfileDriverActivity.this, R.string.txt_img_upload, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
