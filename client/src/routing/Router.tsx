import { Navigate, createBrowserRouter } from "react-router-dom";
import LoginPage from "../view/LoginPage";
import GamePage from "../view/GamePage";
import RegisterPage from "../view/RegisterPage";

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
    path: "/register",
    element: <RegisterPage />,
  },
  {
    path: "*",
    element: <Navigate to="/login" />,
  },
]);