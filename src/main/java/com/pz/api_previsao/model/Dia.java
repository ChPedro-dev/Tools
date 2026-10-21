package com.pz.api_previsao.model;

public class Dia {

    String dia;
    int maxima;
    int minima;
    String condicao;
    int iconeIndex;
    String prob;
	String milimetros;
	String vento;
    public String getDia() {
        return dia;
    }
    public void setDia(String dia) {
        this.dia = dia;
    }
    public int getMaxima() {
        return maxima;
    }
    public void setMaxima(int maxima) {
        this.maxima = maxima;
    }
    public int getMinima() {
        return minima;
    }
    public void setMinima(int minima) {
        this.minima = minima;
    }
    public String getCondicao() {
        return condicao;
    }
    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }
    public int getIconeIndex() {
        return iconeIndex;
    }
    public void setIconeIndex(int iconeIndex) {
        this.iconeIndex = iconeIndex;
    }
    public String getProb() {
        return prob;
    }
    public void setProb(String prob) {
        this.prob = prob;
    }
    public String getMilimetros() {
        return milimetros;
    }
    public void setMilimetros(String milimetros) {
        this.milimetros = milimetros;
    }
    public String getVento() {
        return vento;
    }
    public void setVento(String vento) {
        this.vento = vento;
    }
    public Dia(String dia, int maxima, int minima, String condicao, int iconeIndex, String prob, String milimetros,
            String vento) {
        this.dia = dia;
        this.maxima = maxima;
        this.minima = minima;
        this.condicao = condicao;
        this.iconeIndex = iconeIndex;
        this.prob = prob;
        this.milimetros = milimetros;
        this.vento = vento;
    }
    public Dia() {
    }

    
}
