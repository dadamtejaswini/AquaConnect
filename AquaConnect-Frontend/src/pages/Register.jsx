import { Link } from "react-router-dom";

function Register() {
  return (
    <div className="auth-container">
      <div className="auth-form-section">
        <div className="auth-card">
          <h2>Create Account</h2>
          <p>Join AquaConnect and start booking water tankers.</p>

          <input type="text" placeholder="Full Name" />
          <input type="email" placeholder="Email" />
          <input type="text" placeholder="Phone Number" />
          <input type="password" placeholder="Password" />

          <button>Register</button>

          <div className="auth-link">
            Already have an account? <Link to="/login">Login</Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;