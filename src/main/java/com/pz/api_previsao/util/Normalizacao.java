package com.pz.api_previsao.util;

import org.springframework.stereotype.Service;

@Service
public class Normalizacao {

    public static int obterIconeIndex(String condicao) {
        if (condicao == null) {
            return 0;
        }
        return switch (condicao.trim()) {
            case "Céu claro" -> 0;
            case "Chuva" -> 1;
            case "Geada" -> 2;
            case "Poucas nuvens", "Muitas nuvens" -> 3;
            case "Parcialmente nublado com pancadas de chuva" -> 4;
            case "Nublado" -> 5;
            default -> 0;
        };
    }

    public static String normalizarCondicao(String condicao) {
        if (condicao == null) {
            return "";
        }
        return switch (condicao.trim()) {
            case "Parcialmente nublado com pancadas de chuva" -> "Nublado com chuva";
            default -> condicao.trim();
        };
    }

    public static String temp(String temp) {
        if (temp == null) {
            return "0";
        }
        return temp.replace("˚C", "").trim();
    }

}
