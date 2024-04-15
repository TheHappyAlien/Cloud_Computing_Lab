import Game from "../../../stores/interface/game.interface";
import { GameStatus } from "../../../utils/enums";

interface EndOfGameMessageProps {
  game: Game;
  player: string;
}

export default function EndOfGameMessage({
  game,
  player,
}: EndOfGameMessageProps) {
  switch (game.status) {
    case GameStatus.DRAW:
      return <p>Draw!</p>;
    case GameStatus.PLAYER_1_WON:
      return <p>{game.player1 === player ? "You" : game.player1} won!</p>;
    case GameStatus.PLAYER_2_WON:
      return <p>{game.player2 === player ? "You" : game.player2} won!</p>;
  }
}
