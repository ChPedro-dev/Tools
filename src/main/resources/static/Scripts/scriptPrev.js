/* ─────────────────────────────────────────────────────────────
   scriptPrev.js  —  Previsão do Tempo (SIMEPAR / CWB scraper)
   Endpoints consumidos:
     GET  /cidades
     POST /previsao/scrap/cwb?acao={acao}   body: List<CidadeDto>
     GET  /previsao/status
───────────────────────────────────────────────────────────── */

'use strict';

/* ── State ── */
const state = {
  allCities: [],   // CidadeDto[] carregadas da API
  selected:  [],   // CidadeDto[] selecionadas pelo usuário
  loading:   false,
};

/* ── DOM refs ── */
const searchEl   = document.getElementById('cidade-search');
const dropdown   = document.getElementById('dropdown');
const tagsWrap   = document.getElementById('tags-wrap');
const acaoSel    = document.getElementById('acao-select');
const customWrap = document.getElementById('custom-acao-wrap');
const customIn   = document.getElementById('acao-custom');
const btnBuscar  = document.getElementById('btn-buscar');
const prevPanel  = document.getElementById('prev-panel');
const resHeader  = document.getElementById('results-header');
const resMeta    = document.getElementById('result-meta');
const resCount   = document.getElementById('result-count');
const horarioEl  = document.getElementById('horario');
const toast      = document.getElementById('toast');
const popup      = document.getElementById('loadingPopup');

/* ── Utils ── */
function showToast(msg, ms = 4500) {
  toast.textContent = msg;
  toast.style.display = 'block';
  clearTimeout(showToast._t);
  showToast._t = setTimeout(() => { toast.style.display = 'none'; }, ms);
}

function getAcao() {
  return acaoSel.value === 'custom' ? customIn.value.trim() : acaoSel.value;
}

function updateBtn() {
  btnBuscar.disabled = state.selected.length === 0 || state.loading;
}

/* ── Busca horário da última atualização ── */
async function fetchTime() {
  try {
    const res = await fetch('/previsao/status');
    if (!res.ok) return;
    const txt = await res.text();
    if (txt && txt !== 'null') horarioEl.textContent = txt;
  } catch (_) {}
}

/* ── Carrega lista de cidades ── */
async function loadCities() {
  try {
    const res = await fetch('/cidades');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    state.allCities = await res.json();
  } catch (e) {
    showToast('Erro ao carregar cidades: ' + e.message, 7000);
    prevPanel.innerHTML =
      '<div class="state-box"><div class="icon">⚠️</div>' +
      '<p>Não foi possível carregar as cidades.<br>Verifique se a API está em execução.</p></div>';
  }
}

/* ── Dropdown de busca ── */
function renderDropdown(query) {
  const q = query.toLowerCase();
  const ids = new Set(state.selected.map(c => c.id));
  const hits = state.allCities
    .filter(c =>
      !ids.has(c.id) &&
      (c.nome.toLowerCase().includes(q) || (c.busca || '').toLowerCase().includes(q))
    )
    .slice(0, 60);

  dropdown.innerHTML = '';
  if (!hits.length) {
    dropdown.innerHTML = '<div class="dd-empty">Nenhuma cidade encontrada</div>';
  } else {
    hits.forEach(city => {
      const el = document.createElement('div');
      el.className = 'dd-item';
      el.textContent = city.nome;
      el.addEventListener('mousedown', e => { e.preventDefault(); selectCity(city); });
      dropdown.appendChild(el);
    });
  }
  dropdown.classList.add('open');
}

function closeDropdown() {
  dropdown.classList.remove('open');
  dropdown.innerHTML = '';
}

searchEl.addEventListener('input', () => {
  const q = searchEl.value.trim();
  if (!q) { closeDropdown(); return; }
  renderDropdown(q);
});
searchEl.addEventListener('focus', () => {
  if (searchEl.value.trim()) renderDropdown(searchEl.value.trim());
});
searchEl.addEventListener('keydown', e => {
  if (e.key === 'Escape') { closeDropdown(); searchEl.blur(); }
});
document.addEventListener('click', e => {
  if (!document.getElementById('search-container').contains(e.target)) closeDropdown();
});

