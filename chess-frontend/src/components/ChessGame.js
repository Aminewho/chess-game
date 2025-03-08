import { useState, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import axios from "axios";
import { Chess } from "chess.js";

export default function ChessGame() {
  const [chess, setChess] = useState(new Chess());
  const [position, setPosition] = useState("start");
  const [gameOver, setGameOver] = useState(false);
  const [selectedSquare, setSelectedSquare] = useState(null);
  const [validMoves, setValidMoves] = useState([]);
  const [promotionMove, setPromotionMove] = useState(null);

  useEffect(() => {
    fetchBoardState();
  }, []);

  const fetchBoardState = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/board");
      setPosition(response.data);
      setChess(new Chess(response.data));
    } catch (error) {
      console.error("Error fetching board state:", error);
    }
  };

  const getValidMoves = (square) => {
    return chess.moves({ square, verbose: true }).map((move) => move.to);
  };
  
  const onSquareClick = (square) => {
    if (gameOver) return;
  
    if (selectedSquare) {
      if (validMoves.includes(square)) {
        const piece = chess.get(selectedSquare);
        
        // Check if pawn is promoting
        if (piece?.type === "p" && (square[1] === "8" || square[1] === "1")) {
          setPromotionMove({ from: selectedSquare, to: square });
          setSelectedSquare(null);
          setValidMoves([]);
          return; // Stop further execution until promotion is chosen
        }
  
        // Normal move
        handleMove(selectedSquare, square);
      }
      setSelectedSquare(null);
      setValidMoves([]);
    } else {
      const moves = getValidMoves(square);
      if (moves.length > 0) {
        setSelectedSquare(square);
        setValidMoves(moves);
      }
    }
  };
  
  const handleMove = async (sourceSquare, targetSquare, promotion = null) => {
    const piece = chess.get(sourceSquare);
    if (!piece) return;

    let move = { from: sourceSquare, to: targetSquare };

    if (piece.type === "p" && (targetSquare[1] === "8" || targetSquare[1] === "1")) {
      if (!promotion) {
        setPromotionMove({ from: sourceSquare, to: targetSquare });
        return; 
      }
      move.promotion = promotion.toLowerCase();
    }

    const validMove = chess.move(move);
    if (!validMove) {
      console.log("Illegal move:", move);
      return;
    }

    try {
      const moveNotation = `${move.from}${move.to}${move.promotion || ""}`;
      const response = await axios.post("http://localhost:8080/api/move", { moveNotation });

      if (response.data.includes("CHECKMATE") || response.data.includes("DRAW")) {
        alert(response.data);
        setGameOver(true);
        return;
      }

      setPosition(response.data);
      setChess(new Chess(response.data));
      setSelectedSquare(null);
      setValidMoves([]);
    } catch (error) {
      console.error("Invalid move:", error);
    }
  };

  const handlePromotionSelection = (piece) => {
    if (!promotionMove) return;
  
    const promotionLetter = piece.toLowerCase().charAt(1);
  
    handleMove(promotionMove.from, promotionMove.to, promotionLetter);
    setPromotionMove(null);
  };
  
  const onDrop = async ({ sourceSquare, targetSquare }) => {
    if (gameOver) return false;

    const validSquares = getValidMoves(sourceSquare);
    if (!validSquares.includes(targetSquare)) return false;

    const piece = chess.get(sourceSquare);
    if (piece.type === "p" && (targetSquare[1] === "8" || targetSquare[1] === "1")) {
      setPromotionMove({ from: sourceSquare, to: targetSquare });
      return false;
    }

    await handleMove(sourceSquare, targetSquare);
    return true;
  };

  const resetBoard = async () => {
    try {
      await axios.post("http://localhost:8080/api/reset");
      fetchBoardState();
      setGameOver(false);
      setSelectedSquare(null);
      setValidMoves([]);
    } catch (error) {
      console.error("Error resetting board:", error);
    }
  };

  return (
    <div className="flex flex-col items-center relative">
      <h1 className="text-2xl font-bold mb-4">Online Chess Game</h1>
      <Chessboard
        position={position}
        onPieceDrop={(sourceSquare, targetSquare) => onDrop({ sourceSquare, targetSquare })}
        onSquareClick={onSquareClick}
        boardWidth={400}
        customSquareStyles={Object.fromEntries(validMoves.map((sq) => [sq, { backgroundColor: "rgba(255, 255, 0, 0.5)" }]))}
        allowDrag={({ piece, sourceSquare }) => getValidMoves(sourceSquare).length > 0}
        promotionToSquare={promotionMove ? promotionMove.to : undefined}  
        onPromotionPieceSelect={handlePromotionSelection} 
      />
      <button onClick={resetBoard} className="mt-4 p-2 bg-blue-500 text-white rounded">
        Reset Board
      </button>
    </div>
  );
}
