package com.pz.api_previsao.model.TelasBDL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Dados {

    public void getTempoReal(WebDriver driver) {

        WebElement a = driver.findElement(By.cssSelector("span#navios-ao-largo"));
        System.out.println(a);

    }

}
