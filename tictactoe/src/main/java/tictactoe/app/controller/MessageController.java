package tictactoe.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import tictactoe.app.enumeration.GameState;
import tictactoe.app.manager.TicTacToeManager;
import tictactoe.app.model.Game;
import tictactoe.app.model.dto.ErrorMessage;
import tictactoe.app.model.dto.GameMessage;
import tictactoe.app.model.dto.MoveMessage;
import tictactoe.app.model.dto.NameChangeMessage;
import tictactoe.app.model.dto.NameChangedMessage;
import tictactoe.app.model.dto.PlayerMessage;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final TicTacToeManager ticTacToeManager = new TicTacToeManager();

    private final String GAME_ENDPOINT = "/game";
    private final String USER_ENDPOINT = "/user";

    @MessageMapping("/join")
    public void joinGame(@Payload String playerId) {
        Game game = ticTacToeManager.joinGame(playerId);
        if (game == null || game.getPlayer1Id() == null) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Something went wrong.");
            messagingTemplate.convertAndSend(USER_ENDPOINT +  playerId, errorMessage);
        }

        GameMessage gameMessage = gameToMessage(game);
        gameMessage.setType("joined");


        messagingTemplate.convertAndSend(USER_ENDPOINT +  game.getPlayer1Id(), gameMessage);
        messagingTemplate.convertAndSend(USER_ENDPOINT +  game.getPlayer2Id(), gameMessage);
    }

    // @MessageMapping("/refresh")
    // @SendTo("/user")
    // public GameMessage refresh(@Payload JoinMessage message) {
    //     Game game = ticTacToeManager.getGameByPlayer(message.getPlayerId());
    //     if (game == null){
    //         return null;
    //     }
    //     GameMessage gameMessage = gameToMessage(game);

    //     gameMessage.setType("game.refreshAfterReload");
    //     gameMessage.setSender("message.getPlayer()");
    //     return gameMessage;
    // }

    @MessageMapping("/changeName")
    public NameChangedMessage changeName(@Payload NameChangeMessage message) {
        Game game = ticTacToeManager.getGameByPlayer(message.getPlayerId());
        NameChangedMessage nameChangedMessage = new NameChangedMessage();
        nameChangedMessage.setOldName(message.getPlayerId());
        nameChangedMessage.setNewName(message.getNewName());

        if (game == null) { 
            messagingTemplate.convertAndSend(USER_ENDPOINT +  message.getPlayerId(), nameChangedMessage);
        }
        if (game.getPlayer1Id().equals(message.getPlayerId())) game.setPlayer1Name(message.getNewName());

        if (game.getPlayer2Id() != null && game.getPlayer2Id().equals(message.getPlayerId())) {
            game.setPlayer2Name(message.getNewName());
        }

        messagingTemplate.convertAndSend(USER_ENDPOINT +  message.getPlayerId(), nameChangedMessage);
        return nameChangedMessage;
    }

    @MessageMapping("/leave")
    public void leaveGame(@Payload PlayerMessage message) {
        Game game = ticTacToeManager.leaveGame(message.getPlayer());
        String gameId = message.getGameId();
        if (game != null) {
            GameMessage gameMessage = gameToMessage(game);
            gameMessage.setType("left");
            messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, gameMessage);
        }
    }

    @MessageMapping("/move")
    public void makeMove(@Payload MoveMessage message) {
        String playerId = message.getPlayerId();
        String gameId = message.getGameId();
        int move = message.getSquare();
        Game game = ticTacToeManager.getGame(gameId);

        if (game == null || game.isGameOver()) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Game not found or is already over.");
            this.messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, errorMessage);
            return;
        }

        if (game.getGameState().equals(GameState.PENDING)) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Game is waiting for another player to join.");
            this.messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, errorMessage);
            return;
        }

        if (game.getPlayer1Id().equals(playerId) ? game.isPlayer1Move() : !game.isPlayer1Move()) {
            game.makeMove(playerId, move);

            GameMessage gameStateMessage = new GameMessage(game);
            gameStateMessage.setType("move");
            this.messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, gameStateMessage);

            if (game.isGameOver()) {
                GameMessage gameOverMessage = gameToMessage(game);
                gameOverMessage.setType("gameOver");
                this.messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, gameOverMessage);
                ticTacToeManager.removeGame(gameId);
            }
        }
    }

    @EventListener
    public void SessionDisconnectEvent(SessionDisconnectEvent event) {
        // StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        // if (sessionAttributes.isEmpty()) {
        //     return;
        // }
        // String gameId = headerAccessor.getFirstNativeHeader("gameId");
        // String player = headerAccessor.getFirstNativeHeader("playerId");
        // Game game = ticTacToeManager.getGame(gameId);
        // if (game != null) {
        //     if (game.getPlayer1Id().equals(player)) {
        //         game.setPlayer1Id(null);
        //         if (game.getPlayer2Id() != null) {
        //             game.setGameState(GameState.PLAYER_2_WON);
        //         } else {
        //             ticTacToeManager.removeGame(gameId);
        //         }
        //     } else if (game.getPlayer2Id() != null && game.getPlayer2Id().equals(player)) {
        //         game.setPlayer2Id(null);
        //         if (game.getPlayer1Id() != null) {
        //             game.setGameState(GameState.PLAYER_1_WON);
        //         } else {
        //             ticTacToeManager.removeGame(gameId);
        //         }
        //     }
        //     GameMessage gameMessage = gameToMessage(game);
        //     gameMessage.setType("gameOver");
        //     messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, gameMessage);
        //     ticTacToeManager.removeGame(gameId);
        // }
    }

    private GameMessage gameToMessage(Game game) {
        GameMessage message = new GameMessage();
        message.setGameId(game.getGameId());
        message.setPlayer1Id(game.getPlayer1Id());
        message.setPlayer2Id(game.getPlayer2Id());
        message.setBoard(game.getBoard());
        message.setGameState(game.getGameState());
        return message;
    }
}
