package com.pz.api_previsao.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/salvar/lista")
    public void SalvarLista(@RequestBody List<CidadeDto> cidades) {
        
        service.SalvarLista(cidades);
        
    }
    
    @GetMapping("/lista")
    public List<CidadeDto> ListaCidadesSalvas() {
        
        return service.ListaCidades();
    }
    
    


}
