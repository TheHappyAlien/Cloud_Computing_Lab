package tictactoe.app.model.dto;
public class NameChangeMessage {
    private String playerId;
    private String newName;

    public NameChangeMessage() {
    }

    public NameChangeMessage(String playerId, String newName) {
        this.playerId = playerId;
        this.newName = newName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getNewName() {
        return newName;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

}
