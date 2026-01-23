package com.pz.api_previsao.models;

import org.springframework.web.client.RestClient;


public class InmetServiceApi {

    public static void main(String[] args) {
    
        RestClient restClient = RestClient.create();
        String result = restClient.get()
                .uri("http://servicos.cptec.inpe.br/XML/listaCidades")
                .retrieve()
                .body(String.class);
        System.out.println(result);
    }

}
