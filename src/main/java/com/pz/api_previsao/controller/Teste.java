package com.pz.api_previsao.controller;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pz.api_previsao.model.Driver;
import com.pz.api_previsao.model.SorteioInstagram.Comentario;
import com.pz.api_previsao.model.SorteioInstagram.SaveCookies;

@RestController
public class Teste {
    
    SaveCookies saveCookie = new SaveCookies();

    @GetMapping("/TESTE")
    public List<Comentario> instagram(String postUrl) throws Exception{

        WebDriver driver = Driver.getDriver();

        Thread.sleep(2500);
        saveCookie.logar(driver);
        
        Thread.sleep(2500);
        driver.get(postUrl);

        WebElement SessaoComentarios = driver.findElement(By.cssSelector("div.x5yr21d.xw2csxc.x1odjw0f.x1n2onr6"));

        long lastHeight = (long) ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return arguments[0].scrollHeight", SessaoComentarios);

        while (true) {

            // Rola para o fundo
            ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", SessaoComentarios);
            try {
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Calcula a nova altura
            long newHeight = (long) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].scrollHeight", SessaoComentarios);

            // Verifica se parou de crescer
            if (newHeight == lastHeight) {
                
                try { Thread.sleep(2000); } catch (Exception e) {}
                newHeight = (long) ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("return arguments[0].scrollHeight", SessaoComentarios);
                    
                // Se ainda for igual, realmente acabou
                if (newHeight == lastHeight) {
                    break; 
                }
            }

            lastHeight = newHeight;
        }
        int i = 1;

        List<Comentario> comentarios = new ArrayList<>();
        try 
        {
            while(true)
            {
            List<WebElement> UsuarioElement = driver.findElements(By.cssSelector("span.xt0psk2 > span.xjp7ctv > div > a > div > div > span"));

            WebElement Comentario = driver.findElement(By.xpath("//section/main/div/div[1]/div/div[2]/div/div[2]/div/div[2]/div["+i+"]/div/div/div[2]/div[1]/div[1]/div/div[2]/span"));
            
            if(Comentario.equals(" "))
            {
                break;
            }
            
            comentarios.add(new Comentario(i, Comentario.getText(), UsuarioElement.get(i+1).getText()));
        
            i++;
            }    

        } catch (Exception e) {
            System.out.println(e);
        }
        

        


        

        return comentarios;



    }
    
      
}
