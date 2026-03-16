// Footer.jsx
import React from "react";
import "../assets/styles.css";

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-container">

        {/* Brand Section */}
        <div className="footer-brand">
          <h2 className="footer-logo">NexusMart</h2>
          <p className="footer-desc">
            Your one-stop shop for fashion, electronics, and daily essentials.
          </p>
        </div>

        {/* Quick Links */}
        <div className="footer-section">
          <h4>Quick Links</h4>
          <a href="#">Home</a>
          <a href="#">Products</a>
          <a href="#">Categories</a>
          <a href="#">Offers</a>
        </div>

        {/* Support */}
        <div className="footer-section">
          <h4>Support</h4>
          <a href="#">About Us</a>
          <a href="#">Contact</a>
          <a href="#">Orders</a>
          <a href="#">Help Center</a>
        </div>

        {/* Legal */}
        <div className="footer-section">
          <h4>Legal</h4>
          <a href="#">Terms of Service</a>
          <a href="#">Privacy Policy</a>
          <a href="#">Refund Policy</a>
        </div>

      </div>

      {/* Bottom Bar */}
      <div className="footer-bottom">
        <p>© 2026 NexusMart (SalesSavvy). All rights reserved.</p>
      </div>
    </footer>
  );
}
