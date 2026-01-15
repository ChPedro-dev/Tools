package com.pz.api_previsao.model.Previsao;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.pz.api_previsao.model.Driver;

public class ServicoPrevisao {

    public List<Map<String, Object>> servico(String acao) throws InterruptedException {

        normalizacao norma = new normalizacao();
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

        WebDriver driver = Driver.getDriver();

        Dados dados = new Dados();

        // lista com TODAS as cidades
        List<Map<String, Object>> listaCidades = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : cidadeMap.entrySet()) {

            driver.get("https://www.simepar.br/simepar/forecast_by_counties/" + entry.getValue());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(0, 5));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("did-data")));

            int[] max = dados.maximas(driver);
            int[] min = dados.minimas(driver);
            String[] icones = dados.icones(driver);
            String[] dia = dados.dia(driver);

            String[] infos = dados.infos(driver);
            String[] infos2 = dados.infos2(driver);
            System.out.println("Cidade: " + entry.getKey());

            Map<String, Object> cidadeJson = new LinkedHashMap<>();
            cidadeJson.put("cidade", entry.getKey());

            List<Map<String, Object>> previsoes = new ArrayList<>();

            for (int i = 0; i < 5; i++) {

                Map<String, Object> diaJson = new LinkedHashMap<>();
                diaJson.put("dia", dia[i]);
                diaJson.put("maxima", String.valueOf(max[i]));
                diaJson.put("minima", String.valueOf(min[i] == 0 ? max[i] - 2 : min[i]));

                norma.setCondicao(icones[i]);
                diaJson.put("condicao", norma.getCondicao());

                norma.setIconeIndex(icones[i]);
                diaJson.put("index", norma.getIconeIndex());

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
