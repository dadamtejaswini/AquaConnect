import { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import api from "../api/api";

const userIcon = new L.Icon({
  iconUrl:
    "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-black.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

const reservoirIcon = new L.Icon({
  iconUrl:
    "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-blue.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

function MapSection() {
  const [userLocation, setUserLocation] = useState([12.9716, 77.5946]);
  const [reservoirs, setReservoirs] = useState([]);

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setUserLocation([
          position.coords.latitude,
          position.coords.longitude,
        ]);
      },
      () => {
        console.log("Location permission denied");
      }
    );

    api
      .get("/api/reservoirs")
      .then((response) => {
        console.log("Reservoir Data:", response.data);
        setReservoirs(response.data);
      })
      .catch((error) => {
        console.log("Reservoir Error:", error);
      });
  }, []);

  return (
    <section className="map-section">
      <h2>Nearby Water Suppliers</h2>

      <MapContainer
        center={userLocation}
        zoom={11}
        style={{
          height: "500px",
          width: "100%",
          borderRadius: "20px",
        }}
      >
        <TileLayer
          attribution="&copy; OpenStreetMap contributors"
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {/* User Location */}
        <Marker position={userLocation} icon={userIcon}>
          <Popup>
            <strong>📍 Your Location</strong>
          </Popup>
        </Marker>

        {/* Reservoir Markers */}
        {reservoirs.map((reservoir) => (
          <Marker
            key={reservoir.id}
            position={[
              reservoir.latitude,
              reservoir.longitude,
            ]}
            icon={reservoirIcon}
          >
            <Popup>
              <div>
                <h3>{reservoir.branchName}</h3>

                <p>
                  <strong>Location:</strong>{" "}
                  {reservoir.location}
                </p>

                <p>
                  <strong>Water Available:</strong>{" "}
                  {reservoir.currentWaterQuantity} Litres
                </p>

                <p>
                  <strong>Status:</strong>{" "}
                  {reservoir.active ? "Active" : "Inactive"}
                </p>
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </section>
  );
}

export default MapSection;