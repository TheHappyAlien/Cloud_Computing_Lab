import TicTacToe from "../components/GamePage/TicTacToe";
import WelcomeText from "../components/GamePage/WelcomeText";
import { Button } from "../components/shadcn/button";
import { Label } from "../components/shadcn/label";
import { useGamesStore } from "../stores/game-store";
import { useUserStore } from "../stores/user-store";
import { useEffect } from "react";
import { GameStatus } from "../utils/enums";

export default function GamePage() {
  const {
    user
  } = useUserStore();
  
  const {
    // pendingGames,
    // inProgressGames,
    game,
    connect,
    disconnect,
    // createGame,
    joinGame,
    makeMove,
  } = useGamesStore();
  // const myPendingGames = pendingGames.filter((g) => g.player2 === user);


  useEffect(() => {
    console.log({ user });
    connect(user?.email || "");
    return () => {
      disconnect();
    };
  }, []);

  return (
    <div className="flex">
      <div className="w-1/4 mx-3 h-screen pt-10 space-y-2 flex flex-col p-5 gray-400">
        {/* <Button
          className="w-full"
          onClick={() => connect(user.id)}
        >
          Connect
        </Button> */}
        {/* <Button
          className="w-full"
          onClick={() => disconnect()}
        >
          Disconnect
        </Button> */}
      <WelcomeText user={user?.email || ""} />
      <Button onClick={() => joinGame({playerId: user?.email || "", authToken: user?.accessToken || ""})}>Join</Button>
      </div>

      <div className="flex flex-col flex-1">
        <div className="grid grid-cols-3 ms-8 mt-8 gap-8">
          {game === undefined || game === null ? (
            <Label>No games in progress</Label>
          ) : game.status === GameStatus.PENDING ? (
            <Label>Waiting for opponent</Label>
          ) : (
              <div>
                <TicTacToe
                  game={game}
                  player={user?.email!}
                  makeMove={(position: number) =>
                    makeMove({ gameId: game.id, square: position, playerId: user?.email! })
                  }
                />
              </div>
            )
          }
        </div>
      </div> 
    </div>
  );
}
