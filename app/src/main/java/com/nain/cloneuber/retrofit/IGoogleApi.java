package com.nain.cloneuber.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleApi {
    // la peticion te retorna string
    @GET
    Call<String> getDirections(@Url String url);

}
