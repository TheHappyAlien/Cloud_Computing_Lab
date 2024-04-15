interface SeparatorProps {
  text: string;
}

export default function Separator({ text }: SeparatorProps) {
  return (
    <div className="flex py-3 items-center">
      <div className="flex-grow border-t border-gray-400"></div>
      <span className="flex-shrink mx-4 text-gray-400">{text}</span>
      <div className="flex-grow border-t border-gray-400"></div>
    </div>
  );
}
