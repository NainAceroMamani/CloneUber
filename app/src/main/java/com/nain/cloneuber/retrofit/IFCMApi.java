package com.nain.cloneuber.retrofit;

import com.nain.cloneuber.models.FCMBody;
import com.nain.cloneuber.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAU6h06n4:APA91bHGXW2o4F3rH5qSzXyDtQBBndOEJU6fMylcPNz0m-sdZcYXvdaz2PfjIwpzJOCT6Dbdv4RfOd0rqp-zAA76vusGwwFvYRwd9-qwn9C0R5l2vEUv2_SV0D_buDXctc47fyfW0sGI"
    })
    @POST("fcm/send")
    // la respuesta de fireabse se almacena en FCMResponse
    Call<FCMResponse> send(@Body FCMBody body);

}
