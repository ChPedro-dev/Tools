package com.pz.api_previsao.service;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import com.pz.api_previsao.model.Cidade;
import com.pz.api_previsao.model.Dia;
import com.pz.api_previsao.scraper.simepar.DadosScraper;
import com.pz.api_previsao.scraper.simepar.DriverFactory;
import com.pz.api_previsao.util.Normalizacao;
import com.pz.api_previsao.util.SleepAleatorio;
import com.pz.dto.CidadeDto;

@Service 
public class PrevisaoServiceCwb {

    final Normalizacao normalizacao;
    final DadosScraper dados;
    final SleepAleatorio sleepAleatorio;
    final FiltroService filtro;

    PrevisaoServiceCwb(Normalizacao normalizacao, DadosScraper dados, SleepAleatorio sleep, FiltroService filtro) {
        this.normalizacao = normalizacao;
        this.dados = dados;
        this.sleepAleatorio = sleep;
        this.filtro = filtro;
    }

    public List<Cidade> servico(String acao, List<CidadeDto> cidadesSelecionadas) throws InterruptedException, IOException {

        WebDriver driver = DriverFactory.getDriver();

        List<Cidade> cidades = filtro.selecionarIds(cidadesSelecionadas);

        for (Cidade cidade : cidades) {

            driver.get("https://www.simepar.br/simepar/forecast_by_counties/" + cidade.getId());

            System.out.println("Cidade: " + cidade.getId());

            Thread.sleep(15000); // espera a página carregar

            int[] max = dados.maximas(driver);
            int[] min = dados.minimas(driver);

            String[] icones = dados.icones(driver);
            String[] dia = dados.dia(driver);

            String[] infos1 = new String[5] ;
            String[] infos2;

            switch(acao){
                case "BDL" ->{
                    infos1 = dados.infos(driver);
                } 
                case "LU" -> {
                    infos2 = dados.infos2(driver);
                }
                case "18h" -> {
                    infos1 = new String[] { "", "", "" };
                    infos2 = new String[] { "", "", "" };
                }
                default ->{
                    infos1 = new String[] { "", "", "" };
                    infos2 = new String[] { "", "", "" };
                }
            }


            if (acao.equals("LU")) {
                infos2 = dados.infos2(driver);
            } else {
                infos2 = new String[] { "", "", "" };
            }

            for (int i = 0; i < cidades.size(); i++) {

                // Puxa a lista de dias apenas uma vez por cidade
               // List<Dia> dias = cidades.get(i).getDias();
                Dia[] dias = cidades.get(i).getDias(); 

                for (int j = 0; j < 5; j++) {

                    Dia diaAtual = new Dia();

                    diaAtual.setDia(dia[j]);
                    diaAtual.setMaxima(max[j]);
                    diaAtual.setMinima(min[j] == 0 ? max[j] - 2 : min[j]);

                    normalizacao.setIconeIndex(icones[j]);
                    diaAtual.setIconeIndex(normalizacao.getIconeIndex());

                    normalizacao.setCondicao(icones[j]);
                    diaAtual.setCondicao(normalizacao.getCondicao());


                    switch (i) {
                        case 0 -> {
                            // ATENÇÃO: infos[0] vai se repetir para todos os 'j' desta cidade
                            diaAtual.setMilimetros(infos1[0]);
                            diaAtual.setProb(infos1[1]);
                            diaAtual.setVento(infos1[2]);
                        }
                        case 1 -> {
                            diaAtual.setMilimetros(infos2[0]);
                            diaAtual.setProb(infos2[1]);
                            diaAtual.setVento(infos2[2]);
                        }
                        default -> {
                            diaAtual.setMilimetros("");
                            diaAtual.setProb("");
                            diaAtual.setVento("");
                        }
                    }

                    dias[j] = diaAtual;
                }
            }

        }
        driver.close();
        return cidades;
    }
}
