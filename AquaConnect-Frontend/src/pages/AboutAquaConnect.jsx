import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import {
  Droplets,
  Truck,
  ShieldCheck,
  MapPin,
  Star,
  Clock,
} from "lucide-react";

function AboutAquaConnect() {
  const navigate = useNavigate();

  return (
    <>
      <Navbar />

      <div className="about-page">
        <section className="about-hero">
          <h1>About AquaConnect</h1>
          <p>
            AquaConnect connects users with nearby verified water suppliers and
            provides a seamless tanker booking experience with live delivery
            tracking.
          </p>
        </section>

        <section className="mission-section">
          <h2>Our Mission</h2>

          <p>
            Water shortages affect thousands of families every day. AquaConnect
            helps people quickly find nearby water suppliers, compare options,
            book tankers, and track deliveries in real time.
          </p>
        </section>

        <section className="workflow-section">
          <h2>How AquaConnect Works</h2>

          <div className="workflow-grid">
            <div className="workflow-card">
              <MapPin size={30} />
              <h3>Find Nearby Reservoirs</h3>
            </div>

            <div className="workflow-card">
              <Droplets size={30} />
              <h3>Select Water Quantity</h3>
            </div>

            <div className="workflow-card">
              <Truck size={30} />
              <h3>Book Tanker</h3>
            </div>

            <div className="workflow-card">
              <Clock size={30} />
              <h3>Track Delivery</h3>
            </div>

            <div className="workflow-card">
              <Star size={30} />
              <h3>Rate Service</h3>
            </div>
          </div>
        </section>

        <section className="features-showcase">
          <h2>Why Choose AquaConnect?</h2>

          <div className="feature-grid-about">
            <div className="feature-about-card">
              <Truck />
              <h3>Live Tracking</h3>
              <p>Track your water tanker in real time.</p>
            </div>

            <div className="feature-about-card">
              <ShieldCheck />
              <h3>Verified Suppliers</h3>
              <p>Only trusted reservoir owners are listed.</p>
            </div>

            <div className="feature-about-card">
              <Droplets />
              <h3>Clean Water</h3>
              <p>Reliable and safe water supply.</p>
            </div>

            <div className="feature-about-card">
              <Star />
              <h3>Driver Ratings</h3>
              <p>See ratings before booking.</p>
            </div>
          </div>
        </section>

        <section className="review-section">
          <h2>Customer Reviews</h2>

          <div className="review-grid">
            <div className="review-card">
              <h3>⭐⭐⭐⭐⭐</h3>
              <p>
                Water arrived on time and the booking process was very easy.
              </p>
              <span>- Tejaswini</span>
            </div>

            <div className="review-card">
              <h3>⭐⭐⭐⭐</h3>
              <p>
                Great experience. Driver reached quickly and delivery was smooth.
              </p>
              <span>- Rahul</span>
            </div>

            <div className="review-card">
              <h3>⭐⭐⭐⭐⭐</h3>
              <p>
                Live tracking feature is very useful.
              </p>
              <span>- Priya</span>
            </div>
          </div>
        </section>

        <section className="cta-section">
          <h2>Need Water Today?</h2>

          <p>
            Find nearby reservoirs and book a tanker within minutes.
          </p>

          <button
            className="cta-btn"
            onClick={() => navigate("/nearby-reservoirs")}
          >
            Find Nearby Reservoirs
          </button>
        </section>
      </div>
    </>
  );
}

export default AboutAquaConnect;