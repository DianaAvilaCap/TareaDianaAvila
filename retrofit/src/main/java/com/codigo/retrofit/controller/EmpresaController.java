package com.codigo.retrofit.controller;

import com.codigo.retrofit.aggregates.response.ResponseSunat;
import com.codigo.retrofit.service.EmpresaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/empresa")
public class EmpresaController {
    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/{ruc}")
    public ResponseEntity<ResponseSunat> getInfoSunat(@PathVariable String ruc) throws IOException {
        return new ResponseEntity<>(empresaService.getInfoSunat(ruc), HttpStatus.OK);
    }

}
