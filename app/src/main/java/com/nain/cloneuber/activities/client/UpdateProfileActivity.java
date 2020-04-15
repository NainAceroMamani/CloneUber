package com.nain.cloneuber.activities.client;

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
import com.nain.cloneuber.includes.MyToolbar;
import com.nain.cloneuber.models.Client;
import com.nain.cloneuber.providers.AuthProvider;
import com.nain.cloneuber.providers.ClientProvider;
import com.nain.cloneuber.providers.ImageProvider;
import com.nain.cloneuber.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UpdateProfileActivity extends AppCompatActivity {

    private ImageView mImageViewProfile;
    private Button mButtonUpdate;
    private EditText mTextViewName;

    private ClientProvider clientProvider;
    private AuthProvider authProvider;

    private File mImageFile; // para abrir la galeria
    private String mImage; // para almacenar la url
    private final int GALERY_REQUEST = 1;

    private ProgressDialog progressDialog;
    private String name;
    private ImageProvider imageProvider;

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
        imageProvider = new ImageProvider("client_images");

        progressDialog = new ProgressDialog(this);

        mImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cuando presione click abrimos la galeria
                openGalery();
            }
        });

        getClientInfo();

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
    private void getClientInfo(){
        // addListenerForSingleValueEvent => solo obtener info una unica vez
        clientProvider.getClient(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    mTextViewName.setText(name);
                    String image = "";
                    // validar que contenga imagen porque puede ser que el usuario no tenga imagen
                    if(dataSnapshot.hasChild("image")){
                        image = dataSnapshot.child("image").getValue().toString();
                        // mostramos la imagen
                        Picasso.with(UpdateProfileActivity.this).load(image).into(mImageViewProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile() {
        name = mTextViewName.getText().toString(); // nombre que ingreso en el input
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
//        byte[] ImageByte = CompressorBitmapImage.getImage(this, mImageFile.getPath(),500,500);
//        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("client_images").child(authProvider.getId() + ".jpg");
//        UploadTask uploadTask = storageReference.putBytes(ImageByte); // subimos la img a fireabse
//        uploadTask.addOnCompleteListener

        imageProvider.saveImagen(UpdateProfileActivity.this, mImageFile, authProvider.getId()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    // preguntamos si se subio la imagen correctamente
                    // OBTENEMOS EL LINK PARA PODER MOSTRAR LA IMAGEN
//                    storageReference.getDownloadUrl().addOnSuccessListener
                    imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // uri => la url de la imagen que esta apuntando
                            String image = uri.toString();
                            // incluimos la url de la img a Firebase Database
                            Client client = new Client();
                            client.setImagen(image);
                            client.setId(authProvider.getId());
                            client.setName(name);
                            clientProvider.update(client).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss(); // ocultamos el dialog
                                    // si actualizo correctamente
                                    Toast.makeText(UpdateProfileActivity.this, "Su información se actualizó correctamente", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }else {
                    Toast.makeText(UpdateProfileActivity.this, R.string.txt_img_upload, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
