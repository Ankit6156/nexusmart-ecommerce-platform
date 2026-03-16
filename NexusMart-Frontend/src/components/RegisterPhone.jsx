import PhoneFrame from "./PhoneFrame";
import InputWithToggle from "./InputWithToggle";

const RegisterPhone = ({
  active,
  form,
  show,
  setShow,
  onChange,
  onRegister,
  loading,
  message
}) => (
  <PhoneFrame active={active}>
    <h3>Create Account</h3>

    <InputWithToggle
      label="Username"
      name="username"
      value={form.username}
      onChange={onChange}
      show={show}
      setShow={setShow}
    />

    <InputWithToggle
      label="Email"
      name="email"
      value={form.email}
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

    <select name="role" value={form.role} onChange={onChange}>
      <option value="">Select role</option>
      <option value="CUSTOMER">Customer</option>
      <option value="ADMIN">Admin</option>
    </select>

    <button onClick={onRegister} disabled={loading}>
      {loading ? "Registering..." : "Register"}
    </button>

    {/* ✅ MESSAGE INSIDE PHONE FRAME */}
    {message?.text && (
      <div className={`message ${message.type}`}>
        {message.text}
      </div>
    )}
  </PhoneFrame>
);

export default RegisterPhone;
