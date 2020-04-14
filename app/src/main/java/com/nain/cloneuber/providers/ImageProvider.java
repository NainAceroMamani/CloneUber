package com.nain.cloneuber.providers;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nain.cloneuber.utils.CompressorBitmapImage;

import java.io.File;

public class ImageProvider {

    private StorageReference mstorage;

    public ImageProvider(String ref){
        mstorage = FirebaseStorage.getInstance().getReference().child(ref);
    }

    public UploadTask saveImagen(Context context, File image, String idUser){
        byte[] ImageByte = CompressorBitmapImage.getImage(context, image.getPath(),500,500);
        final StorageReference storageReference = mstorage.child(idUser + ".jpg");
        mstorage = storageReference; // le asignamos otro ruta para retornarlo
        UploadTask uploadTask = storageReference.putBytes(ImageByte); // subimos la img a fireabse
        return uploadTask;
    }

    public StorageReference getStorage(){
        return mstorage;
    }
}
