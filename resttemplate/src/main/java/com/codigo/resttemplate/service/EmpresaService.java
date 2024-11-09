package com.codigo.resttemplate.service;

import com.codigo.resttemplate.aggregates.response.ResponseSunat;

public interface EmpresaService {
    ResponseSunat getInfoSunat(String ruc);
}
