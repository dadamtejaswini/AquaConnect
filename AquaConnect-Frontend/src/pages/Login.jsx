import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";

function Login() {
  const navigate = useNavigate();

  const [loginType, setLoginType] = useState("USER");

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
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      let response;

      const requestData = {
        email: loginData.email.trim(),
        password: loginData.password,
      };

      if (loginType === "DRIVER") {
        response = await api.post("/api/auth/driver-login", requestData);
      } else {
        response = await api.post("/api/auth/login", requestData);
      }

      localStorage.setItem("token", response.data.token);
      localStorage.setItem("role", response.data.role);

      if (response.data.role === "USER") {
        navigate("/");
      } else if (response.data.role === "OWNER") {
        navigate("/owner-dashboard");
      } else if (response.data.role === "DRIVER") {
        navigate("/driver-dashboard");
      }
    } catch (error) {
      console.log("LOGIN ERROR:", error);
      console.log("STATUS:", error.response?.status);
      console.log("BACKEND RESPONSE:", error.response?.data);

      setMessage(
        error.response?.data?.message ||
          error.response?.data ||
          "Invalid email/license number or password"
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
              onClick={() => setLoginType("USER")}
            >
              User
            </button>

            <button
              type="button"
              className={loginType === "OWNER" ? "active-tab" : ""}
              onClick={() => setLoginType("OWNER")}
            >
              Owner
            </button>

            <button
              type="button"
              className={loginType === "DRIVER" ? "active-tab" : ""}
              onClick={() => setLoginType("DRIVER")}
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

          <input
            type="password"
            name="password"
            placeholder="Enter Password"
            value={loginData.password}
            onChange={handleChange}
            required
          />

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