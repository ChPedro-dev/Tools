package com.pz.api_previsao.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pz.dto.CidadeDto;

@Service 
public class LerJson extends LeitorJson{

    

    public static List<CidadeDto> ler() throws IOException{
        
        String path = "./src/main/resources/static/Json/municipios_pr.json";

        String jsonText = Ler(path);
        Type collectionType = new TypeToken<List<CidadeDto>>(){}.getType();
        return new Gson().fromJson(jsonText,collectionType);

    }    

}
