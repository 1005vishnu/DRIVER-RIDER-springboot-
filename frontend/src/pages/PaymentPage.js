import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function PaymentPage() {
  const { rideId } = useParams();
  const [fare, setFare] = useState('');
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    // Optionally fetch the bill before payment
    fetch(`http://localhost:8080/rides/${rideId}/bill`)
      .then(res => res.ok ? res.text() : Promise.reject('Failed to get bill'))
      .then(billText => setFare(billText))
      .catch(e => setError(e.toString()));
  }, [rideId]);

  const handlePay = () => {
    setStatus('');
    setError('');
    fetch(`http://localhost:8080/rides/${rideId}/pay`, { method: 'POST' })
      .then(res => res.ok ? res.text() : Promise.reject('Payment failed'))
      .then(msg => {
        setStatus(msg);
        setTimeout(() => navigate(`/rate/${rideId}`), 1000); // Redirect to rating after 1s
      })
      .catch(e => setError(e.toString()));
  };

  return (
    <div className="dashboard-container">
      <h2>Ride Payment</h2>
      <div className="dashboard-card">
        <div><b>Ride ID:</b> {rideId}</div>
        <div><b>Fare:</b> {fare}</div>
        <button className="primary-btn" onClick={handlePay}>Pay Now</button>
        {status && <div className="success">{status}</div>}
        {error && <div className="error">{error}</div>}
      </div>
    </div>
  );
}

export default PaymentPage;
