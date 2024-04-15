package tictactoe.app.model.dto;

public class ErrorMessage implements Message{
    private String type = "error";
    private String content;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
