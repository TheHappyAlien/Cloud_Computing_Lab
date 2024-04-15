import { create } from "zustand";
// import CreateGameDto from "./dto/create-game.dto";
import MakeMoveDto from "./dto/make-move.dto";
// import Game from "./interface/game.interface";
// import Message from "./interface/messages.interface";
import { createJSONStorage, persist } from "zustand/middleware";
// import { Socket } from "socket.io-client";
// import { createSocket } from "../utils/socket-io";
// import { GameStatus } from "../utils/enums";
import { Client } from '@stomp/stompjs';
import Game from "./interface/game.interface";
// import { subscribe } from "diagnostics_channel";

function convertJsonToGame(json: any): Game {
  return {
    id: json.gameId || '',
    player1: json.player1Id || '',
    player2: json.player2Id || '',
    square1: json.board[0] || '',
    square2: json.board[1] || '',
    square3: json.board[2] || '',
    square4: json.board[3] || '',
    square5: json.board[4] || '',
    square6: json.board[5] || '',
    square7: json.board[6] || '',
    square8: json.board[7] || '',
    square9: json.board[8] || '',
    status: json.gameState || "IN_PROGRESS"
  };
}

const currentUrl = window.location.href;
const wsUrl = currentUrl.replace(/^http/, 'ws').replace(/\/[^/]*$/, '/ws');
console.log(wsUrl); // Output: ws://{frontend-ip}:81/ws

const client = new Client( {
    // brokerURL: "ws://localhost:8080/ws"
    brokerURL: wsUrl
});



client.onDisconnect = function (frame: any) {
  console.log('Disconnected: ' + frame);
}

export interface GameStore {
  connect: (userId: string) => void;
  disconnect: () => void;
  joinGame: (playerId: string) => void;
  game?: Game;
  makeMove: (makeMoveDto: MakeMoveDto) => void;
}

// let socket: Socket | null = null;

export const useGamesStore = create<GameStore>()(
  persist((set) => ({
    game: undefined,

    connect: (playerId: string) => {
      client.onConnect = function (frame: any) {
        console.log('Connected: ' + frame);
        client.subscribe('/user' + playerId,
          (message) => {
            const msg = JSON.parse(message.body);
            switch (msg.type) {
              case "joined":
                set({game: convertJsonToGame(msg)});
                client.connectHeaders = {
                  gameId: msg.gameId,
                  playerId: playerId
                }
                client.subscribe('/game' + msg.gameId,
                (message) => {
                  const msg = JSON.parse(message.body);
                  switch (msg.type) {
                    case "gameOver":
                    case "move":
                      set({game: convertJsonToGame(msg)})
                      break;
                    default:
                      break;
                  }
                });
                break;
              case "changeName":
                break;
              default:
                break;
            }
          }
        )
      };
      client.activate()

    },
    disconnect: () => {
      client.deactivate()
    },
    joinGame: (playerId: string) => {

      client.publish({
        destination: '/app/join',
        body: playerId
      })
    },
    makeMove: (message: MakeMoveDto) => {
      console.log("made move", message.gameId, message.square)
      client.publish({
        destination: `/app/move`,
        body: JSON.stringify(message)
      });
    }
  }),

  { name: 'gameStore',
    storage: createJSONStorage(() => localStorage),
  }) 
  
  // Persist configuration
  // (set, get) => ({
  //     pendingGames: [],
  //     inProgressGames: [],

  //     connect: (userId: string) => {
  //       console.log("connecting-store")
  //       const _socket = createSocket(userId);
  //       console.log("createSocket")
  //       socket = _socket;
  //       socket.connect();
  //       console.log("connect")
  //       _socket.on("connect", () => {
  //         socket?.emit("getGames", (games: Game[]) => {
  //           set({
  //             inProgressGames: games.filter(
  //               (g) => g.status === GameStatus.IN_PROGRESS
  //             ),
  //             pendingGames: games.filter(
  //               (g) => g.status === GameStatus.PENDING && g.player2 === userId
  //             ),
  //           });
  //         });
  //       });
  //       _socket.connect();
  //       console.log("_socket.connect()")
  //       _socket.on("gameCreated", (game: Game) => {
  //         set({ pendingGames: [...get().pendingGames, game] });
  //       });
  //       _socket.on("moveMade", (game: Game) => {
  //         set({
  //           inProgressGames: get().inProgressGames.map((g) =>
  //             g.id === game.id ? game : g
  //           ),
  //         });
  //       });
  //       _socket.on("gameJoined", (game: Game) => {
  //         set({
  //           pendingGames: get().pendingGames.filter((g) => g.id !== game.id),
  //           inProgressGames: [...get().inProgressGames, game],
  //         });
  //       });
  //     },
  //     disconnect: () => {
  //       socket?.disconnect();
  //       set({ pendingGames: [], inProgressGames: [] });
  //     },
  //     createGame: (dto: CreateGameDto) => {
  //       socket?.emit("createGame", dto, (game: Game) => {
  //         set({ pendingGames: [...get().pendingGames, game] });
  //       });
  //     },
  //     joinGame: (gameId: string) => {
  //       socket?.emit("joinGame", gameId, (game: Game) => {
  //         set({
  //           pendingGames: get().pendingGames.filter((g) => g.id !== game.id),
  //           inProgressGames: [...get().inProgressGames, game],
  //         });
  //       });
  //     },
  //     makeMove: (makeMoveDto: MakeMoveDto) => {
  //       console.log("made move", makeMoveDto.gameId, makeMoveDto.square);
  //       socket?.emit("makeMove", makeMoveDto, (game: Game) => {
  //         set({
  //           inProgressGames: get().inProgressGames.map((g) =>
  //             g.id === game.id ? game : g
  //           ),
  //         });
  //       });
  //     },
  //   }),
  //   {
  //     name: "games-store",
  //     storage: createJSONStorage(() => localStorage),
  //   }
  // )
);



