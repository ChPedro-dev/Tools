package com.pz.api_previsao.util;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

@Component 
public class SleepAleatorio {

    public void sleep() throws InterruptedException {
        int tempo = ThreadLocalRandom.current().nextInt(3000, 7001);
        Thread.sleep(tempo);
    }

}
