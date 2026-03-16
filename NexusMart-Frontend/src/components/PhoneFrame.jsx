const PhoneFrame = ({ children, active }) => {
  return (
    <div className={`phone ${active ? "active" : ""}`}>
      <div className="screen">{children}</div>
    </div>
  );
};

export default PhoneFrame;
