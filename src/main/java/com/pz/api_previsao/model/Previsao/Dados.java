package com.pz.api_previsao.model.Previsao;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Dados {

    public String[] dia(WebDriver driver) {
        String[] dia = new String[5];

        for (int i = 0; i < 5; i++) {
            List<WebElement> elementos = driver.findElements(By.cssSelector("#linkDia_" + i + " > span"));

            dia[i] = elementos.get(0).getText(); // Pega o texto do primeiro span
        }

        return dia;
    }

    public int[] maximas(WebDriver driver) {
        List<WebElement> max = driver.findElements(By.cssSelector(
                "g.highcharts-data-labels.highcharts-series-0.highcharts-line-series >g > text> tspan.highcharts-text-outline"));

        int[] maximas = new int[5];
        for (int i = 0; i < 5; i++) {
            maximas[i] = Integer.parseInt(normalizacao.temp(max.get(i).getText()));
        }

        return maximas;
    }

    public int[] minimas(WebDriver driver) {
        List<WebElement> min = driver.findElements(By.cssSelector(
                "g.highcharts-data-labels.highcharts-series-1.highcharts-line-series > g > text > tspan.highcharts-text-outline"));

        int[] minimas = new int[5];
        for (int i = 0; i < 5; i++) {
            minimas[i] = Integer
                    .parseInt(min.get(i).getText().equals("") ? "0" : normalizacao.temp(min.get(i).getText()));
        }

        return minimas;
    }

    public String[] infos(WebDriver driver) throws InterruptedException {
        Thread.sleep(1000);
        WebElement precipitacao = driver.findElement(
                By.cssSelector("div > div.row.margin-bottom-20 > div:nth-child(1) > div:nth-child(2) > span.val"));
        WebElement probabilidade = driver.findElement(
                By.cssSelector("div > div.row.margin-bottom-20 > div:nth-child(1) > div:nth-child(3) > span.val"));
        WebElement vento = driver.findElement(
                By.cssSelector("div > div.row.margin-bottom-20 > div:nth-child(2) > div:nth-child(2) > span.val"));

        String[] inf = { precipitacao.getText().replace(" ", ""), probabilidade.getText().replace(" ", ""),
                vento.getText().replace(" ", "") };

        return inf;

    }

    public String[] infos2(WebDriver driver) {
        try {

            WebElement botao = driver.findElement(By.cssSelector("#linkDia_1 > i"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            Thread.sleep(5000);
            js.executeScript("arguments[0].click();", botao);

            WebElement precipitacao = driver.findElement(By.cssSelector(
                    "div.tab-pane.daily_infos-wrapper.active > div.container > div.row.margin-bottom-20 > div:nth-child(1) > div:nth-child(2) > span.val"));
            WebElement probabilidade = driver.findElement(By.cssSelector(
                    "div.tab-pane.daily_infos-wrapper.active >div.container >div.row.margin-bottom-20 > div:nth-child(1) > div:nth-child(3) > span.val"));
            WebElement vento = driver.findElement(By.cssSelector(
                    "div.tab-pane.daily_infos-wrapper.active >div.container >div.row.margin-bottom-20 > div:nth-child(2) > div:nth-child(2) > span.val"));

            String[] inf = { precipitacao.getText().replace(" ", ""), probabilidade.getText().replace(" ", ""),
                    vento.getText().replace(" ", "") };
            return inf;
        } catch (Exception e) {
            return null;
        }

    }

    public String[] icones(WebDriver driver) throws InterruptedException {
        String[] id = new String[5];
        Thread.sleep(1000);
        for (int i = 0; i < 5; i++) {
            List<WebElement> icones = driver.findElements(By.cssSelector("#linkDia_" + i + " > i"));

            id[i] = icones.get(0).getDomAttribute("data-original-title");

        }

        return id;
    }

}
