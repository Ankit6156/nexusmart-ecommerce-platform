import PhoneFrame from "./PhoneFrame";
import { useNavigate } from "react-router-dom";

const WelcomePhone = ({ active, onLogin, onRegister }) => {
  const navigate = useNavigate();

  return (
    <PhoneFrame active={active}>
      <div className="welcome-screen">

        <div className="welcome-hero">
          <h3>Welcome</h3>
          <p className="subtitle">
            Let’s get you started with NexusMart E-Commerace Platform
          </p>
        </div>

        <div className="welcome-actions">
          <button onClick={onLogin}>User Login</button>

          <button className="outline" onClick={onRegister}>
            Register
          </button>

          {/* ✅ ADMIN LOGIN → WEB PAGE */}
          <button
            className="admin-btn"
            onClick={() => navigate("/admin-login")}
          >
            Admin Login
          </button>
        </div>

      </div>
    </PhoneFrame>
  );
};

export default WelcomePhone;
