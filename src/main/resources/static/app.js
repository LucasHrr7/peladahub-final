// ==========================================
// CONFIGURAÇÃO DO BANCO LOCAL (localStorage)
// ==========================================

// Função auxiliar para ler dados do localStorage de forma segura
const loadFromStorage = (key, defaultValue) => {
    const data = localStorage.getItem(key);
    return data ? JSON.parse(data) : defaultValue;
};

// Função auxiliar para salvar dados no localStorage
const saveToStorage = (key, data) => {
    localStorage.setItem(key, JSON.stringify(data));
};

// Estado Inicial do Sistema (Lido do navegador ou zerado se for a primeira vez)
let state = loadFromStorage  ('peladahub_state', {
    jogadores: [],
    timesSorteados: [],
    filaDeTimes: [],
    partidaAtiva: null,
    historicoResultados: [],
    estatisticas: {
        jogosDisputados: 0,
        golsMarcados: 0,
        equipesSorteados: 0
    }
});

// Função para persistir qualquer mudança no estado do app
const salvarEstado = () => {
    saveToStorage('peladahub_state', state);
};

// ==========================================
// SELEÇÃO DE ELEMENTOS DO DOM
// ==========================================
const playersGrid = document.getElementById('playersGrid');
const confirmedCount = document.getElementById('confirmedCount');
const drawTeamsBtn = document.getElementById('drawTeams');
const addPlayerBtn = document.getElementById('addPlayer');
const playerDialog = document.getElementById('playerDialog');
const playerForm = document.getElementById('playerForm');
const resetAppButton = document.getElementById('resetAppButton');
const toast = document.getElementById('toast');

// Elementos da Partida
const matchPlace = document.getElementById('matchPlace');
const matchTitle = document.getElementById('match-title');
const teamAName = document.getElementById('teamAName');
const teamACaptain = document.getElementById('teamACaptain');
const teamBName = document.getElementById('teamBName');
const teamBCaptain = document.getElementById('teamBCaptain');
const scoreAEl = document.getElementById('scoreA');
const scoreBEl = document.getElementById('scoreB');
const goalABtn = document.getElementById('goalA');
const goalBBtn = document.getElementById('goalB');
const finishMatchBtn = document.getElementById('finishMatch');

// Painéis de Informação
const roundStatus = document.getElementById('roundStatus');
const roundDetail = document.getElementById('roundDetail');
const gamesPlayedEl = document.getElementById('gamesPlayed');
const goalsTotalEl = document.getElementById('goalsTotal');
const teamsTotalEl = document.getElementById('teamsTotal');
const progressBar = document.getElementById('progressBar');
const queueList = document.getElementById('queueList');
const resultsEl = document.getElementById('results');

// ==========================================
// FUNÇÕES AUXILIARES / UTILITÁRIAS
// ==========================================

function showToast(message) {
    toast.textContent = message;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3000);
}

// ==========================================
// LÓGICA DO JOGO E RENDERIZAÇÃO DA TELA
// ==========================================

