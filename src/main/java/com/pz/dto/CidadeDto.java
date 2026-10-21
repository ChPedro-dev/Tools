package com.pz.dto;

public class CidadeDto {
    int id;
    String nome;
    String busca;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getBusca() {
        return busca;
    }
    public void setBusca(String busca) {
        this.busca = busca;
    }
    public CidadeDto(int id, String nome, String busca) {
        this.id = id;
        this.nome = nome;
        this.busca = busca;
    }
    public CidadeDto() {
    }

    
    
}
