package com.pz.api_previsao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class Dia {

    String dia;
    int maxima;
    int minima;
    String condicao;
    int iconeIndex;
    String prob;
	String milimetros;
	String vento;
}
