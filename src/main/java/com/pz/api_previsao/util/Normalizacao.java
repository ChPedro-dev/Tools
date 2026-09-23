package com.pz.api_previsao.util;

import org.springframework.stereotype.Component;

@Component
public class Normalizacao {

    String condicao;
    long iconeIndex;

    public void setIconeIndex(String condicao) {
        switch (condicao) {
            case "Céu claro" ->
                this.iconeIndex = 0;
            case "Chuva" ->
                this.iconeIndex = 1;
            case "Geada" ->
                this.iconeIndex = 2;
            case "Poucas nuvens" ->
                this.iconeIndex = 3;
            case "Muitas nuvens" ->
                this.iconeIndex = 3;
            case "Parcialmente nublado com pancadas de chuva" ->
                this.iconeIndex = 4;
            case "Nublado" ->
                this.iconeIndex = 5;
        }
    }

    public long getIconeIndex() {
        return iconeIndex;
    }
    
    public String getCondicao() {
        return condicao;
    }
    
    public void setCondicao(String condicao) {

        switch (condicao) {
            case "Parcialmente nublado com pancadas de chuva":
                this.condicao = "Nublado com chuva";
                break;
            default:
                this.condicao = condicao;
        }
    }   

    public static String temp(String temp) {

        return temp.replace("˚C", "").trim();
    }

}
