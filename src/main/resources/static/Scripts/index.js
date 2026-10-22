/**
 * @file index.js
 * @description Controlador principal da aplicação de Previsão do Tempo (SIMEPAR).
 * Implementa gerenciamento de estado previsível, navegação acessível por teclado (combobox/tabs)
 * e microinterações ricas em feedback.
 */

'use strict';

import { WeatherApi } from './api.js';
import {
  escapeHtml,
  renderCityTag,
  renderCityWeatherCard,
  renderEmptyOrErrorState,
  renderSkeletonGrid,
} from './components.js';

const PRESETS = {
  principais: [4106902, 4113700, 4115200, 4119905, 4104808, 4108304], // Curitiba, Londrina, Maringá, Ponta Grossa, Cascavel, Foz
  litoral: [4118204, 4115705, 4109609, 4101200, 4116208, 4119954],    // Paranaguá, Matinhos, Guaratuba, Antonina, Morretes, Pontal
};

const state = {
  allCities: [],
  selected: [],
  results: [],
  filteredDropdown: [],
  activeDropdownIndex: -1,
  loading: false,
  lastAction: 'buscar_previsao',
  lastUpdated: null,
  elapsedTimerId: null,
};

/* Referências DOM */
const dom = {
  searchInput: document.getElementById('cidade-search'),
  searchContainer: document.getElementById('search-container'),
  dropdown: document.getElementById('dropdown'),
  tagsWrap: document.getElementById('tags-wrap'),
  selectedCounter: document.getElementById('selected-counter'),
  btnClearSelected: document.getElementById('btn-clear-selected'),
  btnPresetPrincipais: document.getElementById('btn-preset-principais'),
  btnPresetLitoral: document.getElementById('btn-preset-litoral'),
  acaoSelect: document.getElementById('acao-select'),
  customAcaoWrap: document.getElementById('custom-acao-wrap'),
  customAcaoInput: document.getElementById('acao-custom'),
  btnBuscar: document.getElementById('btn-buscar'),
  btnSalvar: document.getElementById('btn-salvar'),
  btnCarregar: document.getElementById('btn-carregar'),
  btnCache: document.getElementById('btn-cache'),
  btnCopyJson: document.getElementById('btn-copy-json'),
  resultsHeader: document.getElementById('results-header'),
  resultCount: document.getElementById('result-count'),
  resultMeta: document.getElementById('result-meta'),
  resultFilterInput: document.getElementById('result-filter'),
  output: document.getElementById('output'),
  statusHorario: document.getElementById('status-horario'),
  statusCitiesTotal: document.getElementById('status-cities-total'),
  loadingBanner: document.getElementById('loading-banner'),
  loadingElapsed: document.getElementById('loading-elapsed'),
  toastContainer: document.getElementById('toast-container'),
};

/**
 * Exibe notificação toast empilhável com suporte a a11y (role="status" / "alert").
 */
function notify(message, type = 'info', duration = 4200) {
  const styles = {
    success: 'bg-emerald-950/95 border-emerald-500/40 text-emerald-200',
    error: 'bg-rose-950/95 border-rose-500/40 text-rose-200',
    info: 'bg-slate-900/95 border-indigo-500/40 text-slate-100',
  };
  const icons = {
    success: '✅',
    error: '⚠️',
    info: 'ℹ️',
  };

  const toastEl = document.createElement('div');
  toastEl.setAttribute('role', type === 'error' ? 'alert' : 'status');
  toastEl.className = `animate-fade-slide pointer-events-auto flex items-center gap-3 rounded-xl border px-4 py-3 text-sm font-medium shadow-2xl backdrop-blur-md ${styles[type] || styles.info}`;
  toastEl.innerHTML = `
    <span aria-hidden="true">${icons[type] || icons.info}</span>
    <span class="flex-1">${escapeHtml(message)}</span>
    <button type="button" aria-label="Fechar notificação" class="rounded-lg p-1 opacity-70 hover:opacity-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-sky-400">
      ✕
    </button>
  `;

  const closeBtn = toastEl.querySelector('button');
  const removeToast = () => {
    toastEl.remove();
  };
  closeBtn.addEventListener('click', removeToast);

  dom.toastContainer.appendChild(toastEl);
  setTimeout(removeToast, duration);
}

