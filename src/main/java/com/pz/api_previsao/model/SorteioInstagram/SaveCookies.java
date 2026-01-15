package com.pz.api_previsao.model.SorteioInstagram;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SaveCookies {

    // Seus seletores originais (INTOCADOS)
    private static final String LOGIN_URL = "https://www.instagram.com/accounts/login/";
    private static final String SELECTOR_USERNAME = "#loginForm > div.html-div.x14z9mp.xat24cr.x1lziwak.xexx8yu.xyri2b.x18d9i69.x1c1uobl.x9f619.xjbqb8w.x78zum5.x15mokao.x1ga7v0g.x16uus16.xbiv7yw.xqui205.x1n2onr6.x1plvlek.xryxfnj.x1c4vz4f.x2lah0s.xdt5ytf.xqjyukv.x1qjc9v5.x1oa3qoh.x1nhvcw1 > div:nth-child(1) > div > label > input";
    private static final String SELECTOR_PASSWORD = "#loginForm > div.html-div.x14z9mp.xat24cr.x1lziwak.xexx8yu.xyri2b.x18d9i69.x1c1uobl.x9f619.xjbqb8w.x78zum5.x15mokao.x1ga7v0g.x16uus16.xbiv7yw.xqui205.x1n2onr6.x1plvlek.xryxfnj.x1c4vz4f.x2lah0s.xdt5ytf.xqjyukv.x1qjc9v5.x1oa3qoh.x1nhvcw1 > div:nth-child(2) > div > label > input";
    private static final String SELECTOR_SUBMIT   = "#loginForm > div.html-div.x14z9mp.xat24cr.x1lziwak.xexx8yu.xyri2b.x18d9i69.x1c1uobl.x9f619.xjbqb8w.x78zum5.x15mokao.x1ga7v0g.x16uus16.xbiv7yw.xqui205.x1n2onr6.x1plvlek.xryxfnj.x1c4vz4f.x2lah0s.xdt5ytf.xqjyukv.x1qjc9v5.x1oa3qoh.x1nhvcw1 > div:nth-child(3)";
    
    // Suas credenciais
    private static final String USERNAME = "SorteioAPP@hotmail.com";
    private static final String PASSWORD = "APP&123()";

    public void logar(WebDriver driver) {
        
        
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            System.out.println("--- Iniciando Login no Instagram ---");
            driver.get(LOGIN_URL);
            
            // Isso resolve 90% dos erros de "ElementNotFound" em produção
            WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(SELECTOR_USERNAME)));
            userField.clear();
            userField.sendKeys(USERNAME);

            WebElement passField = driver.findElement(By.cssSelector(SELECTOR_PASSWORD));
            passField.clear();
            passField.sendKeys(PASSWORD);

            // Clica no botão
            try {
                WebElement btnSubmit = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(SELECTOR_SUBMIT)));
                btnSubmit.click();
            } catch (Exception e) {
                // Fallback: Se o botão falhar, tenta dar Enter na senha
                passField.submit();
            }

            // Espera o login acontecer (verifica se a URL mudou ou só espera um pouco)
            // Mantive um sleep pequeno só para garantir o redirecionamento
            Thread.sleep(5000); 

            System.out.println("--- Login enviado (sem salvar cookies) ---");

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO NO LOGIN: " + e.getMessage());
            e.printStackTrace();
            // Importante: Não relança o erro para não derrubar o resto do app,
            // a menos que o login seja obrigatório para o resto funcionar.
        }
        
        // REMOVIDO: Bloco de FileWriter que causava erro de permissão no Linux
    }
}