import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";

function Login() {
  const navigate = useNavigate();

  const [loginType, setLoginType] = useState("USER");
  const [showPassword, setShowPassword] = useState(false);

  const [loginData, setLoginData] = useState({
    email: "",
    password: "",
  });

  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    setLoginData({
      ...loginData,
      [e.target.name]: e.target.value,
    });

    setMessage("");
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      const requestData = {
        email: loginData.email.trim(),
        password: loginData.password,
      };

      let response;

      if (loginType === "DRIVER") {
        response = await api.post("/api/auth/driver-login", requestData);
      } else {
        response = await api.post("/api/auth/login", requestData);
      }

      const token = response.data.token;
      const role = response.data.role;

      localStorage.setItem("token", token);
      localStorage.setItem("role", role);

      if (role === "USER") {
        navigate("/");
      } else if (role === "OWNER") {
        navigate("/owner-dashboard");
      } else if (role === "DRIVER") {
        navigate("/driver-dashboard");
      } else {
        navigate("/");
      }
    } catch (error) {
      console.log("LOGIN ERROR:", error);

      setMessage(
        error.response?.data?.message ||
          error.response?.data?.error ||
          (typeof error.response?.data === "string"
            ? error.response.data
            : "Invalid email/license number or password")
      );
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-form-section">
        <form className="auth-card" onSubmit={handleLogin}>
          <h2>Welcome Back</h2>
          <p>Login to continue with AquaConnect.</p>

          <div className="login-tabs">
            <button
              type="button"
              className={loginType === "USER" ? "active-tab" : ""}
              onClick={() => {
                setLoginType("USER");
                setLoginData({ email: "", password: "" });
                setMessage("");
              }}
            >
              User
            </button>

            <button
              type="button"
              className={loginType === "OWNER" ? "active-tab" : ""}
              onClick={() => {
                setLoginType("OWNER");
                setLoginData({ email: "", password: "" });
                setMessage("");
              }}
            >
              Owner
            </button>

            <button
              type="button"
              className={loginType === "DRIVER" ? "active-tab" : ""}
              onClick={() => {
                setLoginType("DRIVER");
                setLoginData({ email: "", password: "" });
                setMessage("");
              }}
            >
              Driver
            </button>
          </div>

          <input
            type="text"
            name="email"
            placeholder={
              loginType === "DRIVER"
                ? "Enter Driver License Number"
                : "Enter Email"
            }
            value={loginData.email}
            onChange={handleChange}
            required
          />

          <div className="password-field">
            <input
              type={showPassword ? "text" : "password"}
              name="password"
              placeholder="Enter Password"
              value={loginData.password}
              onChange={handleChange}
              required
            />

            <button
              type="button"
              className="show-password-btn"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? "Hide" : "Show"}
            </button>
          </div>

          <button type="submit">Login as {loginType}</button>

          {message && <p className="auth-message">{message}</p>}

          <div className="auth-link">
            Don't have an account? <Link to="/register">Sign Up</Link>
          </div>

          <div className="auth-link">
            Forgot password? <Link to="/reset-password">Reset Password</Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Login;