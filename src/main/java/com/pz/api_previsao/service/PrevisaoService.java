package com.pz.api_previsao.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pz.api_previsao.scraper.simepar.DadosScraper;
import com.pz.api_previsao.scraper.simepar.DriverFactory;
import com.pz.api_previsao.util.Normalizacao;

@Service
public class PrevisaoService {

    @Autowired
    Normalizacao normalizacao;

    @Autowired
    DadosScraper dados;

    private void sleepAleatorio() throws InterruptedException {
        int tempo = ThreadLocalRandom.current().nextInt(3000, 10001);
        Thread.sleep(tempo);
    }

    public List<Map<String, Object>> servico(String acao) throws InterruptedException {

        Map<String, Integer> cidadeMap = new LinkedHashMap<>();

        cidadeMap.put("Paranaguá", 4118204);
        cidadeMap.put("Antonina", 4101200);
        cidadeMap.put("Morretes", 4116208);
        cidadeMap.put("Pontal do PR", 4119954);
        cidadeMap.put("Matinhos", 4115705);
        cidadeMap.put("Guaratuba", 4109609);
        cidadeMap.put("Guaraqueçaba", 4109500);
        cidadeMap.put("Curitiba", 4106902);
        cidadeMap.put("Ponta Grossa", 4119905);
        cidadeMap.put("Fazenda Rio Grande", 4107652);
        cidadeMap.put("Guarapuava", 4109401);
        cidadeMap.put("Cascavel", 4104808);
        cidadeMap.put("Foz do Iguaçu", 4108304);
        cidadeMap.put("Londrina", 4113700);
        cidadeMap.put("Maringá", 4115200);
        cidadeMap.put("Paranavaí", 4118402);

        WebDriver driver = DriverFactory.getDriver();

        // lista com TODAS as cidades
        List<Map<String, Object>> listaCidades = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : cidadeMap.entrySet()) {

            driver.get("https://www.simepar.br/simepar/forecast_by_counties/" + entry.getValue());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5, 8));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("did-data")));

            int[] max = dados.maximas(driver);
            int[] min = dados.minimas(driver);
            sleepAleatorio();

            String[] icones = dados.icones(driver);
            String[] dia = dados.dia(driver);
            sleepAleatorio();

            String[] infos = dados.infos(driver);
            String[] infos2 = dados.infos2(driver);
            sleepAleatorio();

            System.out.println("Cidade: " + entry.getKey());

            Map<String, Object> cidadeJson = new LinkedHashMap<>();
            cidadeJson.put("cidade", entry.getKey());
            sleepAleatorio();

            List<Map<String, Object>> previsoes = new ArrayList<>();

            for (int i = 0; i < 5; i++) {

                Map<String, Object> diaJson = new LinkedHashMap<>();
                diaJson.put("dia", dia[i]);
                diaJson.put("maxima", String.valueOf(max[i]));
                diaJson.put("minima", String.valueOf(min[i] == 0 ? max[i] - 2 : min[i]));

                normalizacao.setCondicao(icones[i]);
                diaJson.put("condicao", normalizacao.getCondicao());

                normalizacao.setIconeIndex(icones[i]);
                diaJson.put("index", normalizacao.getIconeIndex());

                switch (i) {
                    case 0 -> {
                        diaJson.put("precipitacao", infos[0]);
                        diaJson.put("probabilidade", infos[1]);
                        diaJson.put("vento", infos[2]);
                    }
                    case 1 -> {
                        diaJson.put("precipitacao", infos2[0]);
                        diaJson.put("probabilidade", infos2[1]);
                        diaJson.put("vento", infos2[2]);
                    }
                    default -> {
                        diaJson.put("precipitacao", "");
                        diaJson.put("probabilidade", "");
                        diaJson.put("vento", "");
                    }
                }
                previsoes.add(diaJson);
            }

            cidadeJson.put("previsoes", previsoes);
            listaCidades.add(cidadeJson); // adiciona cidade na lista

            if (acao.equals("BDL") && entry.getKey().equals("Fazenda Rio Grande") == true) {
                break;
            }
        }

        driver.close();
        return listaCidades; // retorna TODAS

    }

}
