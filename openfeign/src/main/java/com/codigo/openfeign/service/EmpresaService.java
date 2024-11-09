package com.codigo.openfeign.service;

import com.codigo.openfeign.aggregates.response.ResponseSunat;

public interface EmpresaService {
    ResponseSunat getInfoSunat(String ruc);
}
