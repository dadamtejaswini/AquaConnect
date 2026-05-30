import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import { QRCodeCanvas } from "qrcode.react";
import L from "leaflet";
import "leaflet-routing-machine";
import "leaflet-routing-machine/dist/leaflet-routing-machine.css";
import api from "../api/api";
import "../App.css";

const tankerIcon = L.divIcon({
  html: `<div class="tanker-marker">🚚</div>`,
  className: "custom-tanker-wrapper",
  iconSize: [48, 48],
  iconAnchor: [24, 24],
});

const userIcon = L.divIcon({
  html: `<div class="user-location-marker">📍</div>`,
  className: "custom-home-wrapper",
  iconSize: [50, 50],
  iconAnchor: [25, 50],
});

function RoutingMap({ vehiclePos, deliveryPos }) {
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

function DriverDashboard() {
  const navigate = useNavigate();

  const [driver, setDriver] = useState(null);
  const [bookings, setBookings] = useState([]);
  const [success, setSuccess] = useState("");
  const [progress, setProgress] = useState(0.05);
  const [showQrFor, setShowQrFor] = useState(null);

  const fetchDriverData = async () => {
    try {
      const dashboardResponse = await api.get("/api/drivers/dashboard");
      const bookingsResponse = await api.get("/api/drivers/bookings");

      setDriver(dashboardResponse.data);
      setBookings(bookingsResponse.data || []);
    } catch (error) {
      console.log(error);
      alert("Failed to load driver dashboard");
    }
  };

  useEffect(() => {
    fetchDriverData();

    const interval = setInterval(() => {
      setProgress((prev) => (prev >= 0.95 ? 0.95 : prev + 0.008));
    }, 1800);

    return () => clearInterval(interval);
  }, []);

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const showSuccessMessage = (msg) => {
    setSuccess(msg);
    setTimeout(() => setSuccess(""), 3000);
  };

  const markOutForDelivery = async (bookingId) => {
    try {
      await api.put(`/api/drivers/bookings/${bookingId}/out-for-delivery`);
      setProgress(0.05);
      showSuccessMessage("Booking marked as out for delivery");
      fetchDriverData();
    } catch (error) {
      alert(error.response?.data?.message || error.response?.data || "Failed");
    }
  };

  const markDelivered = async (booking) => {
    try {
      await api.put(`/api/drivers/bookings/${booking.bookingId}/delivered`);

      setShowQrFor(booking.bookingId);

      setTimeout(() => {
        setShowQrFor(null);
      }, 180000);

      showSuccessMessage("Booking delivered. QR will disappear after 3 minutes.");
      fetchDriverData();
    } catch (error) {
      alert(error.response?.data?.message || error.response?.data || "Failed");
    }
  };

  const getStatusClass = (status) => {
    if (status === "DELIVERED") return "status-badge delivered";
    if (status === "OUT_FOR_DELIVERY") return "status-badge out";
    if (status === "DRIVER_ASSIGNED") return "status-badge assigned";
    return "status-badge pending";
  };

  if (!driver) {
    return <h2 className="dashboard-loading">Loading driver dashboard...</h2>;
  }

  return (
    <div className="driver-layout">
      <aside className="driver-sidebar">
        <h2>AquaConnect</h2>

        <button className="driver-active">Dashboard</button>

        <button onClick={() => navigate("/")}>Home Page</button>

        <button onClick={logout}>Logout</button>
      </aside>

      <main className="driver-main">
        <div className="driver-header">
          <h1>Welcome, {driver.driverName}</h1>
          <p>Manage your assigned water deliveries</p>
        </div>

        {success && <div className="success-box">{success}</div>}

        <div className="driver-cards">
          <div className="driver-card">
            <h3>Driver Rating</h3>
            <h2>{driver.rating || 0} ⭐</h2>
          </div>

          <div className="driver-card">
            <h3>Status</h3>
            <h2>{driver.status}</h2>
          </div>

          <div className="driver-card">
            <h3>Branch</h3>
            <h2 className="small-branch-name">{driver.branchName || "-"}</h2>
          </div>
        </div>

        <section className="driver-section">
          <h2>My Assigned Bookings</h2>

          {bookings.length === 0 ? (
            <p>No bookings assigned yet.</p>
          ) : (
            <div className="driver-booking-list">
              {bookings.map((booking) => {
                const deliveryPos =
                  booking.deliveryLatitude && booking.deliveryLongitude
                    ? [
                        Number(booking.deliveryLatitude),
                        Number(booking.deliveryLongitude),
                      ]
                    : [12.9421, 77.5678];

                const branchPos = [
                  Number(booking.branchLatitude || booking.latitude || 12.9345),
                  Number(booking.branchLongitude || booking.longitude || 77.5347),
                ];

                const start =
                  booking.vehicleCurrentLatitude &&
                  booking.vehicleCurrentLongitude
                    ? [
                        Number(booking.vehicleCurrentLatitude),
                        Number(booking.vehicleCurrentLongitude),
                      ]
                    : branchPos;

                const vehiclePos =
                  booking.status === "DELIVERED"
                    ? branchPos
                    : [
                        start[0] + (deliveryPos[0] - start[0]) * progress,
                        start[1] + (deliveryPos[1] - start[1]) * progress,
                      ];

                const eta =
                  booking.status === "DELIVERED"
                    ? 0
                    : Math.max(0, 18 - Math.floor(progress * 18));

                const distanceLeft =
                  booking.status === "DELIVERED"
                    ? "0.0"
                    : Math.max(0, 4.2 - progress * 4.2).toFixed(1);

                const reachedLocation =
                  progress >= 0.95 || booking.status === "DELIVERED";

                const bankNumber =
                  booking.ownerBankAccountNumber ||
                  booking.bankAccountNumber ||
                  booking.ownerBankNumber ||
                  "Bank account not available";

                return (
                  <div className="driver-booking-card" key={booking.bookingId}>
                    <div className="driver-booking-details">
                      <span className="live-pill">
                        {booking.status === "DELIVERED"
                          ? "DELIVERED - VEHICLE RETURNED"
                          : "LIVE DELIVERY"}
                      </span>

                      <h3>Booking #{booking.bookingId}</h3>

                      <p><strong>Customer:</strong> {booking.userName}</p>
                      <p><strong>Phone:</strong> {booking.userPhone || "-"}</p>
                      <p><strong>Quantity:</strong> {booking.quantity} L</p>
                      <p><strong>Total Price:</strong> ₹{booking.totalPrice || "-"}</p>
                      <p><strong>Address:</strong> {booking.deliveryAddress}</p>
                      <p><strong>Vehicle:</strong> {booking.vehicleNumber || "-"}</p>

                      <p>
                        <strong>Status:</strong>{" "}
                        <span className={getStatusClass(booking.status)}>
                          {booking.status}
                        </span>
                      </p>

                      <div className="tracking-stats driver-stats">
                        <div>
                          <h3>{eta} min</h3>
                          <span>Estimated</span>
                        </div>

                        <div>
                          <h3>{distanceLeft} km</h3>
                          <span>Distance left</span>
                        </div>
                      </div>

                      <div className="driver-actions">
                        {booking.status === "DRIVER_ASSIGNED" && (
                          <button
                            onClick={() =>
                              markOutForDelivery(booking.bookingId)
                            }
                          >
                            Start Delivery
                          </button>
                        )}

                        {booking.status === "OUT_FOR_DELIVERY" &&
                          reachedLocation && (
                            <button
                              className="delivered-btn"
                              onClick={() => markDelivered(booking)}
                            >
                              Mark Delivered
                            </button>
                          )}

                        {booking.status === "OUT_FOR_DELIVERY" &&
                          !reachedLocation && (
                            <button disabled>
                              Reaching Soon...
                            </button>
                          )}
                      </div>

                      {showQrFor === booking.bookingId && (
                        <div className="payment-qr-box">
                          <h3>Payment QR</h3>

                          <p>Owner Bank Account: {bankNumber}</p>

                          <QRCodeCanvas
                            value={`AquaConnect Payment | Bank Account: ${bankNumber} | Booking: ${booking.bookingId} | Amount: ${booking.totalPrice || ""}`}
                            size={180}
                          />

                          <p className="qr-note">
                            QR will disappear automatically after 3 minutes.
                          </p>
                        </div>
                      )}
                    </div>

                    <div className="driver-map-box live-driver-map">
                      <MapContainer
                        center={vehiclePos}
                        zoom={14}
                        style={{
                          height: "100%",
                          width: "100%",
                          borderRadius: "18px",
                        }}
                      >
                        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

                        <Marker position={vehiclePos} icon={tankerIcon}>
                          <Popup>
                            {booking.status === "DELIVERED"
                              ? "Vehicle returned to reservoir"
                              : "Water tanker location"}
                          </Popup>
                        </Marker>

                        <Marker position={deliveryPos} icon={userIcon}>
                          <Popup>User delivery location</Popup>
                        </Marker>

                        <RoutingMap
                          vehiclePos={vehiclePos}
                          deliveryPos={deliveryPos}
                        />
                      </MapContainer>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default DriverDashboard;