/* ── Seleção de cidades ── */
function selectCity(city) {
  if (state.selected.find(c => c.id === city.id)) return;
  state.selected.push(city);
  searchEl.value = '';
  closeDropdown();
  renderTags();
  updateBtn();
}

function removeCity(id) {
  state.selected = state.selected.filter(c => c.id !== id);
  renderTags();
  updateBtn();
}

function renderTags() {
  tagsWrap.innerHTML = '';
  state.selected.forEach(city => {
    const tag = document.createElement('span');
    tag.className = 'tag';
    tag.innerHTML =
      city.nome +
      ' <button aria-label="Remover ' + city.nome + '" data-id="' + city.id + '">&times;</button>';
    tag.querySelector('button').addEventListener('click', () => removeCity(city.id));
    tagsWrap.appendChild(tag);
  });
}

/* ── Acao select ── */
acaoSel.addEventListener('change', () => {
  customWrap.style.display = acaoSel.value === 'custom' ? 'flex' : 'none';
});

/* ── POST /previsao/scrap/cwb ── */
async function fetchPrevisao() {
  const acao = getAcao();
  if (!acao)                    { showToast('Informe a ação antes de buscar.'); return; }
  if (!state.selected.length)   { showToast('Selecione ao menos uma cidade.'); return; }

  state.loading = true;
  updateBtn();
  btnBuscar.innerHTML =
    '<i class="fa-solid fa-circle-notch spin"></i> Buscando…';
  popup.showModal();
  renderSkeleton(state.selected.length);

  try {
    const res = await fetch('/previsao/scrap/cwb?acao=' + encodeURIComponent(acao), {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(state.selected),
    });

    if (!res.ok) {
      const txt = await res.text().catch(() => '');
      throw new Error('HTTP ' + res.status + (txt ? ': ' + txt : ''));
    }

    const data = await res.json();
    renderResults(data, acao);
    fetchTime();

  } catch (e) {
    showToast('Erro na requisição: ' + e.message, 7000);
    prevPanel.innerHTML =
      '<div class="state-box"><div class="icon">⚠️</div><p>' + e.message + '</p></div>';
    resHeader.style.display = 'none';
  } finally {
    state.loading = false;
    updateBtn();
    btnBuscar.innerHTML = '<i class="fa-solid fa-cloud-arrow-down"></i> Buscar previsão';
    popup.close();
  }
}

btnBuscar.addEventListener('click', fetchPrevisao);

/* ── Skeleton loading ── */
function renderSkeleton(n) {
  resHeader.style.display = 'none';
  prevPanel.innerHTML = '<div class="skeleton-grid">' +
    Array.from({ length: n }, () =>
      '<div class="skeleton-card">' +
      '<div class="skel skel-h"></div>' +
      '<div class="skel skel-r w80"></div>' +
      '<div class="skel skel-r w60"></div>' +
      '<div class="skel skel-r w70"></div>' +
      '<div class="skel skel-r w80"></div>' +
      '</div>'
    ).join('') + '</div>';
}

/* ── Ícone meteorológico ── */
const ICON_MAP = [
  ['chuva_fraca',  '🌦️'],
  ['chuva',        '🌧️'],
  ['tempestade',   '⛈️'],
  ['neve',         '❄️'],
  ['neblina',      '🌫️'],
  ['muito_nublado','☁️'],
  ['nublado',      '☁️'],
  ['parcialmente', '⛅'],
  ['poucas_nuven', '🌤️'],
  ['ensolarado',   '☀️'],
  ['sol',          '☀️'],
];

function weatherIcon(condicao, idx) {
  const k = (condicao || '').toLowerCase().replace(/\s+/g, '_');
  for (const [kw, em] of ICON_MAP) { if (k.includes(kw)) return em; }
  if (idx <= 1) return '☀️';
  if (idx <= 3) return '⛅';
  if (idx <= 5) return '🌧️';
  return '⛈️';
}

