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
  const [gameHistory, setGameHistory] = useState(["start"]); // Store all FEN states
  const [currentHistoryIndex, setCurrentHistoryIndex] = useState(0); // Current displayed move

  useEffect(() => {
    fetchBoardState();
  }, []);

  const fetchBoardState = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/board");
      setPosition(response.data);
      setChess(new Chess(response.data));
      setGameHistory([response.data]); // Initialize history with starting position
      setCurrentHistoryIndex(0);
    } catch (error) {
      console.error("Error fetching board state:", error);
    }
  };

  const getValidMoves = (square) => {
    return chess.moves({ square, verbose: true }).map((move) => move.to);
  };

  const onSquareClick = (square) => {
    if (gameOver) return;
    console.log("ONsquareClick");
  
    // Block moves if not in the latest position
    if (currentHistoryIndex !== gameHistory.length - 1) {
      alert("You must return to the latest position to make a move!");
      return;
    }
  
    if (selectedSquare) {
      if (validMoves.includes(square)) {
        console.log("selectedSquare", selectedSquare);
        console.log("square", square);
  
        // Check if the move is a promotion move
        const piece = chess.get(selectedSquare);
       
  
        // If not a promotion, proceed with the move
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
        console.log("Promotion move:********", move);
        return;
      }
      move.promotion = promotion.toLowerCase();
      console.log("Promotion move: dandan", move);
    }

    const validMove = chess.move(move);
    if (!validMove) {
      console.log("Illegal move:", move);
      return;
    }

    try {
      const moveNotation = `${move.from}${move.to}${move.promotion || ""}`;
      const response = await axios.post("http://localhost:8080/api/move", { moveNotation });

      // Check if the game is over
      if (response.data.includes("CHECKMATE") || response.data.includes("DRAW")) {
        setGameOver(true);

        // First, update the board to reflect the final position
        setPosition(chess.fen());
        setChess(new Chess(chess.fen()));

        // Then, display the alert after a short delay to ensure the UI updates first
        setTimeout(() => alert(response.data), 100);
        return;
      }

      // If the game is not over, update the board normally
      setPosition(response.data);
      setChess(new Chess(response.data));
      setSelectedSquare(null);
      setValidMoves([]);

      // Update history with new position
      setGameHistory((prev) => [...prev, response.data]);
      setCurrentHistoryIndex((prev) => prev + 1);
    } catch (error) {
      console.error("Invalid move:", error);
    }
  };


  const handlePromotionSelection = (piece) => {
    if (!promotionMove || !promotionMove.from || !promotionMove.to) return false; // Return false if invalid

    const promotionLetter = piece.toLowerCase().charAt(1);
    handleMove(promotionMove.from, promotionMove.to, promotionLetter);
    setPromotionMove(null);

    console.log("handlePromotionSelection", piece);
    return true; // Return true if the move was successful
};

  const onDrop = async ({ sourceSquare, targetSquare }) => {
    if (!sourceSquare || !targetSquare) {
      console.error("Invalid source or target square:", sourceSquare, targetSquare);
      return;
    }
    if (gameOver) return false;
  
    // Block moves if user is not in the latest position
    if (currentHistoryIndex !== gameHistory.length - 1) {
      alert("You must return to the latest position to make a move!");
      return false;
    }
  
    const validSquares = getValidMoves(sourceSquare);
    if (!validSquares.includes(targetSquare)) return false;
    const piece = chess.get(sourceSquare);
    console.log("piece",piece);
    console.log(targetSquare[1]);

    if (piece.type === "p" && (targetSquare[1] === "8" || targetSquare[1] === "1")) {
      console.log("promondrop",piece);
      setPromotionMove((prev) => ({
        from: prev?.from === sourceSquare ? prev.from : sourceSquare,
        to: prev?.to === targetSquare ? prev.to : targetSquare,
      }));
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
  const undoMove = async () => {
    try {
      const response = await axios.post("http://localhost:8080/api/undo");
      setPosition(response.data);
      setChess(new Chess(response.data));
  
      // Update history state
      setGameHistory((prev) => prev.slice(0, -1));
      setCurrentHistoryIndex((prev) => Math.max(0, prev - 1));
    } catch (error) {
      console.error("Error undoing move:", error);
    }
  };
  const goBack = () => {
    if (currentHistoryIndex > 0) {
      setCurrentHistoryIndex(currentHistoryIndex - 1);
      setPosition(gameHistory[currentHistoryIndex - 1]);
    }
  };

  const goForward = () => {
    if (currentHistoryIndex < gameHistory.length - 1) {
      setCurrentHistoryIndex(currentHistoryIndex + 1);
      setPosition(gameHistory[currentHistoryIndex + 1]);
    }
  };

  return (
    <div className="flex flex-col items-center relative">
      <h1 className="text-2xl font-bold mb-4">Online Chess Game</h1>
      <Chessboard
        position={position}
        showPromotionDialog={true}
        onPieceDrop={(sourceSquare, targetSquare) => onDrop({ sourceSquare, targetSquare })}
        onSquareClick={onSquareClick}
        boardWidth={400}
        customSquareStyles={Object.fromEntries(validMoves.map((sq) => [sq, { backgroundColor: "rgba(255, 255, 0, 0.5)" }]))}
        //allowDrag={({ piece, sourceSquare }) => getValidMoves(sourceSquare).length > 0}
        promotionToSquare={promotionMove ? promotionMove.to : undefined}
        onPromotionPieceSelect={handlePromotionSelection}
      />
      <div className="mt-4 flex gap-4">
        <button onClick={goBack} className="p-2 bg-gray-500 text-white rounded">←</button>
        <button onClick={resetBoard} className="p-2 bg-blue-500 text-white rounded">Reset Board</button>
        <button onClick={goForward} className="p-2 bg-gray-500 text-white rounded">→</button>
        <button onClick={undoMove} className="p-2 bg-red-500 text-white rounded">
  Undo Move
</button>
      </div>
    </div>
  );
}
