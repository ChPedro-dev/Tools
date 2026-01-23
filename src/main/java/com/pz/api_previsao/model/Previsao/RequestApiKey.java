package com.pz.api_previsao.model.Previsao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RequestApiKey {

    @Value("${external.service.INMET-api-key}")
    private static String INMET_API_KEY;
    
    @PostConstruct
    public static String getINMET() {
        return INMET_API_KEY;
    }

    

    

    
}
