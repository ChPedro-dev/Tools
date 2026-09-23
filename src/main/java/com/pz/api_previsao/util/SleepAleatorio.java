package com.pz.api_previsao.util;

import java.util.concurrent.ThreadLocalRandom;

public class SleepAleatorio {

    public void sleep() throws InterruptedException {
        int tempo = ThreadLocalRandom.current().nextInt(3000, 7001);
        Thread.sleep(tempo);
    }

}
