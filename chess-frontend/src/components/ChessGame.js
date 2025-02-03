import { useState, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import axios from "axios";

export default function ChessGame() {
  const [position, setPosition] = useState("start");

  useEffect(() => {
    fetchBoardState();
  }, []);

  const fetchBoardState = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/board");
      setPosition(response.data); // FEN string from backend
    } catch (error) {
      console.error("Error fetching board state:", error);
    }
  };

  const onDrop = async ({ sourceSquare, targetSquare }) => {
    try {
      const response = await axios.post("http://localhost:8080/api/move", {
        from: sourceSquare,
        to: targetSquare,
      });
      setPosition(response.data); // Update board after valid move
    } catch (error) {
      console.error("Invalid move:", error);
    }
  };

  const resetBoard = async () => {
    try {
      await axios.post("http://localhost:8080/api/reset");
      fetchBoardState();
    } catch (error) {
      console.error("Error resetting board:", error);
    }
  };

  return (
    <div className="flex flex-col items-center">
      <h1 className="text-2xl font-bold mb-4">Online Chess Game</h1>
      {/* Adjust board size here */}
      <Chessboard position={position} onPieceDrop={onDrop} boardWidth={400} />
      <button onClick={resetBoard} className="mt-4 p-2 bg-blue-500 text-white rounded">
        Reset Board
      </button>
    </div>
  );
}