function getSelectedAction() {
  return dom.acaoSelect.value === 'custom'
    ? dom.customAcaoInput.value.trim()
    : dom.acaoSelect.value;
}

function syncControlsState() {
  const hasSelection = state.selected.length > 0;
  dom.btnBuscar.disabled = !hasSelection || state.loading;
  dom.btnSalvar.disabled = !hasSelection || state.loading;
  dom.btnCarregar.disabled = state.loading;
  dom.btnCache.disabled = state.loading;

  if (dom.selectedCounter) {
    dom.selectedCounter.textContent = `${state.selected.length} selecionada${state.selected.length === 1 ? '' : 's'}`;
  }
  if (dom.btnClearSelected) {
    dom.btnClearSelected.classList.toggle('hidden', !hasSelection || state.loading);
  }
}

/**
 * Renderiza os chips das cidades selecionadas.
 */
function renderSelectedTags() {
  if (!state.selected.length) {
    dom.tagsWrap.innerHTML = `
      <span class="text-xs text-slate-500 italic py-1">
        Nenhuma cidade selecionada. Pesquise acima ou use um atalho rápido.
      </span>
    `;
    return;
  }

  dom.tagsWrap.innerHTML = state.selected.map(renderCityTag).join('');
}

/**
 * Renderiza o dropdown de autocomplete com suporte a navegação via teclado (aria-activedescendant).
 */
