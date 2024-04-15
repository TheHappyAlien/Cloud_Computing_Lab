package tictactoe.app.model.dto;

import tictactoe.app.enumeration.GameState;
import tictactoe.app.model.Game;

public class GameMessage implements Message{
    private String type;
    private String gameId;
    private String player1Name;
    private String player2Name;
    private String player1Id;
    private String player2Id;
    private String[] board;

    private GameState gameState;

    public GameMessage() {}

    public GameMessage(Game game) {
        this.gameId = game.getGameId();
        this.player1Id = game.getPlayer1Id();
        this.player2Id = game.getPlayer2Id();
        this.board = game.getBoard();
        this.gameState = game.getGameState();
    }

    /**
     * Getters and Setters
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1) {
        this.player1Id = player1;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2) {
        this.player2Id = player2;
    }

    public String[] getBoard() {
        return board;
    }

    public void setBoard(String[] board) {
        this.board = board;
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
