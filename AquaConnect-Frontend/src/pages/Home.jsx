import { useNavigate } from "react-router-dom";
import logo from "../assets/logo.png";
import MapSection from "../components/MapSection";
import {
  Droplets,
  Truck,
  ShieldCheck,
  Headphones,
  CalendarCheck,
  CheckCircle,
} from "lucide-react";

function Home() {
  const navigate = useNavigate();

  return (
    <div className="app">
      <nav className="navbar">
        <div className="logo">
          <img src={logo} alt="AquaConnect Logo" />
          <span>AquaConnect</span>
        </div>

        <div className="nav-links">
          <a href="#home">Home</a>
          <a href="#about">About Us</a>
          <a href="#services">Services</a>
          <a href="#how">How It Works</a>
          <a href="#contact">Contact</a>
        </div>

        <div className="nav-buttons">
          <button className="login-btn" onClick={() => navigate("/login")}>
            Login
          </button>
          <button className="signup-btn" onClick={() => navigate("/register")}>
            Sign Up
          </button>
        </div>
      </nav>

      <section className="hero" id="home">
        <div className="hero-content">
          <h1>
            Clean Water. <br /> Delivered to You.
          </h1>
          <p>
            Book water tankers easily and get reliable water supply at your
            doorstep.
          </p>

          <div className="hero-buttons">
            <button className="primary-btn" onClick={() => navigate("/login")}>
              <Droplets size={20} />
              Book a Tanker
            </button>
          </div>
        </div>
      </section>

      <MapSection />

      <section className="features" id="services">
        <div className="feature-card">
          <Droplets className="feature-icon" />
          <h3>Clean & Safe Water</h3>
          <p>We provide clean and safe water through verified suppliers.</p>
        </div>

        <div className="feature-card">
          <Truck className="feature-icon" />
          <h3>On-Time Delivery</h3>
          <p>Book tankers and get water delivered to your location on time.</p>
        </div>

        <div className="feature-card">
          <ShieldCheck className="feature-icon" />
          <h3>Trusted & Verified</h3>
          <p>All owners, drivers, and vehicles are verified before service.</p>
        </div>

        <div className="feature-card">
          <Headphones className="feature-icon" />
          <h3>24/7 Support</h3>
          <p>Get support anytime for booking, delivery, or tracking issues.</p>
        </div>
      </section>

      <section className="about" id="about">
        <div className="about-image"></div>

        <div className="about-text">
          <h5>ABOUT US</h5>
          <h2>Your Trusted Water Delivery Partner</h2>
          <p>
            AquaConnect connects users with nearby verified water suppliers. It
            helps users book tankers, track delivery status, and get water
            supply during emergencies or daily needs.
          </p>
          <button className="learn-btn">Learn More</button>
        </div>
      </section>

      <section className="steps" id="how">
        <h5>HOW IT WORKS</h5>
        <h2>Simple Steps to Get Water</h2>

        <div className="step-container">
          <div className="step-card">
            <CalendarCheck />
            <h3>1. Register / Login</h3>
            <p>Create your account or login to get started.</p>
          </div>

          <div className="step-card">
            <Droplets />
            <h3>2. Book a Tanker</h3>
            <p>Choose location, water quantity, and tanker capacity.</p>
          </div>

          <div className="step-card">
            <Truck />
            <h3>3. We Deliver</h3>
            <p>Driver delivers water safely to your doorstep.</p>
          </div>

          <div className="step-card">
            <CheckCircle />
            <h3>4. Track & Manage</h3>
            <p>Track bookings and manage delivery updates easily.</p>
          </div>
        </div>
      </section>

      <footer className="footer" id="contact">
        <div>
          <h3>AquaConnect</h3>
          <p>Delivering clean water to every home.</p>
        </div>

        <div>
          <h4>Quick Links</h4>
          <p>Home</p>
          <p>About Us</p>
          <p>Services</p>
        </div>

        <div>
          <h4>Services</h4>
          <p>Water Tanker Booking</p>
          <p>Track Booking</p>
          <p>Bulk Orders</p>
        </div>

        <div>
          <h4>Contact Us</h4>
          <p>support@aquaconnect.com</p>
          <p>Bengaluru, India</p>
        </div>
      </footer>
    </div>
  );
}

export default Home;