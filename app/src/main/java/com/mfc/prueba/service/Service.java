package com.mfc.prueba.service;

import com.mfc.prueba.model.ResponseApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by USUARIO on 03/10/2017.
 */

public interface Service {

    @GET("/forecast/{api_key}/{latlng}")
    Call<ResponseApi> getWeather (@Path("api_key")String api_key,@Path("latlng")String latlng);
}
