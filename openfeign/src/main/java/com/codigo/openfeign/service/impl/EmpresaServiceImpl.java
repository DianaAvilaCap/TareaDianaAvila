package com.codigo.openfeign.service.impl;

import com.codigo.openfeign.aggregates.constants.Constants;
import com.codigo.openfeign.aggregates.response.ResponseSunat;
import com.codigo.openfeign.client.ClientSunat;
import com.codigo.openfeign.redis.RedisService;
import com.codigo.openfeign.service.EmpresaService;
import com.codigo.openfeign.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final ClientSunat clientSunat;
    private final RedisService redisService;

    @Value("${token.api}")
    private String token;

    public EmpresaServiceImpl(ClientSunat clientSunat, RedisService redisService) {
        this.clientSunat = clientSunat;
        this.redisService = redisService;
    }


    @Override
    public ResponseSunat getInfoSunat(String ruc) {
        ResponseSunat datosSunat = new ResponseSunat();

        //Obtener data de redis
        String redisInfo = redisService.getDataFromRedis(Constants.REDIS_KEY_API_SUNAT+ruc);
        //Validar si se encontró data
        if(Objects.nonNull(redisInfo)){
            datosSunat = Util.convertirDesdeString(redisInfo,ResponseSunat.class);
        }else{
            //Consultar a Sunat
            datosSunat = executionSunat(ruc);
            //Convertir respuesta a String
            String dataForRedis = Util.convertirAString(datosSunat);
            //Guardar en redis
            redisService.saveInRedis(Constants.REDIS_KEY_API_SUNAT+ruc,dataForRedis,Constants.REDIS_TTL);
        }

        return datosSunat;
    }

    private ResponseSunat executionSunat(String ruc){
        String tokenOK = Constants.BEARER+token;
        return clientSunat.getEmpresaSunat(ruc,tokenOK);
    }
}
