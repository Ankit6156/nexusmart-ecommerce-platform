import PhoneFrame from "./PhoneFrame";
import InputWithToggle from "./InputWithToggle";

const OverlayPhone = ({
  title,
  inputFields,
  form,
  onChange,
  onSubmit,
  onClose,
  loading,
  message
}) => (
  <div className="overlay">
    <PhoneFrame active>
      <h3>{title}</h3>

      {/* ✅ INPUTS */}
      {inputFields.map((field) => (
        <InputWithToggle
          key={field.name}
          label={field.label}
          name={field.name}
          value={form[field.name] || ""}
          onChange={onChange}
          defaultHidden={field.hidden}
        />
      ))}

      {/* ✅ IMPORTANT: type="button" */}
      <button
        type="button"
        onClick={onSubmit}
        disabled={loading}
      >
        {loading ? "Please wait..." : "Submit"}
      </button>

      <button
        type="button"
        className="outline"
        onClick={onClose}
        disabled={loading}
      >
        Cancel
      </button>

      {/* ✅ MESSAGE AT BOTTOM (NO CLICK BLOCKING) */}
      {message?.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}
    </PhoneFrame>
  </div>
);

export default OverlayPhone;
