package com.codigo.retrofit.retrofit;

import com.codigo.retrofit.aggregates.response.ResponseSunat;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ClientSunatService {

    @GET("/v2/sunat/ruc/full")
    Call<ResponseSunat> getInfoSunat(@Header("Authorization") String token,
                                     @Query("numero") String numero);
}
