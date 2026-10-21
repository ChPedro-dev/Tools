package com.pz.api_previsao.util;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pz.dto.CidadeDto;

@Service
public class LerJson extends LeitorJson {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<CidadeDto> ler() throws IOException {
        String path = "static/Json/municipios_pr.json";
        String jsonText = Ler(path);
        return MAPPER.readValue(jsonText, new TypeReference<List<CidadeDto>>() {});
    }

}