function renderizarTela() {
    // 1. Renderizar Jogadores Disponíveis
    playersGrid.innerHTML = '';
    state.jogadores.forEach((jogador, index) => {
        const pCard = document.createElement('div');
        pCard.className = 'player-card';
        pCard.innerHTML = `
            <span class="avatar mini">${jogador.charAt(0).toUpperCase()}</span>
            <b>${jogador}</b>
            <button class="remove-player-btn" onclick="removerJogador(${index})">×</button>
        `;
        playersGrid.appendChild(pCard);
    });

    // Atualizar Contadores
    confirmedCount.textContent = `${state.jogadores.length} jogadores`;
    drawTeamsBtn.disabled = state.jogadores.length < 12; // Mínimo 12 para sortear 2 times de 6

    // 2. Renderizar Partida Ativa (Placar)
    if (state.partidaAtiva) {
        matchPlace.innerHTML = `<span class="live-pill"><i></i> AO VIVO</span> Quadra Principal`;
        matchTitle.textContent = `Partida #${state.estatisticas.jogosDisputados + 1}`;
        teamAName.textContent = state.partidaAtiva.timeA.nome;
        teamACaptain.textContent = `Capitão: ${state.partidaAtiva.timeA.jogadores[0]}`;
        teamBName.textContent = state.partidaAtiva.timeB.nome;
        teamBCaptain.textContent = `Capitão: ${state.partidaAtiva.timeB.jogadores[0]}`;
        scoreAEl.textContent = state.partidaAtiva.golsA;
        scoreBEl.textContent = state.partidaAtiva.golsB;
        
        goalABtn.disabled = false;
        goalBBtn.disabled = false;
        finishMatchBtn.disabled = false;
        roundStatus.textContent = "Partida rolando!";
        roundDetail.textContent = "Acompanhe e anote os gols no placar.";
    } else {
        matchPlace.textContent = "Nenhuma partida em andamento";
        matchTitle.textContent = "Aguardando sorteio";
        teamAName.textContent = "Time A";
        teamACaptain.textContent = "Aguardando";
        teamBName.textContent = "Time B";
        teamBCaptain.textContent = "Aguardando";
        scoreAEl.textContent = "0";
        scoreBEl.textContent = "0";
        
        goalABtn.disabled = true;
        goalBBtn.disabled = true;
        finishMatchBtn.disabled = true;

        if (state.jogadores.length >= 12) {
            roundStatus.textContent = "Pronto para o sorteio";
            roundDetail.textContent = "Clique em Sortear para montar as equipes.";
        } else {
            roundStatus.textContent = "Monte os times";
            roundDetail.textContent = `Adicione mais ${12 - state.jogadores.length} jogadores para iniciar.`;
        }
    }

    // 3. Renderizar Próximos Times na Fila
    queueList.innerHTML = '';
    if (state.filaDeTimes.length === 0) {
        queueList.innerHTML = `<p class="empty-state" style="padding: 1rem 0;">Nenhum time na fila de espera.</p>`;
    } else {
        state.filaDeTimes.forEach((time, index) => {
            const queueItem = document.createElement('div');
            queueItem.className = 'queue-item';
            queueItem.innerHTML = `
                <div style="display:flex; align-items:center; gap: 12px;">
                    <span class="queue-number">#${index + 1}</span>
                    <div>
                        <b>${time.nome}</b>
                        <small style="display:block; color:#666;">${time.jogadores.slice(0, 3).join(', ')}...</small>
                    </div>
                </div>
                <span class="status-badge" style="background: #eef7f2; color: #0b7a45; padding: 4px 8px; border-radius: 6px; font-size: 0.8rem; font-weight: 600;">Na fila</span>
            `;
            queueList.appendChild(queueItem);
        });
    }

    // 4. Renderizar Resumo / Estatísticas
    gamesPlayedEl.textContent = state.estatisticas.jogosDisputados;
    goalsTotalEl.textContent = state.estatisticas.golsMarcados;
    teamsTotalEl.textContent = state.estatisticas.equipesSorteados;
    
    // Barra de progresso visual simulando 10 rodadas
    const progressPercent = Math.min((state.estatisticas.jogosDisputados / 10) * 100, 100);
    progressBar.style.width = `${progressPercent}%`;

    // 5. Renderizar Histórico de Resultados
    resultsEl.innerHTML = '';
    if (state.historicoResultados.length === 0) {
        resultsEl.innerHTML = `<p class="empty-state">Os resultados aparecerão aqui quando uma partida terminar.</p>`;
    } else {
        state.historicoResultados.slice().reverse().forEach((jogo) => {
            const resCard = document.createElement('div');
            resCard.className = 'result-card';
            resCard.style.padding = '12px';
            resCard.style.borderBottom = '1px solid #eee';
            resCard.innerHTML = `
                <div style="display:flex; justify-content:space-between; font-weight:600; font-size: 0.95rem;">
                    <span>${jogo.timeA}</span>
                    <span style="color:#0b7a45;">${jogo.golsA} x ${jogo.golsB}</span>
                    <span>${jogo.timeB}</span>
                </div>
                <small style="display:block; color:#888; margin-top:4px; font-size:0.75rem;">Partida finalizada</small>
            `;
            resultsEl.appendChild(resCard);
        });
    }
}

// ==========================================
// OPERAÇÕES DO SISTEMA
// ==========================================

// Adicionar Novo Jogador
playerForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const formData = new FormData(playerForm);
    const nome = formData.get('player').trim();
    
    if (nome) {
        state.jogadores.push(nome);
        salvarEstado();
        renderizarTela();
        playerForm.reset();
        playerDialog.close();
        showToast(`${nome} foi confirmado na rodada!`);
    }
});

// Remover Jogador
window.removerJogador = function(index) {
    const nomeRemovido = state.jogadores[index];
    state.jogadores.splice(index, 1);
    salvarEstado();
    renderizarTela();
    showToast(`${nomeRemovido} removido.`);
};

