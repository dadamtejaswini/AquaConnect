import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";

function ResetPassword() {
  const [formData, setFormData] = useState({
    role: "USER",
    identifier: "",
    lastFourDigits: "",
    newPassword: "",
    confirmPassword: "",
  });

  

  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleReset = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      const response = await api.put("/api/auth/reset-password", formData);
      setMessage(response.data);

      setFormData({
        role: "USER",
        identifier: "",
        lastFourDigits: "",
        newPassword: "",
        confirmPassword: "",
      });
    } catch (error) {
      setMessage(error.response?.data?.message || "Password reset failed");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-form-section">
        <form className="auth-card" onSubmit={handleReset}>
          <h2>Reset Password</h2>
          <p>Verify your account and create a new password.</p>

          <select name="role" value={formData.role} onChange={handleChange}>
            <option value="USER">User</option>
            <option value="OWNER">Owner</option>
            <option value="DRIVER">Driver</option>
          </select>

          <input
            type="text"
            name="identifier"
            placeholder={
              formData.role === "DRIVER"
                ? "Enter License Number"
                : "Enter Email"
            }
            value={formData.identifier}
            onChange={handleChange}
            required
          />

          <input
            type="text"
            name="lastFourDigits"
            placeholder="Enter Last 4 Digits of Phone Number"
            value={formData.lastFourDigits}
            onChange={handleChange}
            maxLength="4"
            required
          />

          <input
            type="password"
            name="newPassword"
            placeholder="Enter New Password"
            value={formData.newPassword}
            onChange={handleChange}
            required
          />

          <input
            type="password"
            name="confirmPassword"
            placeholder="Confirm New Password"
            value={formData.confirmPassword}
            onChange={handleChange}
            required
          />

          <button type="submit">Reset Password</button>

          {message && <p className="auth-message">{message}</p>}

          <div className="auth-link">
            Back to <Link to="/login">Login</Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ResetPassword;