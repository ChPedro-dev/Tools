package com.pz.api_previsao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pz.api_previsao.model.Comentario;
import com.pz.api_previsao.scraper.instagram.ComentarioScraper;

@Service
public class SorteioService {

    private final ComentarioScraper scraper = new ComentarioScraper();

    public List<Comentario> scrapePost(String url) throws Exception {
        return scraper.scrap(url);
    }
}
