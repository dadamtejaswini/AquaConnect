import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import api from "../api/api";
import "../App.css";

const BASE_URL = "http://localhost:8080/";

function NearbyReservoirs() {
  const navigate = useNavigate();

  const [reservoirs, setReservoirs] = useState([]);
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getLocationAndFetch();
  }, []);

  const fixImageUrl = (url) => {
    if (!url) return null;

    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }

    const cleanUrl = url.replaceAll("\\", "/");

    if (cleanUrl.startsWith("/")) {
      return BASE_URL + cleanUrl.substring(1);
    }

    return BASE_URL + cleanUrl;
  };

  const normalizeReservoir = (r) => {
    const images =
      r.imageUrls ||
      r.reservoirImages ||
      r.images ||
      (r.reservoirImage ? [r.reservoirImage] : []);

    return {
      branchId: r.branchId || r.id,
      branchName: r.branchName || r.name,
      location: r.location,
      latitude: r.latitude,
      longitude: r.longitude,
      currentWaterQuantity: r.currentWaterQuantity,
      ownerName: r.ownerName || r.ownerFullName || "-",

      imageUrls: images.map((img) => fixImageUrl(img)).filter(Boolean),

      vehicles: r.vehicles || [],
      drivers: r.drivers || [],
      feedbacks: r.feedbacks || [],

      distanceInKm: r.distanceInKm,
      startingPrice: r.startingPrice,
    };
  };

  const getLocationAndFetch = () => {
    setLoading(true);

    if (!navigator.geolocation) {
      alert("Location is not supported in this browser");
      setLoading(false);
      return;
    }

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        try {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;

          const res = await api.get(
            `/api/reservoirs/nearby?lat=${lat}&lng=${lng}`
          );

          const data = (res.data || []).map(normalizeReservoir);

          setReservoirs(data);

          if (data.length > 0) {
            setSelected(data[0]);
          }
        } catch (error) {
          console.log("NEARBY RESERVOIRS ERROR:", error);

          alert(
            error.response?.data?.message ||
              error.response?.data?.error ||
              (typeof error.response?.data === "string"
                ? error.response.data
                : JSON.stringify(error.response?.data)) ||
              "Failed to load nearby reservoirs"
          );
        } finally {
          setLoading(false);
        }
      },
      () => {
        alert("Please allow location access to find nearby reservoirs");
        setLoading(false);
      }
    );
  };

  const handleBookFromHere = () => {
    if (!selected) return;

    navigate("/book-tanker", {
      state: {
        branchId: selected.branchId,
        branchName: selected.branchName,
        location: selected.location,
        startingPrice: selected.startingPrice,
      },
    });
  };

  return (
    <>
      <Navbar />

      <div className="nearby-page">
        <div className="nearby-header">
          <h1>Nearby Water Reservoirs</h1>

          <p>
            Select a reservoir to view images, vehicles, drivers and feedback.
          </p>

          <button onClick={getLocationAndFetch}>Refresh My Location</button>
        </div>

        {loading ? (
          <h2>Finding nearby reservoirs...</h2>
        ) : (
          <div className="nearby-layout">
            <div className="reservoir-list">
              {reservoirs.length === 0 ? (
                <div className="empty-booking">
                  <h3>No nearby reservoirs found</h3>
                </div>
              ) : (
                reservoirs.map((r) => (
                  <div
                    key={r.branchId}
                    className={`reservoir-card ${
                      selected?.branchId === r.branchId
                        ? "selected-reservoir"
                        : ""
                    }`}
                    onClick={() => setSelected(r)}
                  >
                    <div className="reservoir-img">
                      {r.imageUrls?.length > 0 ? (
                        <img src={r.imageUrls[0]} alt={r.branchName} />
                      ) : (
                        <span>💧</span>
                      )}
                    </div>

                    <div>
                      <h3>{r.branchName}</h3>

                      <p>{r.location}</p>

                      <p>
                        <b>Water:</b> {r.currentWaterQuantity || 0} L
                      </p>

                      {r.distanceInKm && (
                        <p>
                          <b>Distance:</b>{" "}
                          {Number(r.distanceInKm).toFixed(2)}km
                        </p>
                      )}

                      {r.startingPrice && (
                        <p>
                          <b>Starting Price:</b> ₹{r.startingPrice}
                        </p>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>

            <div className="reservoir-details">
              {!selected ? (
                <div className="empty-booking">
                  <h3>Select a reservoir</h3>
                </div>
              ) : (
                <>
                  <div className="reservoir-details-header">
                    <div>
                      <h2>{selected.branchName}</h2>
                      <p>{selected.location}</p>
                    </div>

                    <button
                      className="book-tanker-btn"
                      onClick={handleBookFromHere}
                    >
                      Book Tanker From Here
                    </button>
                  </div>

                  <h3>Reservoir Images</h3>

                  <div className="detail-image-grid">
                    {selected.imageUrls?.length > 0 ? (
                      selected.imageUrls.map((img, i) => (
                        <img key={i} src={img} alt="Reservoir" />
                      ))
                    ) : (
                      <p>No reservoir images</p>
                    )}
                  </div>

                  <h3>Vehicles</h3>

                  <div className="detail-card-grid">
                    {selected.vehicles?.length > 0 ? (
                      selected.vehicles.map((v, index) => (
                        <div
                          className="small-detail-card"
                          key={v.vehicleId || index}
                        >
                          {v.vehicleImageUrl && (
                            <img
                              src={fixImageUrl(v.vehicleImageUrl)}
                              alt="Vehicle"
                            />
                          )}

                          <h4>{v.vehicleNumber}</h4>

                          <p>{v.vehicleCapacity} L</p>

                          {v.status && <p>{v.status}</p>}
                        </div>
                      ))
                    ) : (
                      <p>No vehicle details available</p>
                    )}
                  </div>

                  <h3>Drivers</h3>

                  <div className="detail-card-grid">
                    {selected.drivers?.length > 0 ? (
                      selected.drivers.map((d, index) => (
                        <div
                          className="small-detail-card"
                          key={d.driverId || index}
                        >
                          {d.driverImageUrl && (
                            <img
                              src={fixImageUrl(d.driverImageUrl)}
                              alt="Driver"
                            />
                          )}

                          <h4>{d.driverName}</h4>

                          <p>{d.rating || 0} ⭐</p>

                          {d.status && <p>{d.status}</p>}
                        </div>
                      ))
                    ) : (
                      <p>No driver details available</p>
                    )}
                  </div>

                  <h3>Feedbacks</h3>

                  <div className="feedback-list">
                    {selected.feedbacks?.length > 0 ? (
                      selected.feedbacks.map((f, index) => (
                        <div
                          className="feedback-card"
                          key={f.feedbackId || index}
                        >
                          <h4>{f.userName}</h4>
                          <p>{f.rating} ⭐</p>
                          <p>{f.message}</p>
                        </div>
                      ))
                    ) : (
                      <p>No feedback yet</p>
                    )}
                  </div>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </>
  );
}

export default NearbyReservoirs;