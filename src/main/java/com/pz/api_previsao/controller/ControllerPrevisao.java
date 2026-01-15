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

import com.pz.api_previsao.model.ApiToFile;
import com.pz.api_previsao.model.Previsao.ServicoPrevisao;
import com.pz.api_previsao.model.TimerPrevisao;

@RestController
@RequestMapping("/previsao")
public class ControllerPrevisao {

    ApiToFile file = new ApiToFile();
    ServicoPrevisao dados = new ServicoPrevisao();
    TimerPrevisao timer = new TimerPrevisao();

    List<Map<String,Object>> dadosCache = new ArrayList() ;


    String acao;
    // String url = "/home/pedro/Documentos/dados.json";
    String caminhoPasta = "/mnt/dados-windows/PREVISAO DO TEMPO/JSON/dados.json";

    private String getTime() {

        LocalTime agora = LocalTime.now();
        DateTimeFormatter formatadorCompleto = DateTimeFormatter.ofPattern("HH:mm");
        String dataHoraFormatada = agora.format(formatadorCompleto);
        return dataHoraFormatada;
    }

    @GetMapping("/cache")
    public List<Map<String,Object>> CacheDados(){
            return dadosCache;
    }

    @PostMapping("/scrap")
    public List<Map<String, Object>> Previsao(@RequestParam(name = "acao") String acao) throws InterruptedException {

        List<Map<String, Object>> listaPrevisao = dados.servico(acao);

       dadosCache = listaPrevisao;

        file.convert(caminhoPasta, listaPrevisao);

        timer.setUltimaAtualizacao(getTime() + acao );

        return listaPrevisao;

    }

    @GetMapping("/status")
    public String getMethodName() {
        return timer.getUltimaAtualizacao();
    }

}
