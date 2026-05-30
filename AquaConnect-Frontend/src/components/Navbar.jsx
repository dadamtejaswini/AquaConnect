import { Link, useNavigate } from "react-router-dom";
import logo from "../assets/logo.png";

function Navbar() {
  const navigate = useNavigate();

  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="logo" onClick={() => navigate("/")}>
        <img src={logo} alt="AquaConnect Logo" />
        <span>AquaConnect</span>
      </div>

      <div className="nav-links">
        <Link to="/">Home</Link>
        <a href="/#about">About Us</a>
        <a href="/#services">Services</a>
        <a href="/#how">How It Works</a>
        <Link to="/contact">Contact</Link>

        {token && role === "USER" && (
          <Link to="/bookings">Bookings</Link>
        )}

        {token && role === "OWNER" && (
          <Link to="/owner-dashboard">Owner Dashboard</Link>
        )}

        {token && role === "DRIVER" && (
          <Link to="/driver-dashboard">Driver Dashboard</Link>
        )}
      </div>

      <div className="nav-buttons">
        {!token ? (
          <>
            <button
              className="login-btn"
              onClick={() => navigate("/login")}
            >
              Login
            </button>

            <button
              className="signup-btn"
              onClick={() => navigate("/register")}
            >
              Sign Up
            </button>
          </>
        ) : (
          <button
            className="login-btn logout-btn"
            onClick={logout}
          >
            Logout
          </button>
        )}
      </div>
    </nav>
  );
}

export default Navbar;