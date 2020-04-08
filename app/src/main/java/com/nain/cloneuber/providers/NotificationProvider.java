package com.nain.cloneuber.providers;

import com.nain.cloneuber.models.FCMBody;
import com.nain.cloneuber.models.FCMResponse;
import com.nain.cloneuber.retrofit.IFCMApi;
import com.nain.cloneuber.retrofit.retrofitClient;

import retrofit2.Call;

public class NotificationProvider {
    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body){
        return retrofitClient.getClientObject(url).create(IFCMApi.class).send(body);
    }
}
