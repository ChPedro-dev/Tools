package com.pz.api_previsao.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pz.api_previsao.service.PrevisaoService;
import com.pz.api_previsao.integration.ApiToFile;

@RestController
@RequestMapping("/previsao")
public class PrevisaoController {

    final ApiToFile file;
    final PrevisaoService dados;

    PrevisaoController(PrevisaoService dados, ApiToFile file) 
    {
        this.file = file;
        this.dados = dados;
    }

    List<Map<String, Object>> dadosCache;

    String acao;
    String ultimaAtualizacao;
    String url;

    private String getTime() 
    {
        LocalTime agora = LocalTime.now();
        DateTimeFormatter formatadorCompleto = DateTimeFormatter.ofPattern("HH:mm");
        String dataHoraFormatada = agora.format(formatadorCompleto);
        return dataHoraFormatada;
    }

    @GetMapping("/cache")
    public List<Map<String, Object>> CacheDados() 
    {
        return dadosCache;
    }

    @PostMapping("/scrap")
    public List<Map<String, Object>> Previsao(@RequestParam(name = "acao") String acao) throws InterruptedException 
    {    
        List<Map<String, Object>> listaPrevisao = dados.servico(acao);

        dadosCache = listaPrevisao; 

        ultimaAtualizacao = getTime();

       // File.convert(url, listaPrevisao);

        return listaPrevisao;

    }

    @GetMapping("/status")
    public String getMethodName() 
    {
        return ultimaAtualizacao;
    }

}
