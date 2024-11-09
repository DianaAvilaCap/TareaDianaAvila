package com.codigo.resttemplate.service.impl;

import com.codigo.resttemplate.aggregates.constants.Constants;
import com.codigo.resttemplate.aggregates.response.ResponseSunat;
import com.codigo.resttemplate.redis.RedisService;
import com.codigo.resttemplate.service.EmpresaService;
import com.codigo.resttemplate.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final RestTemplate restTemplate;
    private final RedisService redisService;
    @Value("${token.api}")
    private String token;

    public EmpresaServiceImpl(RestTemplate restTemplate, RedisService redisService) {
        this.restTemplate = restTemplate;
        this.redisService = redisService;
    }

    @Override
    public ResponseSunat getInfoSunat(String ruc) {
        ResponseSunat datosSunat = new ResponseSunat();
        //Recuperar información de Redis
        String redisInfo = redisService.getDataFromRedis(Constants.REDIS_KEY_API_SUNAT+ruc);
        //Validar si se encontró registro
        if(Objects.nonNull(redisInfo)){
            datosSunat = Util.convertirDesdeString(redisInfo,ResponseSunat.class);
        }else{
            //No existe en redis -> Consultar directo a Sunat
            datosSunat = executeRestTemplate(ruc);
            //Convertir respuesta de sunat a string
            String dataForRedis = Util.convertirAString(datosSunat);
            //Guardar en redis
            redisService.saveInRedis(Constants.REDIS_KEY_API_SUNAT+ruc,dataForRedis,Constants.REDIS_TTL);
        }
        return datosSunat;
    }

    private ResponseSunat executeRestTemplate(String ruc){
        //Configurar una URL completa como String
        String url = Constants.BASE_URL+"/v2/sunat/ruc/full?numero="+ruc;
        //Generar mi Client RestTemplate y ejecuto
        ResponseEntity<ResponseSunat> executeRestTemplate = restTemplate.exchange(
                url, //URL A LA CUAL VAS A EJECUTAR
                HttpMethod.GET, //TIPO DE SOLICITUD AL QUE LE PERTENECE LA URL
                new HttpEntity<>(createHeaders()), //CABECERA || HEADERS
                ResponseSunat.class //RESPONSE A CASTEAR

        );

        if(executeRestTemplate.getStatusCode().equals(HttpStatus.OK)){
            return executeRestTemplate.getBody();
        }
        return null;
    }

    private HttpHeaders createHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+token);
        return headers;
    }
}
