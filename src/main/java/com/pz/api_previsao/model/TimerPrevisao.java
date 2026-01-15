package com.pz.api_previsao.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pz.api_previsao.model.Previsao.ServicoPrevisao;

@Component
public class TimerPrevisao {

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
        ApiToFile file = new ApiToFile();
        ServicoPrevisao dados = new ServicoPrevisao();

        try {
            file.convert(url, dados.servico("BDL"));
            setUltimaAtualizacao(getTime() + " (BDL)");
        } catch (InterruptedException e) {

        }

    }

    @Scheduled(cron = "0 1 3 * * ?")
    public void getPrevisaoLU() {
        ApiToFile file = new ApiToFile();
        ServicoPrevisao dados = new ServicoPrevisao();

        try {
            file.convert(url, dados.servico("LU"));
            setUltimaAtualizacao(getTime() + " (LU)");
        } catch (InterruptedException e) {

        }

    }

    public String getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(String ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

}
