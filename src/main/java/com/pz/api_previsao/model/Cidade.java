package com.pz.api_previsao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data 
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor 
@AllArgsConstructor 
public class Cidade {

	String nome;
	int id;
	Dia[] dias = new Dia[5];


}
