package tictactoe.app.model;

import tictactoe.app.enumeration.GameState;

import java.util.Objects;
import java.util.UUID;

public class Game {
    private String gameId = "";
    private String[] board;
    private String player1Name = "";
    private String player2Name = "";
    private String player1Id = "";
    private String player2Id = "";
    private boolean player1Move = true;
    public boolean isPlayer1Move() {
        return player1Move;
    }


    public void setPlayer1Move(boolean player1Move) {
        this.player1Move = player1Move;
    }


    private GameState gameState;

    public Game(String player1Id, String player2Id) {
        this.gameId = UUID.randomUUID().toString();
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.board = new String[9];
        for (int i = 0; i < 9; i++) {
            this.board[i] = "";
        }
        gameState = GameState.PENDING;
    }


    public void makeMove(String player, int move) {
        if (Objects.equals(board[move], "")) {
            board[move] = Objects.equals(player, player1Id) ? "X" : "O";
            player1Move = !player1Move;
            updateGame();
        }
    }


    private static final int[][] winningMoves = {
            {0, 1, 2}, // Top row
            {3, 4, 5}, // Middle row
            {6, 7, 8}, // Bottom row
            {0, 3, 6}, // Left column
            {1, 4, 7}, // Middle column
            {2, 5, 8}, // Right column
            {0, 4, 8}, // Diagonal from top-left to bottom-right
            {2, 4, 6}  // Diagonal from top-right to bottom-left
    };

    private void updateGame() {
        for (int[] moves: winningMoves) {
            if (!board[moves[0]].isEmpty() && board[moves[0]].equals(board[moves[1]]) && board[moves[1]].equals(board[moves[2]])) {
                gameState = board[moves[0]].equals("X") ? GameState.PLAYER_1_WON : GameState.PLAYER_2_WON;
                return;
            }
        }

        boolean isFull = true;
        for (int i = 0; i < 9; i++) {
            if (board[i].isEmpty()) {
                isFull = false;
                break;
            }
        }
        if (isFull) {
            gameState = GameState.DRAW;
        }
    }

    public boolean isGameOver() {
        return gameState == GameState.DRAW || gameState == GameState.PLAYER_1_WON || gameState == GameState.PLAYER_2_WON;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String[] getBoard() {
        return board;
    }

    public void setBoard(String[] board) {
        this.board = board;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }
}

