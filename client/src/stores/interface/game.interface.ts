import { Figure, GameStatus } from "../../utils/enums";

export default interface Game {
  id: string;
  player1: string;
  player2: string;
  square1: Figure;
  square2: Figure;
  square3: Figure;
  square4: Figure;
  square5: Figure;
  square6: Figure;
  square7: Figure;
  square8: Figure;
  square9: Figure;
  status: GameStatus;
}
