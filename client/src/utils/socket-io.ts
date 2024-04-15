import { io } from "socket.io-client";




export const socket = io( "",{
    path: "/ws",
    autoConnect: false,
    transports: ["websocket"],
});

export const createSocket = (userId: string) => {
    socket.auth = { userId };
    return socket;
}