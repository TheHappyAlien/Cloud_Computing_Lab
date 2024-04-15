package tictactoe.app.manager;

import tictactoe.app.enumeration.GameState;
import tictactoe.app.model.Game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TicTacToeManager {

    private final Map<String, Game> games;

    public TicTacToeManager() {
        games = new ConcurrentHashMap<>();
    }
    public synchronized Game joinGame(String playerId) {
        if (games.values().stream().anyMatch(game -> game.getPlayer1Id().equals(playerId) || (!playerId.isEmpty() && game.getPlayer2Id().equals(playerId)))) {
            return games.values().stream().filter(game -> game.getPlayer1Id().equals(playerId) || game.getPlayer2Id().equals(playerId)).findFirst().get();
        }

        for (Game game : games.values()) {
            if (!game.getPlayer1Id().isEmpty() && game.getPlayer2Id().isEmpty()) {
                game.setPlayer2Id(playerId);
                game.setPlayer1Move(true);
                game.setGameState(GameState.IN_PROGRESS);
                return game;
            }
        }

        Game game = new Game(playerId, "");
        games.put(game.getGameId(), game);
        return game;
    }

    public synchronized Game leaveGame(String playerId) {
        String gameId = getGameByPlayer(playerId) != null ? getGameByPlayer(playerId).getGameId() : null;
        if (gameId != null) {
            Game game = games.get(gameId);
            if (playerId.equals(game.getPlayer1Id())) {
                if (game.getPlayer2Id() != null) {
                    game.setPlayer1Id(game.getPlayer2Id());
                    game.setPlayer2Id(null);
                    game.setGameState(GameState.PENDING);
                    game.setBoard(new String[9]);
                } else {
                    games.remove(gameId);
                    return null;
                }
            } else if (playerId.equals(game.getPlayer2Id())) {
                game.setPlayer2Id(null);
                game.setGameState(GameState.PENDING);
                game.setBoard(new String[9]);
            }
            return game;
        }
        return null;
    }


    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    public Game getGameByPlayer(String playerId) {
        return games.values().stream().filter(game -> game.getPlayer1Id().equals(playerId) || (!playerId.isEmpty() &&
                game.getPlayer2Id().equals(playerId))).findFirst().orElse(null);
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
    }
}
