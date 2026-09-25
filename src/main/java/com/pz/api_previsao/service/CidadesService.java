package com.pz.api_previsao.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pz.api_previsao.util.LerJson;
import com.pz.dto.CidadeDto;

@Service 
public class CidadesService {

    public List<CidadeDto> cidades() throws IOException{

        return LerJson.ler();
        
    } 







}
