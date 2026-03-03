package com.pz.api_previsao.scraper.simepar;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Abre o site, injeta cookies do arquivo cookies.json e recarrega para ficar
 * logado.
 *
 * IMPORTANTE:
 * - O driver deve navegar para o domínio (BASE_URL) antes de addCookie.
 * - Cookies HttpOnly e SameSite podem ter comportamento específico.
 */
public class CookieLoader {

    private static final String BASE_URL = "https://www.instagram.com"; // mesma origem dos cookies
    private static final String COOKIES_FILE = "cookies.json";

    static class SerializableCookie {
        String name;
        String value;
        String domain;
        String path;
        Long expiry; // epoch millis (nullable)
        boolean isSecure;
        boolean isHttpOnly;
    }

    public void load(WebDriver driver) throws Exception {
        WebDriverManager.chromedriver().setup();

        try {
            // é preciso navegar para a origem antes de adicionar cookies
            driver.get(BASE_URL);
            Thread.sleep(1000);

            // lê cookies do arquivo
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SerializableCookie>>() {
            }.getType();
            List<SerializableCookie> list = gson.fromJson(new FileReader(COOKIES_FILE), listType);

            int added = 0;
            for (SerializableCookie sc : list) {
                Cookie.Builder b = new Cookie.Builder(sc.name, sc.value)
                        .path(sc.path == null ? "/" : sc.path);

                // domínio: somente adicione se não for null/empty
                if (sc.domain != null && !sc.domain.isEmpty()) {
                    // Selenium aceita domain; cuidado com domínio diferente (subdomínios)
                    b.domain(sc.domain);
                }

                if (sc.expiry != null) {
                    b.expiresOn(new Date(sc.expiry));
                }

                if (sc.isSecure)
                    b.isSecure(true);
                if (sc.isHttpOnly)
                    b.isHttpOnly(true);

                Cookie cookie = b.build();
                try {
                    driver.manage().addCookie(cookie);
                    added++;
                } catch (Exception e) {
                    System.err.println("Erro ao adicionar cookie " + sc.name + ": " + e.getMessage());
                }
            }

            System.out.printf("[+] Adicionados %d cookies%n", added);

            // recarrega a página para aplicar os cookies e permanecer logado
            driver.navigate().refresh();
            Thread.sleep(2000);

            // opcional: verificar se está logado (procure elemento específico)
            System.out.println(driver.getTitle());

        } finally {
            // não feche se quiser inspecionar; aqui fechamos

        }
    }
}
