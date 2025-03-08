import React from "react";
import { motion } from "framer-motion"; // For smooth animation

export default function PromotionDialog({ onSelect, isWhite }) {
  const pieces = ["q", "r", "b", "n"]; // Queen, Rook, Bishop, Knight

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.8 }}
      animate={{ opacity: 1, scale: 1 }}
      className="absolute bg-gray-900 bg-opacity-80 p-2 rounded-lg shadow-lg grid grid-cols-2 gap-2"
    >
      {pieces.map((piece) => (
        <button
          key={piece}
          onClick={() => onSelect(piece)}
          className="p-3 rounded-md bg-white hover:bg-gray-300 transition"
        >
          <img
            src={`/pieces/${isWhite ? "w" : "b"}${piece}.png`} // White or Black piece
            alt={piece}
            className="w-10 h-10"
          />
        </button>
      ))}
    </motion.div>
  );
}
