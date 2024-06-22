package tictactoe.app.model.dto;

public class PlayerAuthMessage {
    private String playerId;
    private String authToken;
   
    public PlayerAuthMessage() {
    }

    public PlayerAuthMessage(String playerId, String authToken) {
        this.playerId = playerId;
        this.authToken = authToken;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
