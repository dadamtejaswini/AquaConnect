import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import api from "../api/api";
import "../App.css";

function BookTanker() {
  const navigate = useNavigate();
  const location = useLocation();

  const reservoir = location.state;

  const waterPrices =
    reservoir?.waterPrices ||
    reservoir?.prices ||
    reservoir?.waterPriceList ||
    [];

  const [formData, setFormData] = useState({
    quantity: "",
    customQuantity: "",
    deliveryAddress: "",
    specialOccasion: "",
    deliveryLatitude: "",
    deliveryLongitude: "",
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const useMyLocation = () => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setFormData({
          ...formData,
          deliveryLatitude: position.coords.latitude,
          deliveryLongitude: position.coords.longitude,
          deliveryAddress: `Current Location (${position.coords.latitude}, ${position.coords.longitude})`,
        });
      },
      () => alert("Please allow location access")
    );
  };

  const bookTanker = async (e) => {
    e.preventDefault();

    if (!reservoir?.branchId) {
      alert("Please select a reservoir first");
      navigate("/nearby-reservoirs");
      return;
    }

    const finalQuantity =
      formData.quantity === "OTHER"
        ? Number(formData.customQuantity)
        : Number(formData.quantity);

    try {
      const requestData = {
        branchId: reservoir.branchId,
        quantity: finalQuantity,
        deliveryAddress: formData.deliveryAddress,
        deliveryLatitude: Number(formData.deliveryLatitude),
        deliveryLongitude: Number(formData.deliveryLongitude),
        specialOccasion:
          formData.quantity === "OTHER"
            ? formData.specialOccasion
            : "",
      };

      await api.post("/api/user/bookings", requestData);

      alert("Booking placed successfully");
      navigate("/bookings");
    } catch (error) {
      console.log(error);

      alert(
        error.response?.data?.message ||
          error.response?.data ||
          "Booking failed"
      );
    }
  };

  return (
    <>
      <Navbar />

      <div className="book-center-section">
        <form className="book-card-pro" onSubmit={bookTanker}>
          <div className="book-card-header">
            <h2>Book Water Tanker</h2>

            <p>
              Reservoir: <b>{reservoir?.branchName || "Not selected"}</b>
            </p>

            <p>{reservoir?.location}</p>
          </div>

          <div className="book-form-grid">
            <div className="book-field">
              <label>Water Quantity</label>

              <select
                name="quantity"
                value={formData.quantity}
                onChange={handleChange}
                required
              >
                <option value="">Select Quantity</option>

                {waterPrices.length > 0 ? (
                  waterPrices.map((item, index) => (
                    <option
                      key={item.id || item.priceId || index}
                      value={item.quantity || item.waterQuantity}
                    >
                      {item.quantity || item.waterQuantity} L - ₹
                      {item.price}
                    </option>
                  ))
                ) : (
                  <>
                    <option value="1000">1000 L</option>
                    <option value="2000">2000 L</option>
                    <option value="5000">5000 L</option>
                    <option value="10000">10000 L</option>
                  </>
                )}

                <option value="OTHER">Other...</option>
              </select>
            </div>

            {formData.quantity === "OTHER" && (
              <>
                <div className="book-field">
                  <label>Enter Quantity in Litres</label>

                  <input
                    type="number"
                    name="customQuantity"
                    placeholder="Example: 3000"
                    value={formData.customQuantity}
                    onChange={handleChange}
                    min="500"
                    required
                  />
                </div>

                <div className="book-field full-width">
                  <label>Special Occasion</label>

                  <input
                    type="text"
                    name="specialOccasion"
                    placeholder="Example: Wedding, Function, Emergency, Construction"
                    value={formData.specialOccasion}
                    onChange={handleChange}
                    required
                  />
                </div>
              </>
            )}

            <div className="book-field full-width">
              <label>Delivery Address</label>

              <textarea
                name="deliveryAddress"
                placeholder="Enter delivery address"
                value={formData.deliveryAddress}
                onChange={handleChange}
                required
              />
            </div>

            <div className="book-field">
              <label>Latitude</label>

              <input
                type="text"
                name="deliveryLatitude"
                value={formData.deliveryLatitude}
                onChange={handleChange}
                required
              />
            </div>

            <div className="book-field">
              <label>Longitude</label>

              <input
                type="text"
                name="deliveryLongitude"
                value={formData.deliveryLongitude}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="book-actions-pro">
            <button
              type="button"
              className="location-btn-pro"
              onClick={useMyLocation}
            >
              Use My Current Location
            </button>

            <button type="submit" className="confirm-btn-pro">
              Confirm Booking
            </button>
          </div>
        </form>
      </div>
    </>
  );
}

export default BookTanker;