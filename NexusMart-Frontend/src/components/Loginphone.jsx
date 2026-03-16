import PhoneFrame from "./PhoneFrame";
import InputWithToggle from "./InputWithToggle";

const LoginPhone = ({
  active,
  form,
  show,
  setShow,
  onChange,
  onLogin,
  onForgot,
  loading,
  message
}) => (
  <PhoneFrame active={active}>
    <h3>Welcome Back</h3>

    <InputWithToggle
      label="Username"
      name="username"
      value={form.username}
      onChange={onChange}
      show={show}
      setShow={setShow}
    />

    <InputWithToggle
      label="Password"
      name="password"
      value={form.password}
      onChange={onChange}
      show={show}
      setShow={setShow}
      defaultHidden
    />

    <button onClick={onLogin} disabled={loading}>
      {loading ? "Sending OTP..." : " User Login"}
    </button>

    <button className="link" onClick={onForgot} disabled={loading}>
      Forgot password?
    </button>

    {/* ✅ MESSAGE INSIDE PHONE FRAME */}
    {message?.text && (
      <div className={`message ${message.type}`}>
        {message.text}
      </div>
    )}
  </PhoneFrame>
);

export default LoginPhone;
