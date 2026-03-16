import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import useravatar from "../assets/useravatar.png";
import "../assets/styles.css";

export default function ProfileDropdown({ username }) {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();

  const toggleDropdown = () => {
    setIsOpen((prev) => !prev);
  };

  const handleLogout = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });

      if (response.ok) {
        setIsOpen(false);
        navigate("/");
      }
    } catch (error) {
      console.error("Logout failed", error);
    }
  };

  const handleOrdersClick = () => {
    setIsOpen(false);
    navigate("/orders");
  };

  return (
    <div className="profile-dropdown">
      <button className="profile-button" onClick={toggleDropdown}>
        <img src={useravatar} alt="User Avatar" className="user-avatar" />
        <span className="username">{username || "Guest"}</span>
      </button>

      {isOpen && (
        <div className="dropdown-menu">
          <button onClick={() => {setIsOpen(false); navigate("/profile");}}> Profile </button>
          <button onClick={handleOrdersClick}>Orders</button>
          <button onClick={handleLogout}>Logout</button>
        </div>
      )}
    </div>
  );
}
