package tictactoe.app.model.dto;

public class JoinMessage implements Message{
    private String type;
    private String gameId;
    private String playerId;


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

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String player) {
        this.playerId = player;
    }
}
