import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import "../App.css";

function OwnerDashboard() {
  const navigate = useNavigate();

  const [activeSection, setActiveSection] = useState("dashboard");
  const [dashboard, setDashboard] = useState(null);
  const [message, setMessage] = useState("");
  const [success, setSuccess] = useState("");

  const [vehicleForm, setVehicleForm] = useState({
    branchId: "",
    vehicleNumber: "",
    vehicleCapacity: "",
    vehicleImage: null,
  });

  const [driverForm, setDriverForm] = useState({
    branchId: "",
    driverName: "",
    phoneNumber: "",
    licenseNumber: "",
    password: "",
    driverImage: null,
    licenseImage: null,
  });

  const [assignForm, setAssignForm] = useState({
    bookingId: "",
    driverId: "",
    vehicleId: "",
  });

  const [statusForm, setStatusForm] = useState({
    bookingId: "",
    status: "OUT_FOR_DELIVERY",
  });

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

  const showSuccess = (msg) => {
    setSuccess(msg);
    setTimeout(() => setSuccess(""), 3000);
  };

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const handleVehicleChange = (e) => {
    const { name, value, files } = e.target;
    setVehicleForm({
      ...vehicleForm,
      [name]: files ? files[0] : value,
    });
  };

  const handleDriverChange = (e) => {
    const { name, value, files } = e.target;
    setDriverForm({
      ...driverForm,
      [name]: files ? files[0] : value,
    });
  };

  const handleAssignChange = (e) => {
    setAssignForm({
      ...assignForm,
      [e.target.name]: e.target.value,
    });
  };

  const handleStatusChange = (e) => {
    setStatusForm({
      ...statusForm,
      [e.target.name]: e.target.value,
    });
  };

  const addVehicle = async (e) => {
    e.preventDefault();

    try {
      const formData = new FormData();

      formData.append("branchId", String(vehicleForm.branchId));
      formData.append("vehicleNumber", vehicleForm.vehicleNumber);
      formData.append("vehicleCapacity", vehicleForm.vehicleCapacity);

      if (vehicleForm.vehicleImage) {
        formData.append("vehicleImage", vehicleForm.vehicleImage);
      }

      await api.post("/api/owners/vehicles", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      setVehicleForm({
        branchId: "",
        vehicleNumber: "",
        vehicleCapacity: "",
        vehicleImage: null,
      });

      e.target.reset();
      showSuccess("Vehicle added successfully");
      fetchDashboard();
    } catch (error) {
      console.log("FULL VEHICLE ERROR:", error);

      let errorMessage = "Failed to add vehicle";

      if (typeof error.response?.data === "string") {
        errorMessage = error.response.data;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.response?.data?.error) {
        errorMessage = error.response.data.error;
      }

      alert(errorMessage);
    }
  };

  const addDriver = async (e) => {
    e.preventDefault();

    try {
      const formData = new FormData();

      formData.append("branchId", String(driverForm.branchId));
      formData.append("driverName", driverForm.driverName);
      formData.append("phoneNumber", driverForm.phoneNumber);
      formData.append("licenseNumber", driverForm.licenseNumber);
      formData.append("password", driverForm.password);

      if (driverForm.driverImage) {
        formData.append("driverImage", driverForm.driverImage);
      }

      if (driverForm.licenseImage) {
        formData.append("licenseImage", driverForm.licenseImage);
      }

      await api.post("/api/owners/drivers", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      setDriverForm({
        branchId: "",
        driverName: "",
        phoneNumber: "",
        licenseNumber: "",
        password: "",
        driverImage: null,
        licenseImage: null,
      });

      e.target.reset();
      showSuccess("Driver added successfully");
      fetchDashboard();
    } catch (error) {
      console.log("FULL DRIVER ERROR:", error);

      let errorMessage = "Failed to add driver";

      if (typeof error.response?.data === "string") {
        errorMessage = error.response.data;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.response?.data?.error) {
        errorMessage = error.response.data.error;
      }

      alert(errorMessage);
    }
  };

  const assignBooking = async (e) => {
    e.preventDefault();

    try {
      await api.put(`/api/owners/bookings/${assignForm.bookingId}/assign`, {
        driverId: Number(assignForm.driverId),
        vehicleId: Number(assignForm.vehicleId),
      });

      setAssignForm({
        bookingId: "",
        driverId: "",
        vehicleId: "",
      });

      showSuccess("Driver and vehicle assigned successfully");
      fetchDashboard();
    } catch (error) {
      alert(
        error.response?.data?.message ||
          JSON.stringify(error.response?.data) ||
          "Failed to assign"
      );
    }
  };

  const updateBookingStatus = async (e) => {
    e.preventDefault();

    try {
      await api.put(`/api/owners/bookings/${statusForm.bookingId}/status`, {
        status: statusForm.status,
      });

      setStatusForm({
        bookingId: "",
        status: "OUT_FOR_DELIVERY",
      });

      showSuccess("Booking status updated successfully");
      fetchDashboard();
    } catch (error) {
      alert(
        error.response?.data?.message ||
          error.response?.data ||
          "Failed to update status"
      );
    }
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

        <button onClick={() => navigate("/")}>Home Page</button>

        <button className="logout-btn" onClick={logout}>
          Logout
        </button>
      </aside>

      <main className="owner-main">
        <div className="owner-header">
          <div>
            <h1>Welcome, {dashboard.ownerName}</h1>
            <p>Owner Dashboard</p>

            <div className="dashboard-profile-box">
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
          </div>
        </div>

        {success && <div className="success-box">{success}</div>}

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
                      <strong>Branch ID:</strong> {branch.branchId}
                    </p>
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

        {activeSection === "vehicles" && (
          <section className="owner-section">
            <h2>Add Vehicle</h2>

            <form className="owner-form modern-form" onSubmit={addVehicle}>
              <div className="form-row">
                <div className="form-group">
                  <label>Select Branch *</label>
                  <select
                    name="branchId"
                    value={vehicleForm.branchId}
                    onChange={handleVehicleChange}
                    required
                  >
                    <option value="">Select Branch</option>
                    {dashboard.branches.map((branch) => (
                      <option key={branch.branchId} value={branch.branchId}>
                        {branch.branchName}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Vehicle Number *</label>
                  <input
                    type="text"
                    name="vehicleNumber"
                    placeholder="Enter vehicle number"
                    value={vehicleForm.vehicleNumber}
                    onChange={handleVehicleChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Vehicle Capacity *</label>
                  <input
                    type="number"
                    name="vehicleCapacity"
                    placeholder="Enter capacity in litres"
                    value={vehicleForm.vehicleCapacity}
                    onChange={handleVehicleChange}
                    required
                  />
                </div>
              </div>

              <div className="upload-row">
                <div className="upload-box">
                  <label>Vehicle Image *</label>
                  <div className="upload-area">
                    <input
                      type="file"
                      name="vehicleImage"
                      accept="image/*"
                      onChange={handleVehicleChange}
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="form-actions">
                <button type="submit">Add Vehicle</button>
              </div>
            </form>

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
                        <th>Vehicle ID</th>
                        <th>Vehicle Number</th>
                        <th>Capacity</th>
                        <th>Status</th>
                      </tr>
                    </thead>

                    <tbody>
                      {branch.vehicles.map((vehicle) => (
                        <tr key={vehicle.vehicleId}>
                          <td>{vehicle.vehicleId}</td>
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
            <h2>Add Driver</h2>

            <form className="owner-form modern-form" onSubmit={addDriver}>
              <div className="form-row">
                <div className="form-group">
                  <label>Select Branch *</label>
                  <select
                    name="branchId"
                    value={driverForm.branchId}
                    onChange={handleDriverChange}
                    required
                  >
                    <option value="">Select Branch</option>
                    {dashboard.branches.map((branch) => (
                      <option key={branch.branchId} value={branch.branchId}>
                        {branch.branchName}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Driver Name *</label>
                  <input
                    type="text"
                    name="driverName"
                    placeholder="Enter driver name"
                    value={driverForm.driverName}
                    onChange={handleDriverChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Phone Number *</label>
                  <input
                    type="text"
                    name="phoneNumber"
                    placeholder="Enter phone number"
                    value={driverForm.phoneNumber}
                    onChange={handleDriverChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>License Number *</label>
                  <input
                    type="text"
                    name="licenseNumber"
                    placeholder="Enter license number"
                    value={driverForm.licenseNumber}
                    onChange={handleDriverChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Password *</label>
                  <input
                    type="password"
                    name="password"
                    placeholder="Enter password"
                    value={driverForm.password}
                    onChange={handleDriverChange}
                    required
                  />
                </div>
              </div>

              <div className="upload-row two-columns">
                <div className="upload-box">
                  <label>Driver Image *</label>
                  <div className="upload-area">
                    <input
                      type="file"
                      name="driverImage"
                      accept="image/*"
                      onChange={handleDriverChange}
                      required
                    />
                  </div>
                </div>

                <div className="upload-box">
                  <label>License Image *</label>
                  <div className="upload-area">
                    <input
                      type="file"
                      name="licenseImage"
                      accept="image/*"
                      onChange={handleDriverChange}
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="form-actions">
                <button type="submit">Add Driver</button>
              </div>
            </form>

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
                        <th>Driver ID</th>
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
                          <td>{driver.driverId}</td>
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
<h2>Assign Driver and Vehicle</h2>

<form className="owner-form" onSubmit={assignBooking}>
  <select
    name="bookingId"
    value={assignForm.bookingId}
    onChange={(e) => {
      setAssignForm({
        bookingId: e.target.value,
        driverId: "",
        vehicleId: "",
      });
    }}
    required
  >
    <option value="">Select Pending Booking</option>

    {dashboard.branches.flatMap((branch) =>
      branch.bookings
        .filter((booking) => booking.status === "PENDING")
        .map((booking) => (
          <option
            key={booking.bookingId}
            value={booking.bookingId}
          >
            Booking #{booking.bookingId} - {booking.userName} - {branch.branchName}
          </option>
        ))
    )}
  </select>

  {(() => {
    const selectedBranch = dashboard.branches.find((branch) =>
      branch.bookings.some(
        (booking) =>
          String(booking.bookingId) === String(assignForm.bookingId)
      )
    );

    return (
      <>
        <select
          name="driverId"
          value={assignForm.driverId}
          onChange={handleAssignChange}
          required
          disabled={!selectedBranch}
        >
          <option value="">Select Available Driver</option>

          {selectedBranch?.drivers
            ?.filter((driver) => driver.status === "AVAILABLE")
            .map((driver) => (
              <option key={driver.driverId} value={driver.driverId}>
                {driver.driverName} - {selectedBranch.branchName}
              </option>
            ))}
        </select>

        <select
          name="vehicleId"
          value={assignForm.vehicleId}
          onChange={handleAssignChange}
          required
          disabled={!selectedBranch}
        >
          <option value="">Select Available Vehicle</option>

          {selectedBranch?.vehicles
            ?.filter((vehicle) => vehicle.status === "AVAILABLE")
            .map((vehicle) => (
              <option key={vehicle.vehicleId} value={vehicle.vehicleId}>
                {vehicle.vehicleNumber} - {vehicle.vehicleCapacity} L - {selectedBranch.branchName}
              </option>
            ))}
        </select>
      </>
    );
  })()}

  <button type="submit">Assign Booking</button>
</form>

            <h2>Update Booking Status</h2>

            <form className="owner-form" onSubmit={updateBookingStatus}>
              <select
                name="bookingId"
                value={statusForm.bookingId}
                onChange={handleStatusChange}
                required
              >
                <option value="">Select Booking</option>
                {dashboard.branches.flatMap((branch) =>
                  branch.bookings
                    .filter(
                      (booking) =>
                        booking.status !== "DELIVERED" &&
                        booking.status !== "CANCELLED"
                    )
                    .map((booking) => (
                      <option key={booking.bookingId} value={booking.bookingId}>
                        Booking #{booking.bookingId} - {booking.status} -{" "}
                        {branch.branchName}
                      </option>
                    ))
                )}
              </select>

              <select
                name="status"
                value={statusForm.status}
                onChange={handleStatusChange}
                required
              >
                <option value="OUT_FOR_DELIVERY">OUT_FOR_DELIVERY</option>
                <option value="DELIVERED">DELIVERED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>

              <button type="submit">Update Status</button>
            </form>

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
                        <th>Booking ID</th>
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
                          <td>{booking.bookingId}</td>
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