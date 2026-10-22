/**
 * @file api.js
 * @description Camada de serviço isolada para comunicação com a API Spring Boot (SIMEPAR Scraper & Cidades).
 * Mantém zero acoplamento com o DOM ou componentes de UI.
 */

'use strict';

/**
 * Helper interno para tratar respostas HTTP de forma padronizada.
 * @param {Response} response
 * @returns {Promise<any>}
 */
async function handleResponse(response) {
  if (!response.ok) {
    const errorText = await response.text().catch(() => '');
    throw new Error(`HTTP ${response.status}${errorText ? `: ${errorText}` : ''}`);
  }
  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    return response.json();
  }
  return response.text();
}

export const WeatherApi = {
  /**
   * Busca todas as cidades disponíveis no estado do Paraná.
   * @returns {Promise<Array<{id: number, nome: string, busca: string}>>}
   */
  async getAllCities() {
    const res = await fetch('/cidades');
    return handleResponse(res);
  },

  /**
   * Busca a lista de cidades salva em memória no servidor.
   * @returns {Promise<Array<{id: number, nome: string, busca: string}>>}
   */
  async getSavedCitiesList() {
    const res = await fetch('/cidades/lista');
    if (!res.ok) return [];
    const text = await res.text();
    if (!text) return [];
    try {
      return JSON.parse(text);
    } catch {
      return [];
    }
  },

  /**
   * Salva a lista atual de cidades selecionadas em memória no servidor.
   * @param {Array<{id: number, nome: string, busca: string}>} cities
   * @returns {Promise<void>}
   */
  async saveCitiesList(cities) {
    const res = await fetch('/cidades/salvar/lista', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cities),
    });
    return handleResponse(res);
  },

  /**
   * Executa o scraping de previsão meteorológica no SIMEPAR para as cidades selecionadas.
   * @param {string} acao - Ação de coleta (ex: 'buscar_previsao', 'BDL', 'LU', '18h')
   * @param {Array<{id: number, nome: string, busca: string}>} cities
   * @returns {Promise<Array<{id: number, nome: string, dias: Array<any>}>>}
   */
  async fetchForecastByCities(acao, cities) {
    const url = `/previsao/scrap/cwb?acao=${encodeURIComponent(acao)}`;
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cities),
    });
    return handleResponse(res);
  },

  /**
   * Busca o último resultado de previsão armazenado em cache no servidor.
   * @returns {Promise<Array<{id: number, nome: string, dias: Array<any>}> | null>}
   */
  async getCachedForecast() {
    try {
      const res = await fetch('/previsao/cache');
      if (!res.ok) return null;
      const text = await res.text();
      if (!text || text === 'null') return null;
      const data = JSON.parse(text);
      return Array.isArray(data) && data.length > 0 ? data : null;
    } catch {
      return null;
    }
  },

  /**
   * Busca o horário da última atualização realizada no servidor.
   * @returns {Promise<string | null>}
   */
  async getLastUpdateTime() {
    try {
      const res = await fetch('/previsao/status');
      if (!res.ok) return null;
      const text = (await res.text()).trim();
      return text && text !== 'null' ? text : null;
    } catch {
      return null;
    }
  },
};
