import { useEffect, useMemo, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet-routing-machine";
import "leaflet-routing-machine/dist/leaflet-routing-machine.css";
import Navbar from "../components/Navbar";
import api from "../api/api";
import "../App.css";

const tankerIcon = L.divIcon({
  html: `<div class="tanker-marker">🚚</div>`,
  className: "custom-tanker-wrapper",
  iconSize: [48, 48],
  iconAnchor: [24, 24],
});

const homeIcon = L.divIcon({
  html: `<div class="user-location-marker">📍</div>`,
  className: "custom-home-wrapper",
  iconSize: [50, 50],
  iconAnchor: [25, 50],
  popupAnchor: [0, -45],
});

function RouteMap({ vehiclePos, deliveryPos }) {
  const map = useMap();

  useEffect(() => {
    if (!vehiclePos || !deliveryPos) return;

    const routingControl = L.Routing.control({
      waypoints: [
        L.latLng(vehiclePos[0], vehiclePos[1]),
        L.latLng(deliveryPos[0], deliveryPos[1]),
      ],
      routeWhileDragging: false,
      addWaypoints: false,
      draggableWaypoints: false,
      fitSelectedRoutes: true,
      show: false,
      createMarker: () => null,
      lineOptions: {
        styles: [{ color: "#4b00ff", weight: 7 }],
      },
    }).addTo(map);

    return () => map.removeControl(routingControl);
  }, [map, vehiclePos, deliveryPos]);

  return null;
}

function UserBookings() {
  const [bookings, setBookings] = useState([]);
  const [progress, setProgress] = useState(0.05);
  const [loading, setLoading] = useState(true);

  const [feedbackBooking, setFeedbackBooking] = useState(null);
  const [rating, setRating] = useState(5);
  const [messageText, setMessageText] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    fetchBookings();

    const interval = setInterval(() => {
      setProgress((prev) => (prev >= 0.92 ? 0.92 : prev + 0.008));
    }, 1800);

    return () => clearInterval(interval);
  }, []);

  const fetchBookings = async () => {
    try {
      const response = await api.get("/api/user/bookings");
      setBookings(response.data || []);
    } catch (error) {
      console.log("USER BOOKINGS ERROR:", error);
      setBookings([]);
    } finally {
      setLoading(false);
    }
  };

  const openFeedback = (booking) => {
    setFeedbackBooking(booking);
    setRating(5);
    setMessageText("");
    setSuccessMessage("");
  };

  const closeFeedback = () => {
    setFeedbackBooking(null);
    setRating(5);
    setMessageText("");
  };

  const submitFeedback = async (e) => {
    e.preventDefault();

    if (!feedbackBooking) return;

    try {
      await api.post("/api/user/feedbacks", {
        bookingId: feedbackBooking.bookingId,
        rating: Number(rating),
        message: messageText,
      });

      setSuccessMessage("Feedback submitted successfully");
      closeFeedback();
      fetchBookings();
    } catch (error) {
      console.log("FEEDBACK ERROR:", error);

      alert(
        error.response?.data?.message ||
          error.response?.data?.error ||
          (typeof error.response?.data === "string"
            ? error.response.data
            : JSON.stringify(error.response?.data)) ||
          "Failed to submit feedback"
      );
    }
  };

  const currentBooking = useMemo(() => {
    return bookings.find(
      (b) => b.status === "DRIVER_ASSIGNED" || b.status === "OUT_FOR_DELIVERY"
    );
  }, [bookings]);

  const hasRealCoords =
    currentBooking?.deliveryLatitude &&
    currentBooking?.deliveryLongitude &&
    currentBooking?.vehicleCurrentLatitude &&
    currentBooking?.vehicleCurrentLongitude;

  const start = hasRealCoords
    ? [
        Number(currentBooking.vehicleCurrentLatitude),
        Number(currentBooking.vehicleCurrentLongitude),
      ]
    : [
        Number(currentBooking?.branchLatitude || currentBooking?.latitude || 12.9345),
        Number(currentBooking?.branchLongitude || currentBooking?.longitude || 77.5347),
      ];

  const deliveryPos =
    currentBooking?.deliveryLatitude && currentBooking?.deliveryLongitude
      ? [
          Number(currentBooking.deliveryLatitude),
          Number(currentBooking.deliveryLongitude),
        ]
      : [12.9421, 77.5678];

  const vehiclePos = hasRealCoords
    ? start
    : [
        start[0] + (deliveryPos[0] - start[0]) * progress,
        start[1] + (deliveryPos[1] - start[1]) * progress,
      ];

  const eta = Math.max(2, 18 - Math.floor(progress * 15));
  const distanceLeft = Math.max(0.3, 4.2 - progress * 3.6).toFixed(1);

  return (
    <>
      <Navbar />

      <div className="bookings-page">
        <div className="bookings-header">
          <h1>My Bookings</h1>
          <p>Track your current and past water tanker bookings.</p>
        </div>

        {successMessage && <div className="success-box">{successMessage}</div>}

        {loading ? (
          <div className="empty-booking">
            <h3>Loading bookings...</h3>
          </div>
        ) : (
          <>
            {currentBooking && (
              <section className="live-tracking-card">
                <div className="tracking-info">
                  <span className="live-pill">LIVE TRACKING</span>
                  <h2>Current Delivery</h2>

                  <p><b>Booking ID:</b> #{currentBooking.bookingId}</p>
                  <p><b>Driver:</b> {currentBooking.driverName || "Driver assigned soon"}</p>
                  <p><b>Driver Phone:</b> {currentBooking.driverPhone || "Not available"}</p>
                  <p><b>Vehicle:</b> {currentBooking.vehicleNumber || "Vehicle assigned soon"}</p>
                  <p><b>Status:</b> {currentBooking.status}</p>
                  <p><b>Delivery Address:</b> {currentBooking.deliveryAddress}</p>

                  <div className="tracking-stats">
                    <div>
                      <h3>{eta} min</h3>
                      <span>Estimated</span>
                    </div>
                    <div>
                      <h3>{distanceLeft} km</h3>
                      <span>Distance left</span>
                    </div>
                  </div>
                </div>

                <div className="tracking-map">
                  <MapContainer
                    center={vehiclePos}
                    zoom={14}
                    style={{ height: "100%", width: "100%" }}
                  >
                    <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

                    <Marker position={vehiclePos} icon={tankerIcon}>
                      <Popup>Water tanker is on the way</Popup>
                    </Marker>

                    <Marker position={deliveryPos} icon={homeIcon}>
                      <Popup>User delivery location</Popup>
                    </Marker>

                    <RouteMap vehiclePos={vehiclePos} deliveryPos={deliveryPos} />
                  </MapContainer>
                </div>
              </section>
            )}

            <section className="booking-list">
              <h2>All Bookings</h2>

              {bookings.length === 0 ? (
                <div className="empty-booking">
                  <h3>No bookings yet</h3>
                  <p>Book a water tanker from the home page.</p>
                </div>
              ) : (
                bookings.map((booking) => (
                  <div className="booking-card" key={booking.bookingId}>
                    <div>
                      <h3>Booking #{booking.bookingId}</h3>
                      <p><b>Branch:</b> {booking.branchName}</p>
                      <p><b>Quantity:</b> {booking.quantity} L</p>
                      <p><b>Total Price:</b> ₹{booking.totalPrice}</p>
                      <p><b>Address:</b> {booking.deliveryAddress}</p>
                      <p><b>Driver:</b> {booking.driverName || "-"}</p>
                      <p><b>Vehicle:</b> {booking.vehicleNumber || "-"}</p>
                    </div>

                    <div className="booking-card-right">
                      <span className="booking-status">{booking.status}</span>

                      {booking.status === "DELIVERED" && !booking.feedbackGiven && (
                        <button
                          className="feedback-btn"
                          onClick={() => openFeedback(booking)}
                        >
                          Give Feedback
                        </button>
                      )}

                      {booking.status === "DELIVERED" && booking.feedbackGiven && (
                        <p className="feedback-done">Feedback given</p>
                      )}
                    </div>
                  </div>
                ))
              )}
            </section>
          </>
        )}
      </div>

      {feedbackBooking && (
        <div className="feedback-modal-overlay">
          <div className="feedback-modal">
            <h2>Give Feedback</h2>
            <p>Booking #{feedbackBooking.bookingId}</p>

            <form onSubmit={submitFeedback}>
              <label>Rating</label>
              <select value={rating} onChange={(e) => setRating(e.target.value)}>
                <option value="5">5 - Excellent</option>
                <option value="4">4 - Good</option>
                <option value="3">3 - Average</option>
                <option value="2">2 - Poor</option>
                <option value="1">1 - Bad</option>
              </select>

              <label>Message</label>
              <textarea
                placeholder="Write your feedback..."
                value={messageText}
                onChange={(e) => setMessageText(e.target.value)}
                required
              />

              <div className="feedback-modal-actions">
                <button type="button" onClick={closeFeedback}>
                  Cancel
                </button>
                <button type="submit">
                  Submit Feedback
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
}

export default UserBookings;