import { Button } from "../components/shadcn/button";
import { useNavigate } from "react-router-dom";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../components/shadcn/card";
import { Input } from "../components/shadcn/input";
import { useUserStore } from "../stores/user-store";
import { useState } from "react";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const { setUserName } = useUserStore();
  const navigate = useNavigate();

  function onSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setUserName(username);
    navigate("/game");
  }

  return (
    <div className="flex justify-center pt-4">
      <Card className="w-1/5">
        <CardHeader>
          <CardTitle>Login Page</CardTitle>
          <CardDescription>Please enter your name to login</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={onSubmit} className="space-y-5">
            <Input
              placeholder="Your Name"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
            <Button type="submit" className="w-full">
              Login
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}