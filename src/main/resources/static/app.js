const playersGrid = document.getElementById('playersGrid');
const addPlayerBtn = document.getElementById('addPlayer');
const playerDialog = document.getElementById('playerDialog');
const playerForm = document.getElementById('playerForm');
let jogadores = JSON.parse(localStorage.getItem('jogadores')) || [];

function atualizarTela() {
    playersGrid.innerHTML = '';
    jogadores.forEach((nome, index) => {
        const div = document.createElement('div');
        div.className = 'card-jogador';
        div.innerHTML = `<span>${nome}</span>`;
        playersGrid.appendChild(div);
    });
    document.getElementById('confirmedCount').innerText = `${jogadores.length} jogadores`;
    localStorage.setItem('jogadores', JSON.stringify(jogadores));
}

addPlayerBtn.addEventListener('click', () => playerDialog.showModal());

playerForm.addEventListener('submit', (e) => {
    const nome = playerForm.player.value;
    jogadores.push(nome);
    atualizarTela();
    playerForm.reset();
});

// Inicializa
atualizarTela();