function probNum(prob) {
  const n = parseInt((prob || '').replace(/\D/g, ''), 10);
  return isNaN(n) ? 0 : Math.min(n, 100);
}

/* ── Renderiza resultados ── */
function renderResults(cities, acao) {
  if (!cities || !cities.length) {
    resHeader.style.display = 'none';
    prevPanel.innerHTML =
      '<div class="state-box"><div class="icon">🔍</div><p>Nenhum dado retornado pela API.</p></div>';
    return;
  }

  resHeader.style.display = 'flex';
  resCount.textContent = cities.length + ' cidade' + (cities.length > 1 ? 's' : '');
  resMeta.textContent  = 'Ação: ' + acao + ' · ' + new Date().toLocaleTimeString('pt-BR');

  prevPanel.innerHTML = cities.map(renderCard).join('');

  // Bind tab clicks
  prevPanel.querySelectorAll('.day-tab').forEach(btn => {
    btn.addEventListener('click', () => {
      const card  = btn.closest('.prev-card');
      const panel = btn.dataset.panel;
      card.querySelectorAll('.day-tab').forEach(b => b.classList.remove('active'));
      card.querySelectorAll('.day-panel').forEach(p => p.classList.remove('active'));
      btn.classList.add('active');
      card.querySelector('[data-id="' + panel + '"]').classList.add('active');
    });
  });
}

/* ── Card de uma cidade ── */
function renderCard(city) {
  const dias = city.dias || [];

  const tabs = dias.map((d, i) =>
    '<button class="day-tab' + (i === 0 ? ' active' : '') +
    '" data-panel="' + city.id + '-' + i + '">' +
    (d.dia || 'Dia ' + (i + 1)) + '</button>'
  ).join('');

  const panels = dias.map((d, i) => {
    const icon = weatherIcon(d.condicao, d.iconeIndex);
    const pn   = probNum(d.prob);
    return (
      '<div class="day-panel' + (i === 0 ? ' active' : '') +
      '" data-id="' + city.id + '-' + i + '">' +
        '<div class="weather-main">' +
          '<span class="weather-icon">' + icon + '</span>' +
          '<div class="temp-block">' +
            '<span class="temp-max">' + (d.maxima !== undefined ? d.maxima : '—') + '°C</span>' +
            '<span class="temp-min">' + (d.minima !== undefined ? d.minima : '—') + '°C</span>' +
            '<span class="condicao-txt">' + (d.condicao || '—') + '</span>' +
          '</div>' +
        '</div>' +
        '<div class="day-details">' +
          '<div class="detail-box"><div class="lbl">💧 Precipitação</div><div class="val">' + (d.milimetros || '—') + ' mm</div></div>' +
          '<div class="detail-box"><div class="lbl">💨 Vento</div><div class="val">' + (d.vento || '—') + '</div></div>' +
        '</div>' +
        (pn > 0 ?
          '<div class="prob-bar-wrap">' +
            '<div class="prob-bar-lbl"><span>Prob. chuva</span><span>' + (d.prob || '0%') + '</span></div>' +
            '<div class="prob-bar-bg"><div class="prob-bar-fill" style="width:' + pn + '%"></div></div>' +
          '</div>' : '') +
      '</div>'
    );
  }).join('');

  return (
    '<div class="prev-card">' +
      '<div class="prev-card-header">' +
        '<h3>' + (city.nome || 'Cidade') + '</h3>' +
        '<span class="city-id-badge">#' + city.id + '</span>' +
      '</div>' +
      (dias.length
        ? '<div class="day-tabs">' + tabs + '</div><div class="day-panels">' + panels + '</div>'
        : '<div class="day-panels" style="padding:16px;color:#6c757d;font-size:.85rem;">Sem dados disponíveis.</div>'
      ) +
    '</div>'
  );
}

/* ── Init ── */
window.addEventListener('load', () => {
  loadCities();
  fetchTime();
});
