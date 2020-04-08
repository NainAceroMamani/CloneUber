package com.nain.cloneuber.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class retrofitClient {

    public static Retrofit getClient(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        return retrofit;
    }

    // method para enviar notificaciones de dispositivo a dispositivo
    public static Retrofit getClientObject(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create()) // para los servicios de firebase
                    .build();
        return retrofit;
    }
}
