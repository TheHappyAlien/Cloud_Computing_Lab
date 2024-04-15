import Game from "../../../stores/interface/game.interface";
import Cell from "./Cell";

interface BoardProps {
  game: Game;
  onMakeMove: (index: number) => void;
}

export default function Board({ game, onMakeMove }: BoardProps) {
  return (
    <div className="w-80 h-80 mx-auto grid grid-cols-3 grid-rows-3 gap-0.5 bg-white">
      <Cell value={game.square1} onMakeMove={() => onMakeMove(0)} />
      <Cell value={game.square2} onMakeMove={() => onMakeMove(1)} />
      <Cell value={game.square3} onMakeMove={() => onMakeMove(2)} />

      <Cell value={game.square4} onMakeMove={() => onMakeMove(3)} />
      <Cell value={game.square5} onMakeMove={() => onMakeMove(4)} />
      <Cell value={game.square6} onMakeMove={() => onMakeMove(5)} />

      <Cell value={game.square7} onMakeMove={() => onMakeMove(6)} />
      <Cell value={game.square8} onMakeMove={() => onMakeMove(7)} />
      <Cell value={game.square9} onMakeMove={() => onMakeMove(8)} />
    </div>
  );
}
