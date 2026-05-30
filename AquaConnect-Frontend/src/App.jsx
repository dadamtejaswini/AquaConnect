import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ResetPassword from "./pages/ResetPassword";
import OwnerDashboard from "./pages/OwnerDashboard";
import DriverDashboard from "./pages/DriverDashboard";
import UserBookings from "./pages/UserBookings";
import NearbyReservoirs from "./pages/NearbyReservoirs";
import AboutAquaConnect from "./pages/AboutAquaConnect";
import Contact from "./pages/Contact";
import BookTanker from "./pages/BookTanker";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/bookings" element={<UserBookings />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route path="/owner-dashboard" element={<OwnerDashboard />} />
        <Route path="/driver-dashboard" element={<DriverDashboard />} />
        <Route path="/nearby-reservoirs" element={<NearbyReservoirs />} />
        <Route path="/about-aquaconnect" element={<AboutAquaConnect />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/book-tanker" element={<BookTanker />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;