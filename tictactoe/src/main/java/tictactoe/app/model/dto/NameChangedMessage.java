package tictactoe.app.model.dto;

public class NameChangedMessage implements Message{

    private String oldName;
    private String newName;
    private boolean isPlayer2 = false;
    private boolean turnChanged = false;
    private String type;

    public NameChangedMessage() {
    }

    public NameChangedMessage(String oldName, String newName, boolean isPlayer2, boolean turnChanged) {
        this.oldName = oldName;
        this.newName = newName;
        this.isPlayer2 = isPlayer2;
        this.turnChanged = turnChanged;
    }


    public boolean isTurnChanged() {
        return turnChanged;
    }

    public void setTurnChanged(boolean turnChanged) {
        this.turnChanged = turnChanged;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public boolean isPlayer2() {
        return isPlayer2;
    }

    public void setPlayer2(boolean player2) {
        isPlayer2 = player2;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getType() {
        return type;
    }


    public String getGameId() {
        return null;
    }

    public void setType(String type) {
        this.type = type;
    }
}
