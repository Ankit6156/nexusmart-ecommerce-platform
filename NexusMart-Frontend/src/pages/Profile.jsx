import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../assets/profile.css";
import useravatar from "../assets/useravatar.png";
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function Profile() {

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetch("http://localhost:8080/api/users/profile", {
      method: "GET",
      credentials: "include"
    })
      .then(res => res.json())
      .then(data => {
        setUser(data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <>
        <Header username="" cartCount={0} />

        <div className="profile-container">
          <div className="profile-card loading-card">
            Loading profile...
          </div>
        </div>

        <Footer />
      </>
    );
  }

  return (
    <>
      {/* ✅ Header */}
      <Header username={user.username} cartCount={0} />

      {/* ✅ Profile Content */}
      <div className="profile-container">

        <div className="profile-card">

          {/* Back button */}
          <button
            className="back-button"
            onClick={() => navigate("/customerhome")}
          >
            ← Back to Home
          </button>

          <img
            src={useravatar}
            alt="avatar"
            className="profile-avatar"
          />

          <h2 className="profile-name">{user.username}</h2>

          <p className="profile-role-badge">
            {user.role}
          </p>

          <div className="profile-info">

            <div className="profile-row">
              <span>User ID</span>
              <span>{user.userId}</span>
            </div>

            <div className="profile-row">
              <span>Email</span>
              <span>{user.email}</span>
            </div>

            <div className="profile-row">
              <span>Username</span>
              <span>{user.username}</span>
            </div>

            <div className="profile-row">
              <span>Role</span>
              <span>{user.role}</span>
            </div>

          </div>

        </div>

      </div>

      {/* ✅ Footer */}
      <Footer />
    </>
  );
}
