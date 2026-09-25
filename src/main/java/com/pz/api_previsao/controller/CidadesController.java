package com.pz.api_previsao.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pz.api_previsao.service.CidadesService;
import com.pz.dto.CidadeDto;


@RestController
@RequestMapping("/cidades")
public class CidadesController {

    final CidadesService service;

    CidadesController(CidadesService cidade){

        this.service = cidade;
    }


    @GetMapping()
    public List<CidadeDto> ListaCidades() throws IOException {
        return service.cidades();
    }
    


}
