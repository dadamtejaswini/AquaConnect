import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet-routing-machine";
import api from "../api/api";
import "../App.css";

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
      lineOptions: {
        styles: [{ color: "#4b00ff", weight: 6 }],
      },
    }).addTo(map);

    return () => {
      map.removeControl(routingControl);
    };
  }, [map, vehiclePos, deliveryPos]);

  return null;
}

function DriverDashboard() {
  const navigate = useNavigate();

  const [driver, setDriver] = useState(null);
  const [bookings, setBookings] = useState([]);
  const [success, setSuccess] = useState("");

  const fetchDriverData = async () => {
    try {
      const dashboardResponse = await api.get("/api/drivers/dashboard");
      const bookingsResponse = await api.get("/api/drivers/bookings");

      setDriver(dashboardResponse.data);
      setBookings(bookingsResponse.data);
    } catch (error) {
      console.log(error);
      alert("Failed to load driver dashboard");
    }
  };

  useEffect(() => {
    fetchDriverData();
  }, []);

  const showSuccess = (msg) => {
    setSuccess(msg);
    setTimeout(() => setSuccess(""), 3000);
  };

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const updateVehicleLocation = (vehicleId) => {
    if (!navigator.geolocation) {
      alert("Location not supported");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        try {
          await api.put("/api/drivers/vehicle-location", {
            vehicleId: vehicleId,
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          });

          showSuccess("Vehicle location updated");
          fetchDriverData();
        } catch (error) {
          alert(error.response?.data?.message || error.response?.data || "Failed");
        }
      },
      () => alert("Please allow location permission")
    );
  };

  const markOutForDelivery = async (bookingId) => {
    try {
      await api.put(`/api/drivers/bookings/${bookingId}/out-for-delivery`);
      showSuccess("Booking marked as out for delivery");
      fetchDriverData();
    } catch (error) {
      alert(error.response?.data?.message || error.response?.data || "Failed");
    }
  };

  const markDelivered = async (bookingId) => {
    try {
      await api.put(`/api/drivers/bookings/${bookingId}/delivered`);
      showSuccess("Booking marked as delivered");
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
            <h2>{driver.branchName || "-"}</h2>
          </div>

          <div className="driver-card">
            <h3>Assigned Vehicle</h3>
            <h2>{driver.vehicleNumber || "-"}</h2>
            <p>{driver.vehicleCapacity ? `${driver.vehicleCapacity} L` : ""}</p>
          </div>
        </div>

        <section className="driver-section">
          <h2>My Assigned Bookings</h2>

          {bookings.length === 0 ? (
            <p>No bookings assigned yet.</p>
          ) : (
            <div className="driver-booking-list">
              {bookings.map((booking) => {
                const vehiclePos =
                  booking.vehicleCurrentLatitude && booking.vehicleCurrentLongitude
                    ? [booking.vehicleCurrentLatitude, booking.vehicleCurrentLongitude]
                    : null;

                const deliveryPos =
                  booking.deliveryLatitude && booking.deliveryLongitude
                    ? [booking.deliveryLatitude, booking.deliveryLongitude]
                    : null;

                return (
                  <div className="driver-booking-card" key={booking.bookingId}>
                    <div className="driver-booking-details">
                      <h3>Booking #{booking.bookingId}</h3>

                      <p><strong>Customer:</strong> {booking.userName}</p>
                      <p><strong>User Phone:</strong> {booking.userPhone || "Phone number not available"}</p>
                      <p><strong>Address:</strong> {booking.deliveryAddress}</p>
                      <p><strong>Quantity:</strong> {booking.quantity} L</p>
                      <p><strong>Vehicle:</strong> {booking.vehicleNumber || "-"}</p>
                      <p><strong>Distance:</strong> {booking.distanceKm ? `${booking.distanceKm} km` : "Update vehicle location"}</p>

                      <p>
                        <strong>Status:</strong>{" "}
                        <span className={getStatusClass(booking.status)}>
                          {booking.status}
                        </span>
                      </p>

                      <div className="driver-actions">
                        {booking.vehicleId && (
                          <button onClick={() => updateVehicleLocation(booking.vehicleId)}>
                            Update Vehicle Location
                          </button>
                        )}

                        {booking.status === "DRIVER_ASSIGNED" && (
                          <button onClick={() => markOutForDelivery(booking.bookingId)}>
                            Out for Delivery
                          </button>
                        )}

                        {booking.status === "OUT_FOR_DELIVERY" && (
                          <button onClick={() => markDelivered(booking.bookingId)}>
                            Mark Delivered
                          </button>
                        )}
                      </div>
                    </div>

                    {deliveryPos && (
                      <div className="driver-map-box">
                        <MapContainer
                          center={vehiclePos || deliveryPos}
                          zoom={14}
                          style={{ height: "360px", width: "100%", borderRadius: "18px" }}
                        >
                          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

                          {vehiclePos && (
                            <Marker position={vehiclePos}>
                              <Popup>Vehicle Location</Popup>
                            </Marker>
                          )}

                          <Marker position={deliveryPos}>
                            <Popup>Delivery Location</Popup>
                          </Marker>

                          {vehiclePos && deliveryPos && (
                            <RoutingMap vehiclePos={vehiclePos} deliveryPos={deliveryPos} />
                          )}
                        </MapContainer>
                      </div>
                    )}
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