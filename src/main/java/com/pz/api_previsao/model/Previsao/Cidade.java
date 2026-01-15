package com.pz.api_previsao.model.Previsao;

import java.util.HashMap;
import java.util.Map;

public class Cidade {

	String nome;
	int id;
	String prob;
	String milimetros;
	String vento;

	public Cidade() {
	}

	public Cidade(String nome, int id) {
		super();
		this.nome = nome;
		this.id = id;

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

	public static final Map<String, String> normalizacoes = new HashMap<>();
	static {
		normalizacoes.put("Parcialmente nublado com pancadas de chuva", "Nublado com chuva");
	}

}
