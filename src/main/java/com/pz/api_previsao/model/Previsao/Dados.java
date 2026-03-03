package com.pz.api_previsao.model.Previsao;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Dados {

    public boolean checkcaptcha(WebDriver driver) throws InterruptedException {

        driver.get("https://www.simepar.br");
        Thread.sleep(20000);
        try {
            WebElement titulo = driver.findElement(
                    By.cssSelector("body > div > div.container.cc > div:nth-child(1) > div > h2 > a")
            );
            String texto = titulo.getText();

            if (texto == null || texto.trim().isEmpty()) {
                throw new RuntimeException("Título da página não encontrado. Verifique o captcha.");
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Captcha detectado ou elemento não encontrado.");
        }
    }

    private void sleepAleatorio() throws InterruptedException {
        int tempo = ThreadLocalRandom.current().nextInt(3000, 10001);
        Thread.sleep(tempo);
    }

    public String[] dia(WebDriver driver) throws InterruptedException {
        String[] dia = new String[5];

        sleepAleatorio();
        for (int i = 0; i < 5; i++) {
            List<WebElement> elementos = driver.findElements(
                    By.cssSelector("#linkDia_" + i + " > span"));

            if (!elementos.isEmpty()) {
                dia[i] = elementos.get(0).getText();
            } else {
                dia[i] = "";
            }
        }

        return dia;
    }

    public int[] maximas(WebDriver driver) throws InterruptedException {

        sleepAleatorio();
        List<WebElement> max = driver.findElements(By.cssSelector(
                "g.highcharts-data-labels.highcharts-series-0.highcharts-line-series >g > text> tspan.highcharts-text-outline"));

        int[] maximas = new int[5];

        for (int i = 0; i < 5 && i < max.size(); i++) {
            try {
                maximas[i] = Integer.parseInt(
                        normalizacao.temp(max.get(i).getText()));
            } catch (Exception e) {
                maximas[i] = 0;
            }
        }

        return maximas;
    }

    public int[] minimas(WebDriver driver) throws InterruptedException {

        sleepAleatorio();
        List<WebElement> min = driver.findElements(By.cssSelector(
                "g.highcharts-data-labels.highcharts-series-1.highcharts-line-series > g > text > tspan.highcharts-text-outline"));

        int[] minimas = new int[5];

        for (int i = 0; i < 5 && i < min.size(); i++) {
            try {
                String texto = min.get(i).getText();
                minimas[i] = Integer.parseInt(
                        texto.equals("") ? "0" : normalizacao.temp(texto));
            } catch (Exception e) {
                minimas[i] = 0;
            }
        }

        return minimas;
    }

    public String[] infos(WebDriver driver) throws InterruptedException {
        sleepAleatorio();

        String[] inf = new String[3];

        try {
            WebElement precipitacao = driver.findElement(
                    By.cssSelector("div > div.row.margin-bottom-20 > div:nth-child(1) > div:nth-child(2) > span.val"));
            WebElement probabilidade = driver.findElement(
                    By.cssSelector("div > div.row.margin-bottom-20 > div:nth-child(1) > div:nth-child(3) > span.val"));
            WebElement vento = driver.findElement(
                    By.cssSelector("div > div.row.margin-bottom-20 > div:nth-child(2) > div:nth-child(2) > span.val"));

            inf[0] = precipitacao.getText().replace(" ", "");
            inf[1] = probabilidade.getText().replace(" ", "");
            inf[2] = vento.getText().replace(" ", "");

        } catch (Exception e) {
            inf[0] = "";
            inf[1] = "";
            inf[2] = "";
        }

        return inf;
    }

    public String[] infos2(WebDriver driver) throws InterruptedException {

        sleepAleatorio();

        String[] inf = new String[3];

        try {
            WebElement botao = driver.findElement(
                    By.cssSelector("#linkDia_1 > i"));

            JavascriptExecutor js = (JavascriptExecutor) driver;

            sleepAleatorio();
            js.executeScript("arguments[0].click();", botao);

            WebElement precipitacao = driver.findElement(By.cssSelector(
                    "div.tab-pane.daily_infos-wrapper.active > div.container > div.row.margin-bottom-20 > div:nth-child(1) > div:nth-child(2) > span.val"));
            WebElement probabilidade = driver.findElement(By.cssSelector(
                    "div.tab-pane.daily_infos-wrapper.active >div.container >div.row.margin-bottom-20 > div:nth-child(1) > div:nth-child(3) > span.val"));
            WebElement vento = driver.findElement(By.cssSelector(
                    "div.tab-pane.daily_infos-wrapper.active >div.container >div.row.margin-bottom-20 > div:nth-child(2) > div:nth-child(2) > span.val"));

            inf[0] = precipitacao.getText().replace(" ", "");
            inf[1] = probabilidade.getText().replace(" ", "");
            inf[2] = vento.getText().replace(" ", "");

        } catch (Exception e) {
            inf[0] = "";
            inf[1] = "";
            inf[2] = "";
        }

        return inf;
    }

    public String[] icones(WebDriver driver) throws InterruptedException {
        String[] id = new String[5];

        sleepAleatorio();

        for (int i = 0; i < 5; i++) {
            List<WebElement> icones = driver.findElements(
                    By.cssSelector("#linkDia_" + i + " > i"));

            if (!icones.isEmpty()) {
                id[i] = icones.get(0)
                        .getDomAttribute("data-original-title");
            } else {
                id[i] = "";
            }
        }

        return id;
    }
}
