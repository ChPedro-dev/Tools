package com.pz.api_previsao.scraper.instagram;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import com.pz.api_previsao.model.Comentario;
import com.pz.api_previsao.scraper.simepar.DriverFactory;

/**
 * Encapsula a lógica de scraping de comentários de um post do Instagram.
 */
public class ComentarioScraper {
    
    @Autowired
    SaveCookies saveCookie;

    public List<Comentario> scrap(String postUrl) throws Exception {
        WebDriver driver = DriverFactory.getDriver();

        // faz login utilizando cookies/dados salvos
        Thread.sleep(2500);
        saveCookie.logar(driver);

        Thread.sleep(2500);
        driver.get(postUrl);

        WebElement sessaoComentarios = driver.findElement(By.cssSelector("div.x5yr21d.xw2csxc.x1odjw0f.x1n2onr6"));

        long lastHeight = (long) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].scrollHeight", sessaoComentarios);

        while (true) {
            // rola para o fim da sessão
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", sessaoComentarios);
            Thread.sleep(2000);

            long newHeight = (long) ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("return arguments[0].scrollHeight", sessaoComentarios);

            if (newHeight == lastHeight) {
                Thread.sleep(2000);
                newHeight = (long) ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return arguments[0].scrollHeight", sessaoComentarios);
                if (newHeight == lastHeight) {
                    break;
                }
            }
            lastHeight = newHeight;
        }

        List<Comentario> comentarios = new ArrayList<>();
        int i = 1;
        try {
            while (true) {
                List<WebElement> usuarioElement = driver.findElements(By.cssSelector(
                        "span.xt0psk2 > span.xjp7ctv > div > a > div > div > span"));

                WebElement comentarioEl = driver.findElement(By.xpath(
                        "//section/main/div/div[1]/div/div[2]/div/div[2]/div/div[2]/div["+i+"]/div/div/div[2]/div[1]/div[1]/div/div[2]/span"));

                if (comentarioEl.getText().trim().isEmpty()) {
                    break;
                }

                comentarios.add(new Comentario(i, comentarioEl.getText(), usuarioElement.get(i+1).getText()));
                i++;
            }
        } catch (Exception e) {
            System.out.println("scraper interrompido: " + e.getMessage());
        }

        // driver.quit(); // deixa caller decidir se fecha
        return comentarios;
    }
}
