import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../assets/admin.css";

export default function AdminLogin() {

  const navigate = useNavigate();
  const API = "http://localhost:8080";

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");

  const [step, setStep] = useState("login");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  // ===== LOGIN (SEND OTP) =====
  const login = async (e) => {

    e.preventDefault();
    setError(null);

    if (!username || !password) {
      setError("Username & password required");
      return;
    }

    try {

      setLoading(true);

      const res = await fetch(`${API}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ username, password })
      });

      const data = await res.json();

      if (!res.ok) {
        setError(data.error || "Invalid credentials");
        return;
      }

      setStep("otp");

    } catch {
      setError("Request failed");
    } finally {
      setLoading(false);
    }
  };

  // ===== VERIFY LOGIN OTP =====
  const verifyOtp = async (e) => {

    e.preventDefault();
    setError(null);

    if (!otp) {
      setError("OTP required");
      return;
    }

    try {

      setLoading(true);

      const res = await fetch(
        `${API}/api/auth/verify?otpvalue=${otp}`,
        { credentials: "include" }
      );

      const data = await res.json();

      if (!res.ok) {
        setError(data.error || "Invalid OTP");
        return;
      }

      if (data.role !== "ADMIN") {
        setError("Not an admin account");
        return;
      }

      navigate("/admindashboard");

    } catch {
      setError("OTP verification failed");
    } finally {
      setLoading(false);
    }
  };

  // ===== SEND FORGOT PASSWORD OTP =====
  const sendForgotOtp = async (e) => {

    e.preventDefault();
    setError(null);

    if (!username) {
      setError("Username required");
      return;
    }

    try {

      setLoading(true);

      const res = await fetch(
        `${API}/api/auth/forgot-password?username=${username}`,
        { method: "POST" }
      );

      const data = await res.json();

      if (!res.ok) {
        setError(data.error || "Failed to send OTP");
        return;
      }

      alert("OTP sent to registered email");

      setStep("forgotOtpVerify");

    } catch {
      setError("Request failed");
    } finally {
      setLoading(false);
    }
  };

  // ===== VERIFY FORGOT OTP =====
  const verifyForgotOtp = async (e) => {

    e.preventDefault();
    setError(null);

    if (!username || !otp) {
      setError("Username & OTP required");
      return;
    }

    try {

      setLoading(true);

      const res = await fetch(
        `${API}/api/auth/forgot-password/verify-otp?username=${username}&otpvalue=${otp}`
      );

      const data = await res.json();

      if (!res.ok) {
        setError(data.error || "Invalid OTP");
        return;
      }

      setStep("forgotReset");

    } catch {
      setError("Request failed");
    } finally {
      setLoading(false);
    }
  };

  // ===== RESET PASSWORD =====
  const resetPassword = async (e) => {

    e.preventDefault();
    setError(null);

    if (!username || !newPassword) {
      setError("Username & new password required");
      return;
    }

    try {

      setLoading(true);

      const res = await fetch(
        `${API}/api/auth/forgot-password/reset?username=${username}&newPassword=${newPassword}`,
        { method: "POST" }
      );

      const data = await res.json();

      if (!res.ok) {
        setError(data.error || "Reset failed");
        return;
      }

      alert("Password reset successful");

      setStep("login");
      setUsername("");
      setPassword("");
      setOtp("");
      setNewPassword("");

    } catch {
      setError("Request failed");
    } finally {
      setLoading(false);
    }
  };

  return (

    <div className="page-layout">

      <div className="page-container1">

        <div className="form-container">

          <div className="brand-section">
            <h1 className="brand-name">NexusMart</h1>
            <p className="brand-tag">Admin Portal</p>
          </div>

          <div className="form-content">

            <h1 className="form-title">Admin Login</h1>

            {error && <p className="error-message">{error}</p>}

            {/* LOGIN */}
            {step === "login" && (
              <form onSubmit={login}>

                <input
                  className="form-input"
                  placeholder="Admin username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                />

                <input
                  type="password"
                  className="form-input"
                  placeholder="Admin password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />

                <button className="form-button">
                  {loading ? "Sending OTP..." : "Send OTP"}
                </button>

                <p
                  style={{
                    marginTop: "10px",
                    cursor: "pointer",
                    color: "#c02c57"
                  }}
                  onClick={() => setStep("forgotOtp")}
                >
                  Forgot Password?
                </p>
              <p style={{ marginTop: "20px", fontSize: "14px" }}> If not admin?{" "}
            <a
              href="http://localhost:5173/"
             style={{ color: "#c02c57", fontWeight: "600", textDecoration: "none" }}
            >
             Go to User Login Page
            </a>
            </p>

              </form>
            )}

            {/* LOGIN OTP */}
            {step === "otp" && (
              <form onSubmit={verifyOtp}>

                <input
                  className="form-input"
                  placeholder="Enter OTP"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                />

                <button className="form-button">
                  {loading ? "Verifying..." : "Verify OTP"}
                </button>

              </form>
            )}

            {/* SEND FORGOT OTP */}
            {step === "forgotOtp" && (
              <form onSubmit={sendForgotOtp}>

                <input
                  className="form-input"
                  placeholder="Username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                />

                <button className="form-button">
                  {loading ? "Sending..." : "Send OTP"}
                </button>

              </form>
            )}

            {/* VERIFY FORGOT OTP */}
            {step === "forgotOtpVerify" && (
              <form onSubmit={verifyForgotOtp}>

                <input
                  className="form-input"
                  placeholder="Enter OTP"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                />

                <button className="form-button">
                  {loading ? "Verifying..." : "Verify OTP"}
                </button>

              </form>
            )}

            {/* RESET PASSWORD */}
            {step === "forgotReset" && (
              <form onSubmit={resetPassword}>

                <input
                  type="password"
                  className="form-input"
                  placeholder="New Password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                />
                

                <button className="form-button">
                  {loading ? "Resetting..." : "Reset Password"}
                </button>

              </form>
            )}

          </div>

        </div>

      </div>

    </div>
  );
}