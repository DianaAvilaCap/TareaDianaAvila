package com.codigo.retrofit.service.impl;

import com.codigo.retrofit.aggregates.constants.Constants;
import com.codigo.retrofit.aggregates.response.ResponseSunat;
import com.codigo.retrofit.redis.RedisService;
import com.codigo.retrofit.retrofit.ClientSunatService;
import com.codigo.retrofit.retrofit.impl.ClientSunatServiceImpl;
import com.codigo.retrofit.service.EmpresaService;
import com.codigo.retrofit.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final RedisService redisService;

    ClientSunatService sunatServiceRetrofit = ClientSunatServiceImpl
            .getRetrofit()
            .create(ClientSunatService.class);

    @Value("${token.api}")
    private String token;

    public EmpresaServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }


    @Override
    public ResponseSunat getInfoSunat(String ruc) throws IOException {
        ResponseSunat datosSunat = new ResponseSunat();

        //Obtener data de redis
        String redisInfo = redisService.getDataFromRedis(Constants.REDIS_KEY_API_SUNAT+ruc);
        //Validar si se encontró data
        if(Objects.nonNull(redisInfo)){
            datosSunat = Util.convertirDesdeString(redisInfo,ResponseSunat.class);
        }else{
            //Preparo llamada a Sunat
            Call<ResponseSunat> clienteRetrofit = prepareSunatRetrofit(ruc);
            //Ejecutar llamada a sunat
            Response<ResponseSunat> executeSunat = clienteRetrofit.execute();
            //Validar resultado de api externa
            if(executeSunat.isSuccessful() && Objects.nonNull(executeSunat.body())){
                datosSunat = executeSunat.body();
            }
            //Convertir respuesta a String
            String dataForRedis = Util.convertirAString(datosSunat);
            //Guardar en redis
            redisService.saveInRedis(Constants.REDIS_KEY_API_SUNAT+ruc,dataForRedis,Constants.REDIS_TTL);
        }

        return datosSunat;
    }

    //Metodo que ejecuta el client Retrofit de Sunat
    private Call<ResponseSunat> prepareSunatRetrofit(String dni){
        String tokenComplete = Constants.BEARER + token;
        return sunatServiceRetrofit.getInfoSunat(tokenComplete,dni);
    }

}
