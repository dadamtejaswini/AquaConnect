import { useState } from "react";
import Navbar from "../components/Navbar";
import "../App.css";

function Contact() {
  const [issueType, setIssueType] = useState("Booking Problem");
  const [issue, setIssue] = useState("");

  const owners = [
    { name: "Ramesh Kumar", email: "rameshk@gmail.com", phone: "9876543210" },
    { name: "Ramya Gowda", email: "ramyag@gmail.com", phone: "9780674567" },
    { name: "Ramya Gowda (Marathahalli)", email: "ramya.marathahalli@gmail.com", phone: "9780674568" },
    { name: "Priya Nair", email: "priya.hsr@gmail.com", phone: "9780674569" },
    { name: "Ananya Reddy", email: "ananya.reddy@gmail.com", phone: "9911223344" },
    { name: "Arjun Kumar", email: "arjunkumar@gmail.com", phone: "9876543211" },
  ];

  const submitIssue = () => {
    if (!issue.trim()) {
      alert("Please describe your issue");
      return;
    }

    alert("Issue submitted successfully");
    setIssue("");
    setIssueType("Booking Problem");
  };

  return (
    <>
      <Navbar />

      <div className="contact-page">
        <div className="contact-header">
          <h1>Contact & Support</h1>
          <p>Need help? Contact support, reservoir owners, or report an issue.</p>
        </div>

        <div className="support-grid">
          <div className="support-card">
            <h2>Customer Support</h2>
            <p>📞 +91 98765 43210</p>
            <p>📞 +91 99887 76655</p>
            <p>📞 +91 91234 56789</p>
          </div>

          <div className="support-card">
            <h2>Support Email</h2>
            <p>support@aquaconnect.com</p>
            <p>help@aquaconnect.com</p>
          </div>
        </div>

        <h2 className="owner-title">Reservoir Owners</h2>

        <div className="owners-list">
  {owners.map((owner) => (
    <div className="owner-row" key={owner.email}>
      
      <div className="owner-info">
        <h3>{owner.name}</h3>
        <p>📞 {owner.phone}</p>
        <p>📧 {owner.email}</p>
      </div>

      <div className="owner-actions">
        <a
          href={`tel:${owner.phone}`}
          className="owner-call-btn"
        >
          Call
        </a>

        <a
          href={`mailto:${owner.email}`}
          className="owner-mail-btn"
        >
          Email
        </a>
      </div>

    </div>
  ))}
</div>

        <div className="issue-card">
          <h2>Report an Issue</h2>

          <select value={issueType} onChange={(e) => setIssueType(e.target.value)}>
            <option>Booking Problem</option>
            <option>Driver Problem</option>
            <option>Payment Problem</option>
            <option>Water Quality Issue</option>
            <option>Other</option>
          </select>

          <textarea
            value={issue}
            onChange={(e) => setIssue(e.target.value)}
            placeholder="Describe your issue..."
          />

          <button onClick={submitIssue}>Submit Issue</button>
        </div>
      </div>
    </>
  );
}

export default Contact;