// Sorteio das Equipes (Grupos de 6 jogadores)
drawTeamsBtn.addEventListener('click', () => {
    if (state.jogadores.length < 12) return;

    // Embaralhar jogadores
    const jEmbaralhados = [...state.jogadores].sort(() => Math.random() - 0.5);
    
    // Divide os jogadores em lotes de 6
    const numeroDeTimes = Math.floor(jEmbaralhados.length / 6);
    const novosTimes = [];

    const nomesEquipes = ["Alvinegro", "Verdão", "Azulão", "Rubro-Negro", "Canarinho", "Furacão"];

    for (let i = 0; i < numeroDeTimes; i++) {
        novosTimes.push({
            nome: nomesEquipes[i % nomesEquipes.length] || `Time ${i + 1}`,
            jogadores: jEmbaralhados.slice(i * 6, (i + 1) * 6)
        });
    }

    state.estatisticas.equipesSorteados += novosTimes.length;

    // A partida ativa pega os dois primeiros times
    state.partidaAtiva = {
        timeA: novosTimes[0],
        timeB: novosTimes[1],
        golsA: 0,
        golsB: 0
    };

    // Os outros times (se houver mais de 12 jogadores) entram na fila de espera
    state.filaDeTimes = novosTimes.slice(2);

    salvarEstado();
    renderizarTela();
    showToast("Times sorteados com sucesso!");
});

// Ações do Placar
goalABtn.addEventListener('click', () => {
    if (state.partidaAtiva) {
        state.partidaAtiva.golsA++;
        state.estatisticas.golsMarcados++;
        salvarEstado();
        renderizarTela();
    }
});

goalBBtn.addEventListener('click', () => {
    if (state.partidaAtiva) {
        state.partidaAtiva.golsB++;
        state.estatisticas.golsMarcados++;
        salvarEstado();
        renderizarTela();
    }
});

// Encerrar Partida
finishMatchBtn.addEventListener('click', () => {
    if (!state.partidaAtiva) return;

    // Registrar no histórico
    state.historicoResultados.push({
        timeA: state.partidaAtiva.timeA.nome,
        timeB: state.partidaAtiva.timeB.nome,
        golsA: state.partidaAtiva.golsA,
        golsB: state.partidaAtiva.golsB
    });

    state.estatisticas.jogosDisputados++;

    // Lógica de "Quem ganha continua"
    let vencedor = null;
    let perdedor = null;

    if (state.partidaAtiva.golsA >= state.partidaAtiva.golsB) {
        vencedor = state.partidaAtiva.timeA;
        perdedor = state.partidaAtiva.timeB;
    } else {
        vencedor = state.partidaAtiva.timeB;
        perdedor = state.partidaAtiva.timeA;
    }

    if (state.filaDeTimes.length > 0) {
        // Envia o perdedor para o fim da fila de espera
        state.filaDeTimes.push(perdedor);
        
        // Pega o próximo da fila para jogar contra o vencedor anterior
        const proximoDesafiante = state.filaDeTimes.shift();

        state.partidaAtiva = {
            timeA: vencedor,
            timeB: proximoDesafiante,
            golsA: 0,
            golsB: 0
        };
        showToast("Nova partida iniciada! O vencedor continua.");
    } else {
        // Se não houver times na fila, apenas encerra a rodada ativa
        state.partidaAtiva = null;
        showToast("Partida encerrada! Sem mais desafiantes na fila.");
    }

    salvarEstado();
    renderizarTela();
});

// ==========================================
// DIÁLOGO (POPUP) E BOTÕES DE CONTROLE
// ==========================================

addPlayerBtn.addEventListener('click', () => {
    playerDialog.showModal();
});

// Botão de fechar do modal
playerDialog.querySelector('.close').addEventListener('click', () => {
    playerDialog.close();
});

// ==========================================
// BOTÃO RESETAR PELADA (O CLEANER DO APP)
// ==========================================
resetAppButton.addEventListener('click', () => {
    if (confirm("Tem certeza que deseja resetar toda a pelada e apagar todos os jogadores e histórico?")) {
        // Apaga do localStorage do navegador
        localStorage.removeItem('peladahub_state');
        
        // Reseta o estado local do código
        state = {
            jogadores: [],
            timesSorteados: [],
            filaDeTimes: [],
            partidaAtiva: null,
            historicoResultados: [],
            estatisticas: {
                jogosDisputados: 0,
                golsMarcados: 0,
                equipesSorteados: 0
            }
        };
        
        salvarEstado();
        renderizarTela();
        showToast("A pelada foi totalmente resetada!");
    }
});

// ==========================================
// INICIALIZAÇÃO DO APP
// ==========================================
renderizarTela();
