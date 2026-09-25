package com.pz.api_previsao.controller;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pz.api_previsao.integration.ApiToFile;
import com.pz.api_previsao.model.Cidade;
import com.pz.api_previsao.service.PrevisaoService;
import com.pz.api_previsao.service.PrevisaoServiceCwb;
import com.pz.dto.CidadeDto;


@RestController
@RequestMapping("/previsao")
public class PrevisaoController {

    final ApiToFile file;
    final PrevisaoService dados;
    final PrevisaoServiceCwb prevCwb;

    PrevisaoController(PrevisaoService dados, ApiToFile file, PrevisaoServiceCwb prevCwb) 
    {
        this.file = file;
        this.dados = dados;
        this.prevCwb = prevCwb;
    }

    List<Map<String, Object>> dadosCache;
    List<Cidade> dadosCache2;
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

    @PostMapping("/scrap/pgua")
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

    @PostMapping("/scrap/cwb")
    public List<Cidade> previsaoCwb(@RequestParam(name = "acao") String acao,@RequestBody List<CidadeDto> cidadesDto) throws InterruptedException, IOException {
        
        List<Cidade> dadosCidades = prevCwb.servico(acao, cidadesDto);

        dadosCache2 = dadosCidades;

        ultimaAtualizacao = getTime(); 

        return dadosCidades;
    }
    

}
