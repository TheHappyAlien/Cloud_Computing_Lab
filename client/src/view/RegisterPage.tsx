import { useNavigate } from "react-router-dom";
import { useState } from "react";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../components/shadcn/card";
import { Input } from "../components/shadcn/input";
import { Button } from "../components/shadcn/button";
import { register } from "../utils/cognito";

export default function RegisterPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirmation, setPasswordConfirmation] = useState("");
  const [error, setError] = useState<string>("");
  const navigate = useNavigate();

  function onSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (email === "") {
      setError("Email cannot be empty");
      return;
    }

    if (password === "") {
      setError("Password cannot be empty");
      return;
    }

    if (password != passwordConfirmation) {
      setError("Password confirmation does not match password");
      return;
    }
    
    console.log("registring")
    register(email, password).then(() => {
      alert("User registered successfully");
      navigate("/login");
    });
  }

  return (
    <div className="flex justify-center pt-4">
      <Card className="w-1/4">
        <CardHeader>
          <CardTitle>Register</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={onSubmit} className="space-y-5">
            <Input
              placeholder="example@email.com"
              value={email}
              type="email"
              onChange={(e) => setEmail(e.target.value)}
            />
            <Input
              placeholder="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <Input
              placeholder="confirm password"
              type="password"
              value={passwordConfirmation}
              onChange={(e) => setPasswordConfirmation(e.target.value)}
            />
            <p>{error}</p>
            <Button type="submit" className="w-full">
              Confirm
            </Button>
          </form>
          <Button className="mt-2 w-full" onClick={() => navigate("/login")}>
            Login
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}