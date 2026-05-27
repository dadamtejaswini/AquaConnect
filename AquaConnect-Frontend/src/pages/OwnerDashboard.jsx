import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import "../App.css";

function OwnerDashboard() {
  const navigate = useNavigate();

  const [activeSection, setActiveSection] = useState("dashboard");
  const [dashboard, setDashboard] = useState(null);
  const [message, setMessage] = useState("");

  const fetchDashboard = async () => {
    try {
      const response = await api.get("/api/owners/dashboard");
      setDashboard(response.data);
    } catch (error) {
      console.log(error);
      setMessage("Failed to load owner dashboard");
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  if (message) {
    return <h2 className="dashboard-error">{message}</h2>;
  }

  if (!dashboard) {
    return <h2 className="dashboard-loading">Loading dashboard...</h2>;
  }

  return (
    <div className="owner-layout">
      <aside className="owner-sidebar">
        <h2>AquaConnect</h2>

        <button
          className={activeSection === "dashboard" ? "active-side" : ""}
          onClick={() => setActiveSection("dashboard")}
        >
          Dashboard
        </button>

        <button
          className={activeSection === "profile" ? "active-side" : ""}
          onClick={() => setActiveSection("profile")}
        >
          Profile
        </button>

        <button
          className={activeSection === "branches" ? "active-side" : ""}
          onClick={() => setActiveSection("branches")}
        >
          Branches
        </button>

        <button
          className={activeSection === "vehicles" ? "active-side" : ""}
          onClick={() => setActiveSection("vehicles")}
        >
          Vehicles
        </button>

        <button
          className={activeSection === "drivers" ? "active-side" : ""}
          onClick={() => setActiveSection("drivers")}
        >
          Drivers
        </button>

        <button
          className={activeSection === "bookings" ? "active-side" : ""}
          onClick={() => setActiveSection("bookings")}
        >
          Bookings
        </button>

        <button className="logout-btn" onClick={logout}>
          Logout
        </button>
      </aside>

      <main className="owner-main">
        <div className="owner-header">
          <div>
            <h1>Welcome, {dashboard.ownerName}</h1>
            <p>Owner Dashboard</p>
          </div>
        </div>

        {activeSection === "dashboard" && (
          <>
            <div className="owner-cards">
              <div className="owner-card">
                <h3>Total Branches</h3>
                <h2>{dashboard.totalBranches}</h2>
              </div>

              <div className="owner-card">
                <h3>Total Vehicles</h3>
                <h2>{dashboard.totalVehicles}</h2>
              </div>

              <div className="owner-card">
                <h3>Total Drivers</h3>
                <h2>{dashboard.totalDrivers}</h2>
              </div>

              <div className="owner-card">
                <h3>Total Bookings</h3>
                <h2>{dashboard.totalBookings}</h2>
              </div>

              <div className="owner-card">
                <h3>Total Water Available</h3>
                <h2>{dashboard.totalWaterAvailable} L</h2>
              </div>
            </div>

            <section className="owner-section">
              <h2>All Branches Overview</h2>

              <div className="branch-grid">
                {dashboard.branches.map((branch) => (
                  <div className="branch-card" key={branch.branchId}>
                    <h3>{branch.branchName}</h3>
                    <p>
                      <strong>Location:</strong> {branch.location}
                    </p>
                    <p>
                      <strong>Water Quantity:</strong>{" "}
                      {branch.currentWaterQuantity} L
                    </p>
                    <p>
                      <strong>Drivers:</strong> {branch.drivers.length}
                    </p>
                    <p>
                      <strong>Vehicles:</strong> {branch.vehicles.length}
                    </p>
                    <p>
                      <strong>Bookings:</strong> {branch.bookings.length}
                    </p>
                    <p>
                      <strong>Status:</strong>{" "}
                      <span
                        className={
                          branch.active ? "status-active" : "status-inactive"
                        }
                      >
                        {branch.active ? "Active" : "Inactive"}
                      </span>
                    </p>
                  </div>
                ))}
              </div>
            </section>
          </>
        )}

        {activeSection === "profile" && (
          <section className="owner-section">
            <h2>Owner Profile</h2>

            <div className="profile-grid">
              <p>
                <strong>Name:</strong> {dashboard.ownerName}
              </p>
              <p>
                <strong>Email:</strong> {dashboard.email}
              </p>
              <p>
                <strong>Phone:</strong> {dashboard.phoneNumber}
              </p>
              <p>
                <strong>Total Branches:</strong> {dashboard.totalBranches}
              </p>
            </div>
          </section>
        )}

        {activeSection === "branches" && (
          <section className="owner-section">
            <h2>My Branches / Reservoirs</h2>

            <div className="branch-grid">
              {dashboard.branches.map((branch) => (
                <div className="branch-card" key={branch.branchId}>
                  <h3>{branch.branchName}</h3>
                  <p>
                    <strong>Location:</strong> {branch.location}
                  </p>
                  <p>
                    <strong>Water Quantity:</strong>{" "}
                    {branch.currentWaterQuantity} L
                  </p>
                  <p>
                    <strong>Status:</strong>{" "}
                    <span
                      className={
                        branch.active ? "status-active" : "status-inactive"
                      }
                    >
                      {branch.active ? "Active" : "Inactive"}
                    </span>
                  </p>
                </div>
              ))}
            </div>
          </section>
        )}

        {activeSection === "vehicles" && (
          <section className="owner-section">
            <h2>My Vehicles</h2>

            {dashboard.branches.map((branch) => (
              <div className="table-block" key={branch.branchId}>
                <h3>{branch.branchName}</h3>

                {branch.vehicles.length === 0 ? (
                  <p>No vehicles found for this branch.</p>
                ) : (
                  <table>
                    <thead>
                      <tr>
                        <th>Vehicle Number</th>
                        <th>Capacity</th>
                        <th>Status</th>
                      </tr>
                    </thead>

                    <tbody>
                      {branch.vehicles.map((vehicle) => (
                        <tr key={vehicle.vehicleId}>
                          <td>{vehicle.vehicleNumber}</td>
                          <td>{vehicle.vehicleCapacity} L</td>
                          <td>{vehicle.status}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            ))}
          </section>
        )}

        {activeSection === "drivers" && (
          <section className="owner-section">
            <h2>My Drivers</h2>

            {dashboard.branches.map((branch) => (
              <div className="table-block" key={branch.branchId}>
                <h3>{branch.branchName}</h3>

                {branch.drivers.length === 0 ? (
                  <p>No drivers found for this branch.</p>
                ) : (
                  <table>
                    <thead>
                      <tr>
                        <th>Driver Name</th>
                        <th>Phone</th>
                        <th>License Number</th>
                        <th>Status</th>
                        <th>Rating</th>
                      </tr>
                    </thead>

                    <tbody>
                      {branch.drivers.map((driver) => (
                        <tr key={driver.driverId}>
                          <td>{driver.driverName}</td>
                          <td>{driver.phoneNumber}</td>
                          <td>{driver.licenseNumber}</td>
                          <td>{driver.status}</td>
                          <td>{driver.rating}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            ))}
          </section>
        )}

        {activeSection === "bookings" && (
          <section className="owner-section">
            <h2>Bookings</h2>

            {dashboard.branches.map((branch) => (
              <div className="table-block" key={branch.branchId}>
                <h3>{branch.branchName}</h3>

                {branch.bookings.length === 0 ? (
                  <p>No bookings found for this branch.</p>
                ) : (
                  <table>
                    <thead>
                      <tr>
                        <th>Customer</th>
                        <th>Quantity</th>
                        <th>Total Price</th>
                        <th>Address</th>
                        <th>Status</th>
                        <th>Driver</th>
                        <th>Vehicle</th>
                      </tr>
                    </thead>

                    <tbody>
                      {branch.bookings.map((booking) => (
                        <tr key={booking.bookingId}>
                          <td>{booking.userName}</td>
                          <td>{booking.quantity} L</td>
                          <td>₹{booking.totalPrice}</td>
                          <td>{booking.deliveryAddress}</td>
                          <td>{booking.status}</td>
                          <td>{booking.driverName}</td>
                          <td>{booking.vehicleNumber}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            ))}
          </section>
        )}
      </main>
    </div>
  );
}

export default OwnerDashboard;