import { useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import WelcomePhone from "../components/WelcomePhone";
import LoginPhone from "../components/LoginPhone";
import RegisterPhone from "../components/RegisterPhone";
import OverlayPhone from "../components/OverlayPhone";
import "../assets/Landing.css";
const API = "http://localhost:8080";

const Landing = () => {
  const navigate = useNavigate();
  /* ================= FLOW ================= */
  const [active, setActive] = useState("welcome");
  const [overlay, setOverlay] = useState(null);
  const [loading, setLoading] = useState(false);

  /* ================= FORMS ================= */
  const [loginForm, setLoginForm] = useState({ username: "", password: "" });
  const [registerForm, setRegisterForm] = useState({
    username: "",
    email: "",
    password: "",
    role: ""
  });
  const [forgotForm, setForgotForm] = useState({ username: "" });
  const [otpForm, setOtpForm] = useState({
    username: "",
    otp: "",
    newPassword: ""
  });

  /* ================= MESSAGES ================= */
  const [loginMessage, setLoginMessage] = useState({ type: "", text: "" });
  const [registerMessage, setRegisterMessage] = useState({ type: "", text: "" });
  const [overlayMessage, setOverlayMessage] = useState({ type: "", text: "" });

  /* ================= MESSAGE TIMER ================= */
  const messageTimer = useRef(null);

  const showTempMessage = (setFn, message, delay = 3000) => {
    if (messageTimer.current) {
      clearTimeout(messageTimer.current);
    }

    setFn(message);

    messageTimer.current = setTimeout(() => {
      setFn({ type: "", text: "" });
    }, delay);
  };

  /* ================= CLEAR LOGIN/REGISTER ON SWITCH ================= */
  useEffect(() => {
    setLoginMessage({ type: "", text: "" });
    setRegisterMessage({ type: "", text: "" });
  }, [active]);

  /* ================= CLEAR OVERLAY MESSAGE WHEN CLOSED ================= */
  useEffect(() => {
    if (!overlay) {
      setOverlayMessage({ type: "", text: "" });
    }
  }, [overlay]);

  /* ================= HANDLERS ================= */
  const onLoginChange = (e) =>
    setLoginForm((p) => ({ ...p, [e.target.name]: e.target.value }));

  const onRegisterChange = (e) =>
    setRegisterForm((p) => ({ ...p, [e.target.name]: e.target.value }));

  const onForgotChange = (e) =>
    setForgotForm({ username: e.target.value });

  const onOtpChange = (e) =>
    setOtpForm((p) => ({ ...p, [e.target.name]: e.target.value }));

  /* ================= REGISTER ================= */
  const register = async () => {
    if (active !== "register") return;

    if (!registerForm.username || !registerForm.email || !registerForm.password || !registerForm.role) {
      showTempMessage(setRegisterMessage, {
        type: "error",
        text: "All fields are required"
      });
      return;
    }

    try {
      setLoading(true);

      const res = await fetch(`${API}/api/users/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(registerForm)
      });

      const data = await res.json();

      if (!res.ok) {
        showTempMessage(setRegisterMessage, {
          type: "error",
          text: data.message || "Registration failed"
        });
        return;
      }

      showTempMessage(setRegisterMessage, {
        type: "success",
        text: data.message || "Registered successfully"
      });

      setTimeout(() => {
        setRegisterForm({ username: "", email: "", password: "", role: "" });
        setActive("login");
      }, 1200);

    } catch {
      showTempMessage(setRegisterMessage, {
        type: "error",
        text: "Request failed"
      });
    } finally {
      setLoading(false);
    }
  };

  /* ================= LOGIN ================= */
  const login = async () => {
    if (active !== "login") return;

    if (!loginForm.username || !loginForm.password) {
      showTempMessage(setLoginMessage, {
        type: "error",
        text: "Username & password required"
      });
      return;
    }

    try {
      setLoading(true);

      const res = await fetch(`${API}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",   // ✅ ADD THIS
        body: JSON.stringify(loginForm)
      });

      const data = await res.json();

      if (!res.ok) {
        showTempMessage(setLoginMessage, {
          type: "error",
          text: data.message || "Invalid credentials"
        });
        return;
      }

      showTempMessage(setOverlayMessage, {
        type: "success",
        text: data.message || "OTP sent successfully"
      });

      setOverlay("loginOtp");

    } catch {
      showTempMessage(setLoginMessage, {
        type: "error",
        text: "Request failed"
      });
    } finally {
      setLoading(false);
    }
  };

  /* ================= LOGIN OTP ================= */
const verifyLoginOtp = async () => {

  if (!otpForm.otp) {
    showTempMessage(setOverlayMessage, {
      type: "error",
      text: "OTP is required"
    });
    return;
  }

  try {
    setLoading(true);

    const res = await fetch(
      `${API}/api/auth/verify?otpvalue=${otpForm.otp}`,
      {
        credentials: "include"
      }
    );

    const data = await res.json();

    if (!res.ok) {
      showTempMessage(setOverlayMessage, {
        type: "error",
        text: data.error || "Invalid OTP"
      });
      return;
    }

    // 🚫 BLOCK ADMIN LOGIN
    if (data.role === "ADMIN") {
  showTempMessage(setOverlayMessage, {
    type: "error",
    text: "Admin cannot login here. Redirecting to Admin Login..."
  });

  setTimeout(() => {
    navigate("/admin-login");
  }, 1500);

  return;
}

    // ✅ SAVE USER
    localStorage.setItem("username", data.username);
    localStorage.setItem("role", data.role);

    // ✅ SUCCESS MESSAGE
    showTempMessage(setOverlayMessage, {
      type: "success",
      text: "Login successful"
    });

    setOverlay(null);

    // ✅ NAVIGATE CUSTOMER
    setTimeout(() => {
      navigate("/customerhome");
    }, 100);

  } catch {
    showTempMessage(setOverlayMessage, {
      type: "error",
      text: "Request failed"
    });
  } finally {
    setLoading(false);
  }
};

  /* ================= FORGOT PASSWORD ================= */
  const sendForgotOtp = async () => {
    if (!forgotForm.username) {
      showTempMessage(setOverlayMessage, {
        type: "error",
        text: "Username is required"
      });
      return;
    }

    try {
      setLoading(true);

      const res = await fetch(
        `${API}/api/auth/forgot-password?username=${forgotForm.username}`,
        { method: "POST" }
      );

      const data = await res.json();

      if (!res.ok) {
        showTempMessage(setOverlayMessage, {
          type: "error",
          text: data.message || "Invalid username"
        });
        return;
      }

      showTempMessage(setOverlayMessage, {
        type: "success",
        text: "OTP sent successfully"
      });

      setOtpForm((p) => ({ ...p, username: forgotForm.username }));
      setOverlay("forgotOtp");

    } catch {
      showTempMessage(setOverlayMessage, {
        type: "error",
        text: "Request failed"
      });
    } finally {
      setLoading(false);
    }
  };

const verifyForgotOtp = async () => {
  if (!otpForm.username || !otpForm.otp) {
    showTempMessage(setOverlayMessage, {
      type: "error",
      text: "Username & OTP required"
    });
    return;
  }

  try {
    setLoading(true);

    const res = await fetch(
      `${API}/api/auth/forgot-password/verify-otp?username=${otpForm.username}&otpvalue=${otpForm.otp}`
    );

    const data = await res.json();

    if (!res.ok) {
      showTempMessage(setOverlayMessage, {
        type: "error",
        text: data.message || "Invalid OTP"
      });
      return;
    }

    // ✅ SHOW SUCCESS MESSAGE FIRST
    showTempMessage(setOverlayMessage, {
      type: "success",
      text: "OTP verified successfully. You can reset your password."
    });

    // ✅ MOVE TO RESET PASSWORD FRAME AFTER MESSAGE
    setTimeout(() => {
      setOverlay("forgotReset");
    }, 1500);

  } catch {
    showTempMessage(setOverlayMessage, {
      type: "error",
      text: "Request failed. Please try again."
    });
  } finally {
    setLoading(false);
  }
};

  const resetForgotPassword = async () => {
    if (!otpForm.username || !otpForm.newPassword) {
      showTempMessage(setOverlayMessage, {
        type: "error",
        text: "Username & new password required"
      });
      return;
    }

    try {
      setLoading(true);

      const res = await fetch(
        `${API}/api/auth/forgot-password/reset?username=${otpForm.username}&newPassword=${otpForm.newPassword}`,
        { method: "POST" }
      );

      const data = await res.json();

      if (!res.ok) {
        showTempMessage(setOverlayMessage, {
          type: "error",
          text: data.message || "Reset failed"
        });
        return;
      }

      showTempMessage(setOverlayMessage, {
        type: "success",
        text: "Password reset successful"
      });

      setTimeout(() => {
        setOverlay(null);
        setForgotForm({ username: "" });
        setOtpForm({ username: "", otp: "", newPassword: "" });
        setActive("login");
      }, 1200);

    } catch {
      showTempMessage(setOverlayMessage, {
        type: "error",
        text: "Request failed"
      });
    } finally {
      setLoading(false);
    }
  };

  /* ================= UI ================= */
  return (
    <div className="landing">
    <div className="left">
    <h1>NexusMart</h1>
    <h3>Modern E-Commerce Platform</h3>
    <p>🛒 Seamless Shopping Experience for Customers | ⚡ Fast & Reliable</p>
    <p>🔐 Secure Authentication with OTP Verification & JWT Authorization</p>
    </div>

      {!overlay && (
        <div className="phones">
          <WelcomePhone
            active={active === "welcome"}
            onLogin={() => setActive("login")}
            onRegister={() => setActive("register")}
          />

          <LoginPhone
            active={active === "login"}
            form={loginForm}
            onChange={onLoginChange}
            onLogin={login}
            onForgot={() => setOverlay("forgotUsername")}
            loading={loading}
            message={loginMessage}
          />

          <RegisterPhone
            active={active === "register"}
            form={registerForm}
            onChange={onRegisterChange}
            onRegister={register}
            loading={loading}
            message={registerMessage}
          />
        </div>
      )}

      {overlay && (
        <div className="phones single">
          <OverlayPhone
            title={
              overlay === "loginOtp"
                ? "Verify OTP"
                : overlay === "forgotUsername"
                ? "Forgot Password"
                : overlay === "forgotOtp"
                ? "Verify OTP"
                : "Reset Password"
            }
            inputFields={
              overlay === "loginOtp"
                ? [{ name: "otp", label: "OTP", hidden: true }]
                : overlay === "forgotUsername"
                ? [{ name: "username", label: "Username" }]
                : overlay === "forgotOtp"
                ? [
                    { name: "username", label: "Username" },
                    { name: "otp", label: "OTP", hidden: true }
                  ]
                : [
                    { name: "username", label: "Username" },
                    { name: "newPassword", label: "New Password", hidden: true }
                  ]
            }
            form={overlay === "forgotUsername" ? forgotForm : otpForm}
            onChange={overlay === "forgotUsername" ? onForgotChange : onOtpChange}
            onSubmit={
              overlay === "loginOtp"
                ? verifyLoginOtp
                : overlay === "forgotUsername"
                ? sendForgotOtp
                : overlay === "forgotOtp"
                ? verifyForgotOtp
                : resetForgotPassword
            }
            onClose={() => setOverlay(null)}
            loading={loading}
            message={overlayMessage}
          />
        </div>
      )}
    </div>
  );
};

export default Landing;
