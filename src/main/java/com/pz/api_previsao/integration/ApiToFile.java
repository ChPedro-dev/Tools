package com.pz.api_previsao.integration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApiToFile {
    
    public void convert(String caminho, List<Map<String, Object>> dados) {
        try {
            // Instancia o ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Converte a lista para JSON
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dados);

            try ( // Salva no arquivo
                    FileWriter file = new FileWriter(caminho)) {
                file.write(json);
            }

        } catch (IOException e) {

        }
    }
}