function renderDropdown(query) {
  const normalizedQuery = query.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  const selectedIds = new Set(state.selected.map((c) => c.id));

  state.filteredDropdown = state.allCities
    .filter((c) => {
      if (selectedIds.has(c.id)) return false;
      const nomeNorm = (c.nome || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
      const buscaNorm = (c.busca || '').toLowerCase();
      return nomeNorm.includes(normalizedQuery) || buscaNorm.includes(normalizedQuery) || String(c.id).includes(normalizedQuery);
    })
    .slice(0, 50);

  state.activeDropdownIndex = state.filteredDropdown.length > 0 ? 0 : -1;

  if (!state.filteredDropdown.length) {
    dom.dropdown.innerHTML = `
      <div class="px-4 py-3 text-xs text-slate-400">
        Nenhum município encontrado para "<span class="text-slate-200 font-medium">${escapeHtml(query)}</span>"
      </div>
    `;
  } else {
    dom.dropdown.innerHTML = state.filteredDropdown
      .map((city, idx) => {
        const isActive = idx === state.activeDropdownIndex;
        return `
          <div
            role="option"
            id="dd-opt-${city.id}"
            aria-selected="${isActive ? 'true' : 'false'}"
            data-city-index="${idx}"
            class="dd-option flex items-center justify-between px-3.5 py-2.5 text-sm cursor-pointer transition-colors duration-150 ${
              isActive ? 'bg-indigo-600/25 text-white' : 'text-slate-200 hover:bg-slate-800/80'
            }"
          >
            <span class="font-medium">${escapeHtml(city.nome)}</span>
            <span class="text-xs font-mono text-slate-400">#${escapeHtml(city.id)}</span>
          </div>
        `;
      })
      .join('');
  }

  dom.dropdown.classList.remove('hidden');
  dom.searchInput.setAttribute('aria-expanded', 'true');
  updateActiveDropdownOption();
}

function updateActiveDropdownOption() {
  const options = dom.dropdown.querySelectorAll('.dd-option');
  options.forEach((opt, idx) => {
    const isActive = idx === state.activeDropdownIndex;
    opt.setAttribute('aria-selected', isActive ? 'true' : 'false');
    opt.classList.toggle('bg-indigo-600/25', isActive);
    opt.classList.toggle('text-white', isActive);
    if (isActive) {
      dom.searchInput.setAttribute('aria-activedescendant', opt.id);
      opt.scrollIntoView({ block: 'nearest' });
    }
  });
}

function closeDropdown() {
  dom.dropdown.classList.add('hidden');
  dom.dropdown.innerHTML = '';
  dom.searchInput.setAttribute('aria-expanded', 'false');
  dom.searchInput.removeAttribute('aria-activedescendant');
  state.filteredDropdown = [];
  state.activeDropdownIndex = -1;
}

function selectCity(city) {
  if (!city || state.selected.some((c) => c.id === city.id)) return;
  state.selected.push(city);
  dom.searchInput.value = '';
  closeDropdown();
  renderSelectedTags();
  syncControlsState();
  dom.searchInput.focus();
}

function removeCityById(id) {
  const numId = Number(id);
  state.selected = state.selected.filter((c) => c.id !== numId);
  renderSelectedTags();
  syncControlsState();
}

function addCitiesByIds(idList) {
  if (!state.allCities.length) return;
  const selectedIds = new Set(state.selected.map((c) => c.id));
  let added = 0;
  for (const id of idList) {
    if (!selectedIds.has(id)) {
      const found = state.allCities.find((c) => c.id === id);
      if (found) {
        state.selected.push(found);
        selectedIds.add(id);
        added++;
      }
    }
  }
  renderSelectedTags();
  syncControlsState();
  if (added > 0) {
    notify(`${added} município${added > 1 ? 's' : ''} adicionado${added > 1 ? 's' : ''} à seleção.`, 'info', 2800);
  }
}

/**
 * Ativa uma aba específica dentro de um card de cidade.
 */
function activateTabInCard(cardEl, targetPanelId) {
  const tabs = cardEl.querySelectorAll('.day-tab-btn');
  const panels = cardEl.querySelectorAll('.day-tab-panel');

  tabs.forEach((tab) => {
    const isMatch = tab.dataset.tabTarget === targetPanelId;
    tab.setAttribute('aria-selected', isMatch ? 'true' : 'false');
    tab.setAttribute('tabindex', isMatch ? '0' : '-1');
    tab.className = `day-tab-btn shrink-0 rounded-lg px-3 py-1.5 text-xs font-semibold transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-sky-400 ${
      isMatch
        ? 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/40 shadow-sm'
        : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60 border border-transparent'
    }`;
  });

  panels.forEach((panel) => {
    const isMatch = panel.id === targetPanelId;
    panel.classList.toggle('hidden', !isMatch);
    panel.classList.toggle('block', isMatch);
    panel.classList.toggle('animate-fade-slide', isMatch);
  });
}

/**
 * Renderiza os cards de resultado e conecta eventos de abas acessíveis.
 */
function renderForecastResults(cities, metaLabel) {
  state.results = Array.isArray(cities) ? cities : [];

  if (!state.results.length) {
    dom.resultsHeader.classList.add('hidden');
    dom.output.innerHTML = renderEmptyOrErrorState({
      icon: '🔍',
      title: 'Nenhum resultado retornado',
      description: 'A consulta não retornou dados para os parâmetros informados.',
    });
    return;
  }

  dom.resultsHeader.classList.remove('hidden');
  dom.resultCount.textContent = `${state.results.length} cidade${state.results.length > 1 ? 's' : ''}`;
  dom.resultMeta.textContent = metaLabel || `Atualizado às ${new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}`;
  if (dom.resultFilterInput) dom.resultFilterInput.value = '';

  dom.output.innerHTML = `
    <div id="cards-grid" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      ${state.results.map(renderCityWeatherCard).join('')}
    </div>
  `;
}

function startLoadingTimer(cityCount) {
  let seconds = 0;
  dom.loadingBanner.classList.remove('hidden');
  dom.loadingElapsed.textContent = `Coletando dados no SIMEPAR para ${cityCount} cidade${cityCount > 1 ? 's' : ''} (0s)…`;
  clearInterval(state.elapsedTimerId);
  state.elapsedTimerId = setInterval(() => {
    seconds += 1;
    dom.loadingElapsed.textContent = `Coletando dados no SIMEPAR para ${cityCount} cidade${cityCount > 1 ? 's' : ''} (${seconds}s)…`;
  }, 1000);
}

function stopLoadingTimer() {
  clearInterval(state.elapsedTimerId);
  dom.loadingBanner.classList.add('hidden');
}

/**
 * Executa a busca de previsão no backend.
 */
async function handleFetchForecast() {
  const acao = getSelectedAction();
  if (!acao) {
    notify('Informe o identificador da ação personalizada antes de buscar.', 'error');
    dom.customAcaoInput.focus();
    return;
  }
  if (!state.selected.length) {
    notify('Selecione ao menos um município para consultar.', 'error');
    dom.searchInput.focus();
    return;
  }

  state.loading = true;
  state.lastAction = acao;
  syncControlsState();

  const originalBtnHtml = dom.btnBuscar.innerHTML;
  dom.btnBuscar.innerHTML = `
    <svg class="h-4 w-4 animate-spin text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" aria-hidden="true">
      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
    </svg>
    <span>Consultando SIMEPAR…</span>
  `;

  dom.resultsHeader.classList.add('hidden');
  dom.output.innerHTML = renderSkeletonGrid(state.selected.length);
  startLoadingTimer(state.selected.length);

  try {
    const data = await WeatherApi.fetchForecastByCities(acao, state.selected);
    const updatedTime = await WeatherApi.getLastUpdateTime();
    if (updatedTime) {
      state.lastUpdated = updatedTime;
      dom.statusHorario.textContent = updatedTime;
    }
    renderForecastResults(data, `Ação: ${acao} · ${updatedTime || new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}`);
    notify(`Previsão atualizada para ${data.length} município${data.length > 1 ? 's' : ''}.`, 'success');
  } catch (err) {
    notify(`Falha ao buscar previsão: ${err.message}`, 'error', 6500);
    dom.output.innerHTML = renderEmptyOrErrorState({
      icon: '⚠️',
      title: 'Erro ao consultar o SIMEPAR',
      description: err.message || 'Verifique se o serviço e o WebDriver estão operacionais.',
      isError: true,
    });
  } finally {
    stopLoadingTimer();
    state.loading = false;
    dom.btnBuscar.innerHTML = originalBtnHtml;
    syncControlsState();
  }
}

/**
 * Salva lista de cidades selecionadas no backend.
 */
async function handleSaveList() {
  if (!state.selected.length) {
    notify('Selecione ao menos uma cidade antes de salvar a lista.', 'error');
    return;
  }

  state.loading = true;
  syncControlsState();
  const originalHtml = dom.btnSalvar.innerHTML;
  dom.btnSalvar.textContent = 'Salvando…';

  try {
    await WeatherApi.saveCitiesList(state.selected);
    const count = state.selected.length;
    notify(`Lista salva em memória (${count} município${count > 1 ? 's' : ''}).`, 'success');
  } catch (err) {
    notify(`Erro ao salvar lista: ${err.message}`, 'error');
  } finally {
    state.loading = false;
    dom.btnSalvar.innerHTML = originalHtml;
    syncControlsState();
  }
}

/**
 * Carrega a lista de cidades salva no backend.
 */
async function handleLoadSavedList() {
  state.loading = true;
  syncControlsState();
  const originalHtml = dom.btnCarregar.innerHTML;
  dom.btnCarregar.textContent = 'Carregando…';

  try {
    const saved = await WeatherApi.getSavedCitiesList();
    if (!saved || !saved.length) {
      notify('Nenhuma lista salva encontrada no servidor.', 'info');
      return;
    }

    const currentIds = new Set(state.selected.map((c) => c.id));
    const newCities = saved.filter((c) => !currentIds.has(c.id));
    state.selected = [...state.selected, ...newCities];
    renderSelectedTags();
    syncControlsState();

    notify(`${saved.length} cidade${saved.length > 1 ? 's' : ''} carregada${saved.length > 1 ? 's' : ''} da memória.`, 'success');
  } catch (err) {
    notify(`Erro ao carregar lista: ${err.message}`, 'error');
  } finally {
    state.loading = false;
    dom.btnCarregar.innerHTML = originalHtml;
    syncControlsState();
  }
}

/**
 * Carrega o último cache de previsão do servidor sem disparar novo scraping.
 */
async function handleLoadCache(silentIfEmpty = false) {
  try {
    const [cached, lastTime] = await Promise.all([
      WeatherApi.getCachedForecast(),
      WeatherApi.getLastUpdateTime(),
    ]);

    if (lastTime) {
      state.lastUpdated = lastTime;
      dom.statusHorario.textContent = lastTime;
    }

    if (cached && cached.length) {
      renderForecastResults(cached, `Cache em memória · Atualizado às ${lastTime || '—'}`);
      if (!silentIfEmpty) {
        notify(`Exibindo ${cached.length} cidade${cached.length > 1 ? 's' : ''} do cache em memória.`, 'info');
      }
      return true;
    } else if (!silentIfEmpty) {
      notify('Nenhuma previsão armazenada em cache no momento.', 'info');
    }
  } catch (err) {
    if (!silentIfEmpty) {
      notify(`Erro ao consultar cache: ${err.message}`, 'error');
    }
  }
  return false;
}

/**
 * Inicialização da aplicação: carrega municípios, status e verifica cache existente.
 */
async function initApp() {
  renderSelectedTags();
  syncControlsState();

  try {
    const cities = await WeatherApi.getAllCities();
    state.allCities = Array.isArray(cities) ? cities : [];
    if (dom.statusCitiesTotal) {
      dom.statusCitiesTotal.textContent = `${state.allCities.length} municípios PR`;
    }

    const restoredFromCache = await handleLoadCache(true);
    if (!restoredFromCache) {
      dom.output.innerHTML = renderEmptyOrErrorState({
        icon: '⛅',
        title: 'Pronto para consultar o clima no Paraná',
        description:
          'Pesquise municípios acima ou utilize os atalhos de Principais Cidades / Litoral para coletar dados meteorológicos em tempo real via SIMEPAR.',
      });
    }
  } catch (err) {
    notify(`Erro ao conectar com a API: ${err.message}`, 'error', 7000);
    dom.output.innerHTML = renderEmptyOrErrorState({
      icon: '🔌',
      title: 'Não foi possível carregar a base de municípios',
      description: 'Verifique se o servidor Spring Boot está em execução e recarregue a página.',
      isError: true,
    });
  }
}

/* ==========================================================================
   Event Listeners & Acessibilidade por Teclado
   ========================================================================== */

// Autocomplete Input & Teclado
dom.searchInput.addEventListener('input', () => {
  const q = dom.searchInput.value.trim();
  if (q) {
    renderDropdown(q);
  } else {
    closeDropdown();
  }
});

dom.searchInput.addEventListener('focus', () => {
  const q = dom.searchInput.value.trim();
  if (q) renderDropdown(q);
});

dom.searchInput.addEventListener('keydown', (e) => {
  const isOpen = !dom.dropdown.classList.contains('hidden');

  if (e.key === 'ArrowDown' && isOpen && state.filteredDropdown.length > 0) {
    e.preventDefault();
    state.activeDropdownIndex = (state.activeDropdownIndex + 1) % state.filteredDropdown.length;
    updateActiveDropdownOption();
  } else if (e.key === 'ArrowUp' && isOpen && state.filteredDropdown.length > 0) {
    e.preventDefault();
    state.activeDropdownIndex =
      (state.activeDropdownIndex - 1 + state.filteredDropdown.length) % state.filteredDropdown.length;
    updateActiveDropdownOption();
  } else if (e.key === 'Enter' && isOpen && state.activeDropdownIndex >= 0) {
    e.preventDefault();
    selectCity(state.filteredDropdown[state.activeDropdownIndex]);
  } else if (e.key === 'Escape') {
    closeDropdown();
    dom.searchInput.blur();
  } else if (e.key === 'Backspace' && dom.searchInput.value === '' && state.selected.length > 0) {
    // Permite remover a última tag rapidamente com Backspace quando o campo está vazio
    const lastCity = state.selected[state.selected.length - 1];
    removeCityById(lastCity.id);
  }
});

// Clique em item do Dropdown
dom.dropdown.addEventListener('mousedown', (e) => {
  const optionEl = e.target.closest('.dd-option');
  if (!optionEl) return;
  e.preventDefault();
  const idx = Number(optionEl.dataset.cityIndex);
  if (!Number.isNaN(idx) && state.filteredDropdown[idx]) {
    selectCity(state.filteredDropdown[idx]);
  }
});

// Fechar dropdown ao clicar fora
document.addEventListener('click', (e) => {
  if (!dom.searchContainer.contains(e.target)) {
    closeDropdown();
  }
});

// Remover cidade clicando no chip (Event Delegation)
dom.tagsWrap.addEventListener('click', (e) => {
  const removeBtn = e.target.closest('[data-remove-city]');
  if (!removeBtn || state.loading) return;
  removeCityById(removeBtn.dataset.removeCity);
});

// Limpar todas as cidades selecionadas
dom.btnClearSelected?.addEventListener('click', () => {
  if (state.loading) return;
  state.selected = [];
  renderSelectedTags();
  syncControlsState();
  dom.searchInput.focus();
});

// Presets rápidos
dom.btnPresetPrincipais?.addEventListener('click', () => addCitiesByIds(PRESETS.principais));
dom.btnPresetLitoral?.addEventListener('click', () => addCitiesByIds(PRESETS.litoral));

// Alternar campo de ação personalizada
dom.acaoSelect.addEventListener('change', () => {
  const isCustom = dom.acaoSelect.value === 'custom';
  dom.customAcaoWrap.classList.toggle('hidden', !isCustom);
  if (isCustom) dom.customAcaoInput.focus();
});

// Botões principais de ação
dom.btnBuscar.addEventListener('click', handleFetchForecast);
dom.btnSalvar.addEventListener('click', handleSaveList);
dom.btnCarregar.addEventListener('click', handleLoadSavedList);
dom.btnCache.addEventListener('click', () => handleLoadCache(false));

// Copiar JSON dos resultados
dom.btnCopyJson?.addEventListener('click', async () => {
  if (!state.results.length) return;
  try {
    await navigator.clipboard.writeText(JSON.stringify(state.results, null, 2));
    notify('JSON dos resultados copiado para a área de transferência!', 'success', 3000);
  } catch {
    notify('Não foi possível copiar automaticamente.', 'error');
  }
});

// Filtro instantâneo de cidades nos resultados renderizados
dom.resultFilterInput?.addEventListener('input', () => {
  const q = dom.resultFilterInput.value.trim().toLowerCase();
  const cards = dom.output.querySelectorAll('.city-card');
  cards.forEach((card) => {
    const name = card.dataset.cityName || '';
    card.classList.toggle('hidden', Boolean(q && !name.includes(q)));
  });
});

// Event Delegation para abas de dias (clique e navegação por setas Left/Right)
dom.output.addEventListener('click', (e) => {
  const tabBtn = e.target.closest('.day-tab-btn');
  if (tabBtn) {
    const card = tabBtn.closest('.city-card');
    if (card) activateTabInCard(card, tabBtn.dataset.tabTarget);
    return;
  }

  const miniBtn = e.target.closest('[data-mini-day]');
  if (miniBtn) {
    const card = miniBtn.closest('.city-card');
    if (card) activateTabInCard(card, miniBtn.dataset.miniDay);
  }
});

dom.output.addEventListener('keydown', (e) => {
  const tabBtn = e.target.closest('.day-tab-btn');
  if (!tabBtn) return;
  if (e.key !== 'ArrowRight' && e.key !== 'ArrowLeft') return;

  e.preventDefault();
  const card = tabBtn.closest('.city-card');
  const tabs = Array.from(card.querySelectorAll('.day-tab-btn'));
  const currentIndex = tabs.indexOf(tabBtn);
  if (currentIndex === -1) return;

  const nextIndex =
    e.key === 'ArrowRight'
      ? (currentIndex + 1) % tabs.length
      : (currentIndex - 1 + tabs.length) % tabs.length;

  const nextTab = tabs[nextIndex];
  activateTabInCard(card, nextTab.dataset.tabTarget);
  nextTab.focus();
});

initApp();
