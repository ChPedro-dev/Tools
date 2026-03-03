package com.pz.api_previsao.model.Previsao;

import org.springframework.web.client.RestTemplate;

public class RequestApi {

    private static void getCwbPrev(String data_inicial, String data_final, String codigo_estacao) {
        StringBuilder UriInmet = new StringBuilder() ;

        final String api_key = RequestApiKey.getINMET();

        UriInmet.append("apitempo.inmet.gov.br/token/estacao/");
        UriInmet.append("/"+ data_inicial + "/" + data_final);
        UriInmet.append("/"+ codigo_estacao);
        UriInmet.append("/"+ api_key);

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(UriInmet.toString(), String.class);
        System.out.println(api_key);
        System.out.println(result);
    }

}
