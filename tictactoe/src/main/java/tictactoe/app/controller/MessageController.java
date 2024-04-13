package tictactoe.app.controller;

import org.springframework.messaging.simp.annotation.SendToUser;
import tictactoe.app.enumeration.GameState;
import tictactoe.app.manager.TicTacToeManager;
import tictactoe.app.model.TicTacToe;
import tictactoe.app.model.dto.*;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final TicTacToeManager ticTacToeManager = new TicTacToeManager();

    private final String GAME_ENDPOINT = "/topic/game.";

    @MessageMapping("/game.join")
    @SendTo("/user/state")
    public TicTacToeMessage joinGame(@Payload JoinMessage message) {
        TicTacToe game = ticTacToeManager.joinGame(message.getPlayer());
        if (game == null || game.getPlayer1() == null) {
            TicTacToeMessage errorMessage = new TicTacToeMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Something went wrong.");
            return errorMessage;
        }

        TicTacToeMessage gameMessage = gameToMessage(game);
        gameMessage.setSender(message.getPlayer());
       if (game.getPlayer2() == null){
            gameMessage.setType("game.created");
        } else  {
            gameMessage.setType("game.joined");
        }

        return gameMessage;
    }

    @MessageMapping("/game.refreshAfterReload")
    @SendTo("/user/state")
    public TicTacToeMessage refresh(@Payload JoinMessage message) {
        TicTacToe game = ticTacToeManager.getGameByPlayer(message.getPlayer());
        if (game == null){
            return null;
        }
        TicTacToeMessage gameMessage = gameToMessage(game);

        gameMessage.setType("game.refreshAfterReload");
        gameMessage.setSender("message.getPlayer()");
        return gameMessage;
    }

    @MessageMapping("/user.changeName")
    @SendTo("/user/state")
    public NameChangedMessage changeName(@Payload NameChangeMessage message) {
        TicTacToe game = ticTacToeManager.getGameByPlayer(message.getOldName());
        NameChangedMessage nameChangedMessage = new NameChangedMessage();
        nameChangedMessage.setOldName(message.getOldName());
        nameChangedMessage.setNewName(message.getNewName());
        nameChangedMessage.setType("user.nameChanged");
        if (game == null) return nameChangedMessage;
        if (game.getPlayer1().equals(message.getOldName())) game.setPlayer1(message.getNewName());
        if (game.getPlayer2() != null && game.getPlayer2().equals(message.getOldName())) {
            game.setPlayer2(message.getNewName());
            nameChangedMessage.setPlayer2(true);
        }
        if (game.getTurn().equals(message.getOldName())) {
            game.setTurn(message.getNewName());
            nameChangedMessage.setTurnChanged(true);
        }

        return nameChangedMessage;
    }

    @MessageMapping("/game.leave")
    public void leaveGame(@Payload PlayerMessage message) {
        TicTacToe game = ticTacToeManager.leaveGame(message.getPlayer());
        if (game != null) {
            TicTacToeMessage gameMessage = gameToMessage(game);
            gameMessage.setType("game.left");
            messagingTemplate.convertAndSend("/topic/game." + game.getGameId(), gameMessage);
        }
    }

    @MessageMapping("/game.move")
    public void makeMove(@Payload TicTacToeMessage message) {
        String player = message.getSender();
        String gameId = message.getGameId();
        int move = message.getMove();
        TicTacToe game = ticTacToeManager.getGame(gameId);

        if (game == null || game.isGameOver()) {
            TicTacToeMessage errorMessage = new TicTacToeMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Game not found or is already over.");
            this.messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, errorMessage);
            return;
        }

        if (game.getGameState().equals(GameState.WAITING_FOR_PLAYER)) {
            TicTacToeMessage errorMessage = new TicTacToeMessage();
            errorMessage.setType("error");
            errorMessage.setContent("Game is waiting for another player to join.");
            this.messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, errorMessage);
            return;
        }

        if (game.getTurn().equals(player)) {
            game.makeMove(player, move);

            TicTacToeMessage gameStateMessage = new TicTacToeMessage(game);
            gameStateMessage.setType("game.move");
            this.messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, gameStateMessage);

            if (game.isGameOver()) {
                TicTacToeMessage gameOverMessage = gameToMessage(game);
                gameOverMessage.setType("game.gameOver");
                this.messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, gameOverMessage);
                ticTacToeManager.removeGame(gameId);
            }
        }
    }

    @EventListener
    public void SessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getSessionAttributes() == null) {
            return;
        }
        String gameId = headerAccessor.getSessionAttributes().get("gameId").toString();
        String player = headerAccessor.getSessionAttributes().get("player").toString();
        TicTacToe game = ticTacToeManager.getGame(gameId);
        if (game != null) {
            if (game.getPlayer1().equals(player)) {
                game.setPlayer1(null);
                if (game.getPlayer2() != null) {
                    game.setGameState(GameState.PLAYER2_WON);
                    game.setWinner(game.getPlayer2());
                } else {
                    ticTacToeManager.removeGame(gameId);
                }
            } else if (game.getPlayer2() != null && game.getPlayer2().equals(player)) {
                game.setPlayer2(null);
                if (game.getPlayer1() != null) {
                    game.setGameState(GameState.PLAYER1_WON);
                    game.setWinner(game.getPlayer1());
                } else {
                    ticTacToeManager.removeGame(gameId);
                }
            }
            TicTacToeMessage gameMessage = gameToMessage(game);
            gameMessage.setType("game.gameOver");
            messagingTemplate.convertAndSend(GAME_ENDPOINT + gameId, gameMessage);
            ticTacToeManager.removeGame(gameId);
        }
    }

    private TicTacToeMessage gameToMessage(TicTacToe game) {
        TicTacToeMessage message = new TicTacToeMessage();
        message.setGameId(game.getGameId());
        message.setPlayer1(game.getPlayer1());
        message.setPlayer2(game.getPlayer2());
        message.setBoard(game.getBoard());
        message.setTurn(game.getTurn());
        message.setGameState(game.getGameState());
        message.setWinner(game.getWinner());
        return message;
    }
}
