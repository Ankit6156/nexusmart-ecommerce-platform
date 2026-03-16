import { useState, useEffect } from "react";

const InputWithToggle = ({
  label,
  name,
  value,
  onChange,
  defaultHidden = false
}) => {
  const [hidden, setHidden] = useState(defaultHidden);

  useEffect(() => {
    setHidden(defaultHidden);
  }, [defaultHidden]);

  return (
    <div className="input-group">
      <input
        type={hidden ? "password" : "text"}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={label}
        autoComplete="off"
      />

      {/* 👁️ Toggle for EVERY FIELD */}
      <span
        className="toggle-icon"
        onClick={() => setHidden((prev) => !prev)}
      >
        {hidden ? "👁️" : "🙈"}
      </span>
    </div>
  );
};

export default InputWithToggle;
