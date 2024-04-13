let stompClient = null;
let game = null;
let player = null;
let userId = null;
const SERVER = "http://localhost:8080"
const player1Label = document.querySelector("#player1");

const sendMessage = (message) => {
    stompClient.send(`/app/${message.type}`, {}, JSON.stringify(message));
}

const makeMove = (move) => {
    sendMessage({
        type: "game.move",
        move: move,
        turn: game.turn,
        sender: player,
        gameId: game.gameId
    });
}

const subscribeToGame = (message) => {
    const socket = new SockJS(SERVER + '/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe(`/topic/game.${message.gameId}`, function (message) {
            handleMessage(JSON.parse(message.body));
        });
    });
}

const messagesTypes = {
    "game.join": (message) => {
        updateGame(message);
    },
    "user.refreshAfterReload": (message) => {
        if (message === null || message.sender !== player) return;
        updateGame(message);
    },
    "game.gameOver": (message) => {
        updateGame(message);
        if (message.gameState === 'TIE') toastr.success(`Game over! It's a tie`);
        else showWinner(message.winner);
    },
    "user.nameChanged": (message) => {
        if (message.oldName !== player) return;
        player = message.newName;
        if (message.isPlayer2) {
            document.querySelector("#player2").innerHTML = message.newName;
        } else {
            document.querySelector("#player1").innerHTML = message.newName;
        }
        if (message.turnChanged) {
            document.querySelector("#turn").innerHTML = player;
        }
    },
    "game.created": (message) => {
        if (game !== null && game.gameId !== message.gameId) return;

        if (message.player1 !== player) return;
        subscribeToGame(message);
        updateGame(message);
    },
    "game.joined": (message) => {
        if (game !== null && game.gameId !== message.gameId) return;
        player = localStorage.getItem("playerName");
        if (message.player2 === player) {
            subscribeToGame(message);;
            updateGame(message);
        }
        if (message.player1 === player) updateGame(message);
    },
    "game.move": (message) => {
        updateGame(message);
    },
    "game.left": (message) => {
        updateGame(message);
        if (message.winner) showWinner(message.winner);
    },
    "error": (message) => {
        toastr.error(message.content);
    }
}

const handleMessage = (message) => {
    if (messagesTypes[message.type])
        messagesTypes[message.type](message);
}

const messageToGame = (message) => {
    return {
        gameId: message.gameId,
        board: message.board,
        turn: message.turn,
        player1: message.player1,
        player2: message.player2,
        gameState: message.gameState,
        winner: message.winner
    }
}

const showWinner = (winner) => {
    toastr.success(`The winner is ${winner}!`);
    const winningPositions = getWinnerPositions(game.board);
    if (winningPositions && winningPositions.length === 3) {
        winningPositions.forEach(position => {
            const row = Math.floor(position / 3);
            const cell = position % 3;
            let cellElement = document.querySelector(`.row-${row} .cell-${cell} span`);
            cellElement.style.backgroundColor = '#b3e6ff';
        });
    }
}

const setPlayerNameLabel = () => {
    let playerName = localStorage.getItem("playerName");
    if (!playerName) {
        changeName();
        playerName = localStorage.setItem("playerName");
    };
    player1Label.innerHTML = playerName;
}

const joinGame = () => {
    if (game !== null) return;
    if (player == null) {
        changeName();
    }
    sendMessage({
        type: "game.join",
        player: player
    });
}

const changeName = () => {
    const oldName = localStorage.getItem("playerName");
    const newName = prompt("Enter your name:");
    if (newName == null || newName == "null") return;
    localStorage.removeItem("playerName");
    localStorage.setItem("playerName", newName);
    sendMessage({
        type: "user.changeName",
        oldName: oldName,
        newName: newName
    });
}

const connect = (userId) => {
    const socket = new SockJS(SERVER +'/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/state', function (message) {
            handleMessage(JSON.parse(message.body));
        });
    });
}

const updateGame = (message) => {
    if (message == null) return;
    game = messageToGame(message);
    updateBoard(message.board);
    document.getElementById("player1").innerHTML = message.player1;
    document.getElementById("player2").innerHTML = message.player2 || (message.winner ? '-' : 'Waiting for player 2...');
    document.getElementById("turn").innerHTML = message.turn;
    document.getElementById("winner").innerHTML = message.winner || '-';
}

const updateBoard = (board) => {
    let counter = 0;
    board.forEach((row, rowIndex) => {
        row.forEach((cell, cellIndex) => {
            const cellElement = document.querySelector(`.row-${rowIndex} .cell-${cellIndex}`);
            cellElement.innerHTML = cell === ' ' ? '<button onclick="makeMove(' + counter + ')"> </button>' : `<span class="cell-item">${cell}</span>`;
            counter++;
        });
    });
}

const getWinnerPositions = (board) => {
    const winnerPositions = [];

    for (let i = 0; i < 3; i++) {
        if (board[i][0] === board[i][1] && board[i][1] === board[i][2] && board[i][0] !== ' ') {
            winnerPositions.push(i * 3);
            winnerPositions.push(i * 3 + 1);
            winnerPositions.push(i * 3 + 2);
        }
    }

    for (let i = 0; i < 3; i++) {
        if (board[0][i] === board[1][i] && board[1][i] === board[2][i] && board[0][i] !== ' ') {
            winnerPositions.push(i);
            winnerPositions.push(i + 3);
            winnerPositions.push(i + 6);
        }
    }

    if (board[0][0] === board[1][1] && board[1][1] === board[2][2] && board[0][0] !== ' ') {
        winnerPositions.push(0);
        winnerPositions.push(4);
        winnerPositions.push(8);
    }

    if (board[0][2] === board[1][1] && board[1][1] === board[2][0] && board[0][2] !== ' ') {
        winnerPositions.push(2);
        winnerPositions.push(4);
        winnerPositions.push(6);
    }

    return winnerPositions;
}

const generateUid = function(){
    return Date.now().toString(36) + Math.random().toString(36).slice(2);
}

window.onload = function () {
    userId = generateUid;
    localStorage.setItem("UID", userId)
    player = localStorage.getItem("playerName");
    connect(userId);
    setTimeout(sendUpdateRequest, 200);
}

function sendUpdateRequest() {
    let playerName = localStorage.getItem("playerName");
    sendMessage({
        type: "game.refreshAfterReload",
        player: playerName
    });
    updateBoard();
}

setPlayerNameLabel();
document.querySelector('#name-change-button').addEventListener('click', changeName);
document.querySelector('#join-button').addEventListener('click', joinGame);
