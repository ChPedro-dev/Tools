package com.pz.api_previsao.model;


import lombok.EqualsAndHashCode;

/**
 * Cidade
 */
@EqualsAndHashCode(callSuper=false)
public class Cidade {

	String nome;
	int id;
	Dia[] dias = new Dia[5];

	public Cidade(String nome, int id, Dia[] dias) {
		this.nome = nome;
		this.id = id;
		this.dias = dias;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Dia[] getDias() {
		return dias;
	}

	public void setDias(Dia[] dias) {
		this.dias = dias;
	}

	public Cidade() {
	}

	


}
