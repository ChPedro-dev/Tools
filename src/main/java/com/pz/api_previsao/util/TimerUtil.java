package com.pz.api_previsao.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pz.api_previsao.integration.ApiToFile;
import com.pz.api_previsao.service.PrevisaoService;

@Component
public class TimerUtil {
    @Autowired
    private ApiToFile file;

    @Autowired
    private PrevisaoService previsaoService;

    // String url = "C:/Users/pedro/Downloads/dados.json";
    public String url = "/mnt/dados-windows/PREVISAO DO TEMPO/JSON/dados.json";

    private String ultimaAtualizacao;

    private String getTime() {
        LocalTime agora = LocalTime.now();
        DateTimeFormatter formatadorCompleto = DateTimeFormatter.ofPattern("HH:mm");
        String dataHoraFormatada = agora.format(formatadorCompleto);
        return dataHoraFormatada;
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void getPrevisaoBDl() {
        try {
            file.convert(url, previsaoService.servico("BDL"));
            setUltimaAtualizacao(getTime() + " (BDL)");
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Scheduled(cron = "0 1 3 * * ?")
    public void getPrevisaoLU() {
        try {
            file.convert(url, previsaoService.servico("LU"));
            setUltimaAtualizacao(getTime() + " (LU)");
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public String getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(String ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}
