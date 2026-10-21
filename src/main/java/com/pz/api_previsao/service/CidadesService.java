package com.pz.api_previsao.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.pz.api_previsao.util.LerJson;
import com.pz.dto.CidadeDto;

@Service
public class CidadesService {

    private final List<CidadeDto> cidades = new CopyOnWriteArrayList<>();

    public List<CidadeDto> cidades() throws IOException {
        return LerJson.ler();
    }

    public void SalvarLista(List<CidadeDto> novasCidades) {
        this.cidades.clear();
        if (novasCidades != null) {
            this.cidades.addAll(novasCidades);
        }
    }

    public List<CidadeDto> ListaCidades() {
        return new ArrayList<>(this.cidades);
    }

}
