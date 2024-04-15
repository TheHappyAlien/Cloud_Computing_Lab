import { Navigate, createBrowserRouter } from "react-router-dom";
import LoginPage from "../view/LoginPage";
import GamePage from "../view/GamePage";

export const router = createBrowserRouter([
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/game",
    element: <GamePage />,
  },
  {
    path: "*",
    element: <Navigate to="/login" />,
  },
]);