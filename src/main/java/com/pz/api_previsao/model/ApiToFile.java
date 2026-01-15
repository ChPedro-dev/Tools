package com.pz.api_previsao.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

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
