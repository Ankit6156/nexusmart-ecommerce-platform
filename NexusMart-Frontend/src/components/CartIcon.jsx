import React from "react";
import { useNavigate } from "react-router-dom";
import "../assets/styles.css";

export function CartIcon({ count }) {
  const navigate = useNavigate();

  const handleCartClick = () => {
    navigate("/UserCartPage");
  };

  return (
    <div className="cart-icon" onClick={handleCartClick}>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="white"          // ✅ force visible color
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        className="cart-icon-svg"
      >
        <path d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13l-1.5 6H19M7 13l1.5 6M10 21a1 1 0 100-2 1 1 0 000 2zm8 0a1 1 0 100-2 1 1 0 000 2z" />
      </svg>

      {count > 0 && <span className="cart-badge">{count}</span>}
    </div>
  );
}
