interface userProps {
  user: string | null;
}

export default function WelcomeText({ user }: userProps) {
  return (
    <>
      <h1 className="text-2xl font-bold text-gray-800 text-center ">
        Welcome {user}
      </h1>
      <h2 className="text-xl font-bold text-gray-800 mb-4 text-center"></h2>
    </>
  );
}
