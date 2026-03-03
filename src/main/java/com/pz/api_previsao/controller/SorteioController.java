package com.pz.api_previsao.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pz.api_previsao.service.SorteioService;
import com.pz.api_previsao.model.Comentario;

@RestController
public class SorteioController {

    @org.springframework.beans.factory.annotation.Autowired
    private SorteioService sorteioService;

    private static final Map<String, List<Comentario>> cacheComentarios = new ConcurrentHashMap<>();

    @PostMapping("/sorteio")
    public Comentario SorteioInstagram(@RequestBody Map<String, String> body) throws Exception {

        String url = body.get("url");

        List<Comentario> listaDeComentarios;

        if (cacheComentarios.containsKey(url)) {

            listaDeComentarios = cacheComentarios.get(url);
            for (Comentario comentario : listaDeComentarios) {
                System.out.println(comentario.toString());
            }

        } else {

            listaDeComentarios = sorteioService.scrapePost(url);
            cacheComentarios.put(url, listaDeComentarios);

        }

        int numero = (int) (Math.random() * listaDeComentarios.size());

        Comentario sorteado = listaDeComentarios.get(numero);

        return sorteado;

    }
}
