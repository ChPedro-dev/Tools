/**
 * @file components.js
 * @description Componentes puros de UI e utilitários de formatação visual com Tailwind CSS.
 * Segue princípios de Acessibilidade (a11y), Semântica HTML5 e Redução de Ruído.
 */

'use strict';

export const escapeHtml = (str) =>
  String(str ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');

const WEATHER_VISUALS = [
  { match: 'tempestade', emoji: '⛈️', label: 'Tempestade', badgeClass: 'bg-purple-500/15 text-purple-300 border-purple-500/30' },
  { match: 'chuva_fraca', emoji: '🌦️', label: 'Chuva Fraca', badgeClass: 'bg-sky-500/15 text-sky-300 border-sky-500/30' },
  { match: 'chuva', emoji: '🌧️', label: 'Chuva', badgeClass: 'bg-blue-500/15 text-blue-300 border-blue-500/30' },
  { match: 'neve', emoji: '❄️', label: 'Frio Intenso / Neve', badgeClass: 'bg-cyan-500/15 text-cyan-200 border-cyan-500/30' },
  { match: 'neblina', emoji: '🌫️', label: 'Neblina', badgeClass: 'bg-slate-500/20 text-slate-300 border-slate-500/30' },
  { match: 'muito_nublado', emoji: '☁️', label: 'Muito Nublado', badgeClass: 'bg-slate-500/20 text-slate-300 border-slate-500/30' },
  { match: 'nublado', emoji: '☁️', label: 'Nublado', badgeClass: 'bg-slate-500/20 text-slate-300 border-slate-500/30' },
  { match: 'parcialmente', emoji: '⛅', label: 'Parc. Nublado', badgeClass: 'bg-amber-500/15 text-amber-200 border-amber-500/30' },
  { match: 'poucas_nuven', emoji: '🌤️', label: 'Poucas Nuvens', badgeClass: 'bg-amber-500/15 text-amber-300 border-amber-500/30' },
  { match: 'ensolarado', emoji: '☀️', label: 'Ensolarado', badgeClass: 'bg-amber-500/20 text-amber-300 border-amber-500/35' },
  { match: 'sol', emoji: '☀️', label: 'Sol', badgeClass: 'bg-amber-500/20 text-amber-300 border-amber-500/35' },
];

export function resolveWeatherVisual(condicao, iconeIndex) {
  const normalized = (condicao || '').toLowerCase().replace(/\s+/g, '_');
  for (const item of WEATHER_VISUALS) {
    if (normalized.includes(item.match)) {
      return item;
    }
  }
  if (iconeIndex <= 1) return WEATHER_VISUALS[9];
  if (iconeIndex <= 3) return WEATHER_VISUALS[7];
  if (iconeIndex <= 5) return WEATHER_VISUALS[2];
  return WEATHER_VISUALS[0];
}

export function parseProbability(prob) {
  const num = parseInt(String(prob || '').replace(/\D/g, ''), 10);
  return Number.isNaN(num) ? 0 : Math.min(Math.max(num, 0), 100);
}

/**
 * Renderiza uma tag (chip) de cidade selecionada.
 */
export function renderCityTag(city) {
  const safeName = escapeHtml(city.nome);
  const safeId = escapeHtml(city.id);
  return `
    <span class="animate-pop inline-flex items-center gap-1.5 rounded-full bg-indigo-500/15 border border-indigo-500/30 pl-3 pr-1.5 py-1 text-xs font-medium text-indigo-200 shadow-sm">
      <span>${safeName}</span>
      <button
        type="button"
        data-remove-city="${safeId}"
        aria-label="Remover ${safeName}"
        class="inline-flex h-5 w-5 items-center justify-center rounded-full text-indigo-300 hover:bg-indigo-500/30 hover:text-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-sky-400 transition-colors duration-200"
      >
        <svg class="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path d="M6.28 5.22a.75.75 0 00-1.06 1.06L8.94 10l-3.72 3.72a.75.75 0 101.06 1.06L10 11.06l3.72 3.72a.75.75 0 101.06-1.06L11.06 10l3.72-3.72a.75.75 0 00-1.06-1.06L10 8.94 6.28 5.22z" />
        </svg>
      </button>
    </span>
  `;
}

/**
 * Renderiza skeleton cards durante o carregamento para manter estabilidade de layout (CLS zero).
 */
export function renderSkeletonGrid(count = 3) {
  const safeCount = Math.max(1, Math.min(count, 9));
  const card = `
    <article class="rounded-2xl bg-slate-900/80 border border-slate-800/80 p-5 shadow-lg animate-pulse" aria-hidden="true">
      <div class="flex items-center justify-between pb-4 border-b border-slate-800">
        <div class="h-5 w-36 rounded-md bg-slate-800"></div>
        <div class="h-5 w-16 rounded-full bg-slate-800"></div>
      </div>
      <div class="flex gap-2 py-3 border-b border-slate-800/60">
        <div class="h-7 w-16 rounded-lg bg-slate-800"></div>
        <div class="h-7 w-16 rounded-lg bg-slate-800/60"></div>
        <div class="h-7 w-16 rounded-lg bg-slate-800/60"></div>
      </div>
      <div class="py-5 flex items-center gap-4">
        <div class="h-14 w-14 rounded-2xl bg-slate-800"></div>
        <div class="space-y-2 flex-1">
          <div class="h-7 w-28 rounded-md bg-slate-800"></div>
          <div class="h-4 w-40 rounded-md bg-slate-800/70"></div>
        </div>
      </div>
      <div class="grid grid-cols-2 gap-3">
        <div class="h-16 rounded-xl bg-slate-800/60"></div>
        <div class="h-16 rounded-xl bg-slate-800/60"></div>
      </div>
    </article>
  `;
  return `
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5" role="status" aria-label="Carregando previsões meteorológicas">
      ${card.repeat(safeCount)}
    </div>
  `;
}

/**
 * Renderiza estado vazio ou de erro de forma semântica e clara.
 */
export function renderEmptyOrErrorState({ icon = '🌤️', title, description, isError = false, actionLabel = '', actionId = '' }) {
  const borderClass = isError ? 'border-rose-500/30 bg-rose-950/15' : 'border-slate-800/80 bg-slate-900/50';
  const titleColor = isError ? 'text-rose-200' : 'text-slate-200';
  return `
    <section class="rounded-2xl border ${borderClass} px-6 py-14 text-center max-w-xl mx-auto my-4 animate-fade-slide">
      <div class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-slate-800/80 text-3xl shadow-inner" aria-hidden="true">
        ${icon}
      </div>
      <h3 class="text-base font-semibold ${titleColor}">${escapeHtml(title)}</h3>
      <p class="mt-1.5 text-sm text-slate-400 leading-relaxed">${escapeHtml(description)}</p>
      ${
        actionLabel && actionId
          ? `<button
              type="button"
              id="${escapeHtml(actionId)}"
              class="mt-5 inline-flex items-center gap-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 px-4 py-2 text-xs font-semibold border border-slate-700 transition-colors duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-sky-400"
            >
              ${escapeHtml(actionLabel)}
            </button>`
          : ''
      }
    </section>
  `;
}

/**
 * Renderiza o card completo de previsão de uma cidade.
 */
export function renderCityWeatherCard(city) {
  const dias = Array.isArray(city.dias) ? city.dias.filter(Boolean) : [];
  const safeId = escapeHtml(city.id);
  const safeNome = escapeHtml(city.nome || 'Município');

  if (!dias.length) {
    return `
      <article class="rounded-2xl bg-slate-900/90 border border-slate-800 p-5 shadow-lg">
        <header class="flex items-center justify-between pb-3 border-b border-slate-800">
          <h3 class="text-base font-bold text-slate-100 capitalize">${safeNome}</h3>
          <span class="rounded-full bg-slate-800 px-2.5 py-0.5 text-xs font-mono text-slate-400">#${safeId}</span>
        </header>
        <p class="py-8 text-center text-sm text-slate-400">Sem dados meteorológicos disponíveis.</p>
      </article>
    `;
  }

  const tabsHtml = dias
    .map((d, idx) => {
      const isSelected = idx === 0;
      const tabId = `tab-${safeId}-${idx}`;
      const panelId = `panel-${safeId}-${idx}`;
      const label = escapeHtml(d.dia || `Dia ${idx + 1}`);
      return `
        <button
          type="button"
          role="tab"
          id="${tabId}"
          aria-selected="${isSelected ? 'true' : 'false'}"
          aria-controls="${panelId}"
          tabindex="${isSelected ? '0' : '-1'}"
          data-tab-target="${panelId}"
          data-day-index="${idx}"
          class="day-tab-btn shrink-0 rounded-lg px-3 py-1.5 text-xs font-semibold transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-sky-400 ${
            isSelected
              ? 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/40 shadow-sm'
              : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60 border border-transparent'
          }"
        >
          ${label}
        </button>
      `;
    })
    .join('');

  const panelsHtml = dias
    .map((d, idx) => {
      const isSelected = idx === 0;
      const tabId = `tab-${safeId}-${idx}`;
      const panelId = `panel-${safeId}-${idx}`;
      const visual = resolveWeatherVisual(d.condicao, d.iconeIndex);
      const probValue = parseProbability(d.prob);
      const hasPrecip = Boolean(d.milimetros && String(d.milimetros).trim() !== '');
      const precipText = hasPrecip ? `${String(d.milimetros).trim().replace(/\s*mm$/i, '')} mm` : '0 mm';
      const hasVento = Boolean(d.vento && String(d.vento).trim() !== '');
      const hasDetails = hasPrecip || hasVento || probValue > 0;
      const condicaoFormatted = escapeHtml((d.condicao || visual.label).replace(/_/g, ' '));

      return `
        <section
          role="tabpanel"
          id="${panelId}"
          aria-labelledby="${tabId}"
          class="day-tab-panel ${isSelected ? 'block animate-fade-slide' : 'hidden'} pt-4"
        >
          <!-- Bloco Principal de Temperatura e Condição -->
          <div class="flex items-center justify-between gap-4 rounded-xl bg-slate-950/60 border border-slate-800/80 p-4">
            <div class="flex items-center gap-3.5">
              <span class="text-4xl select-none" role="img" aria-label="${condicaoFormatted}">${visual.emoji}</span>
              <div>
                <span class="inline-flex items-center rounded-md border px-2 py-0.5 text-[11px] font-medium capitalize ${visual.badgeClass}">
                  ${condicaoFormatted}
                </span>
                <div class="mt-1.5 flex flex-col gap-0.5">
                  <span class="inline-flex items-center gap-1 whitespace-nowrap text-2xl font-bold leading-tight tracking-tight text-amber-400" title="Temperatura Máxima">
                    <span aria-hidden="true">↑</span><span>${escapeHtml(d.maxima ?? '—')}°C</span>
                  </span>
                  <span class="inline-flex items-center gap-1 whitespace-nowrap text-sm font-semibold leading-tight text-sky-400" title="Temperatura Mínima">
                    <span aria-hidden="true">↓</span><span>${escapeHtml(d.minima ?? '—')}°C</span>
                  </span>
                </div>
              </div>
            </div>
            <div class="text-right">
              <span class="block text-[11px] uppercase tracking-wider text-slate-500 font-medium">Amplitude</span>
              <span class="text-sm font-mono font-semibold text-slate-300">
                ${d.maxima !== undefined && d.minima !== undefined ? `${Math.abs(d.maxima - d.minima)}°C` : '—'}
              </span>
            </div>
          </div>

          <!-- Métricas Detalhadas (Precipitação, Vento e Probabilidade) -->
          ${
            hasDetails
              ? `
            <dl class="mt-3 grid grid-cols-2 gap-2.5">
              <div class="rounded-xl bg-slate-800/40 border border-slate-800/80 px-3.5 py-2.5">
                <dt class="text-[11px] font-medium uppercase tracking-wider text-slate-400 flex items-center gap-1.5">
                  <span aria-hidden="true">💧</span> Precipitação
                </dt>
                <dd class="mt-1 text-sm font-semibold text-slate-100">
                  ${escapeHtml(precipText)}
                </dd>
              </div>
              <div class="rounded-xl bg-slate-800/40 border border-slate-800/80 px-3.5 py-2.5">
                <dt class="text-[11px] font-medium uppercase tracking-wider text-slate-400 flex items-center gap-1.5">
                  <span aria-hidden="true">💨</span> Vento
                </dt>
                <dd class="mt-1 text-sm font-semibold text-slate-100 truncate" title="${escapeHtml(d.vento || '—')}">
                  ${hasVento ? escapeHtml(d.vento) : '—'}
                </dd>
              </div>
            </dl>
            <div class="mt-3 rounded-xl bg-slate-800/30 border border-slate-800/60 px-3.5 py-2.5">
              <div class="flex items-center justify-between text-xs mb-1.5">
                <span class="text-slate-400 font-medium">Probabilidade de chuva</span>
                <span class="font-mono font-semibold ${probValue >= 60 ? 'text-sky-300' : 'text-slate-300'}">${escapeHtml(d.prob || `${probValue}%`)}</span>
              </div>
              <div class="h-2 w-full rounded-full bg-slate-800 overflow-hidden" role="progressbar" aria-valuenow="${probValue}" aria-valuemin="0" aria-valuemax="100">
                <div class="h-full rounded-full bg-gradient-to-r from-sky-500 to-indigo-400 transition-all duration-500" style="width: ${probValue}%"></div>
              </div>
            </div>
          `
              : `
            <p class="mt-3 rounded-xl bg-slate-800/25 border border-slate-800/50 px-3.5 py-3 text-xs text-slate-400 flex items-center gap-2">
              <svg class="h-4 w-4 text-slate-500 shrink-0" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a.75.75 0 000 1.5h.253a.25.25 0 01.244.304l-.459 2.066A1.75 1.75 0 0010.747 15H11a.75.75 0 000-1.5h-.253a.25.25 0 01-.244-.304l.459-2.066A1.75 1.75 0 009.253 9H9z" clip-rule="evenodd" />
              </svg>
              <span>Previsão estendida: dados de vento e precipitação detalhados concentram-se nos primeiros dias da consulta.</span>
            </p>
          `
          }
        </section>
      `;
    })
    .join('');

  // Mini resumo horizontal dos 5 dias para comparação rápida sem cliques extras
  const miniStripHtml = dias
    .map((d, idx) => {
      const v = resolveWeatherVisual(d.condicao, d.iconeIndex);
      const panelId = `panel-${safeId}-${idx}`;
      return `
        <button
          type="button"
          data-mini-day="${panelId}"
          class="flex flex-col items-center rounded-lg py-1.5 px-1 hover:bg-slate-800/70 transition-colors duration-150 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-sky-400"
          title="Ver detalhes de ${escapeHtml(d.dia || `Dia ${idx + 1}`)}"
        >
          <span class="text-[10px] font-medium text-slate-400 truncate max-w-[52px]">${escapeHtml(d.dia || `D${idx + 1}`)}</span>
          <span class="my-1 text-base leading-none" aria-hidden="true">${v.emoji}</span>
          <span class="text-[11px] font-mono font-semibold text-amber-300">${escapeHtml(d.maxima ?? '—')}°</span>
          <span class="text-[10px] font-mono text-sky-400">${escapeHtml(d.minima ?? '—')}°</span>
        </button>
      `;
    })
    .join('');

  return `
    <article
      class="city-card group rounded-2xl bg-slate-900/90 border border-slate-800/90 hover:border-indigo-500/40 p-5 shadow-xl shadow-black/20 transition-all duration-200 flex flex-col justify-between"
      data-city-name="${escapeHtml( (city.nome || '').toLowerCase() )}"
    >
      <div>
        <!-- Cabeçalho do Card -->
        <header class="flex items-center justify-between gap-2 pb-3.5 border-b border-slate-800/80">
          <div class="flex items-center gap-2.5 min-w-0">
            <span class="h-2.5 w-2.5 rounded-full bg-indigo-400 shrink-0"></span>
            <h3 class="text-base font-bold text-slate-100 truncate capitalize">${safeNome}</h3>
          </div>
          <span class="shrink-0 rounded-full bg-slate-800/90 border border-slate-700/70 px-2.5 py-0.5 text-[11px] font-mono text-slate-400">
            #${safeId}
          </span>
        </header>

        <!-- Navegação de Abas (Dias) -->
        <div
          role="tablist"
          aria-label="Dias da previsão para ${safeNome}"
          class="mt-3 flex items-center gap-1.5 overflow-x-auto no-scrollbar pb-1"
        >
          ${tabsHtml}
        </div>

        <!-- Painéis de cada Dia -->
        <div class="tab-panels-container">
          ${panelsHtml}
        </div>
      </div>

      <!-- Visão Rápida de 5 Dias no Rodapé do Card -->
      <footer class="mt-4 pt-3 border-t border-slate-800/70">
        <div class="grid grid-cols-5 gap-1">
          ${miniStripHtml}
        </div>
      </footer>
    </article>
  `;
}
