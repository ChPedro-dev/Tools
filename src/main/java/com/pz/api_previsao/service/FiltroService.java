package com.pz.api_previsao.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pz.api_previsao.model.Cidade;
import com.pz.api_previsao.util.LerJson;
import com.pz.dto.CidadeDto;

@Service 
public class FiltroService {

    List<CidadeDto> cidadesIbge;
    
    public List<Cidade> selecionarIds(List<CidadeDto> cidadeSelecionadas) throws IOException {
        
        if (cidadesIbge == null || cidadesIbge.isEmpty()) {
            cidadesIbge = LerJson.ler();
        }

      
        Map<String, Integer> mapaCidades = new HashMap<>();
        for (CidadeDto cidadeOficial : cidadesIbge) {
            if (cidadeOficial.getNome() != null) {
                mapaCidades.put(cidadeOficial.getNome().toLowerCase(), cidadeOficial.getId());
            }
        }

        
        List<Cidade> cidadesCompletas = new ArrayList<>();

       
        for (CidadeDto dto : cidadeSelecionadas) {
            if (dto.getNome() != null) {
                String nomeBuscado = dto.getNome().toLowerCase();
                
                if (mapaCidades.containsKey(nomeBuscado)) {
                    Integer idEncontrado = mapaCidades.get(nomeBuscado);
                    dto.setId(idEncontrado);
                    
                    Cidade cidade = new Cidade();
                    cidade.setId(idEncontrado);
                    cidade.setNome(dto.getNome());
                    
                    cidadesCompletas.add(cidade);
                } else {
                    System.out.println("Cidade não encontrada no JSON: " + dto.getNome());
                }
            }   
        }
        
        return cidadesCompletas;
    }


}
