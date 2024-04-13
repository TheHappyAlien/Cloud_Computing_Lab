package tictactoe.app.model.dto;
public class NameChangeMessage {
    private String oldName;
    private String newName;

    public NameChangeMessage() {
    }

    public NameChangeMessage(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

}
