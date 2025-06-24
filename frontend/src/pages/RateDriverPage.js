import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function RateDriverPage() {
  const { rideId } = useParams();
  const [rating, setRating] = useState(5);
  const [preferred, setPreferred] = useState(false);
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleRate = () => {
    setStatus('');
    setError('');
    fetch(`http://localhost:8080/rides/rate?rideId=${rideId}&rating=${rating}&preferred=${preferred}`, { method: 'POST' })
      .then(res => res.ok ? res.text() : Promise.reject('Failed to rate driver'))
      .then(msg => {
        setStatus(msg);
        setTimeout(() => navigate('/'), 1000); // Redirect to home after 1s
      })
      .catch(e => setError(e.toString()));
  };

  return (
    <div className="dashboard-container">
      <h2>Rate Your Driver</h2>
      <div className="dashboard-card">
        <div><b>Ride ID:</b> {rideId}</div>
        <label>Rating:
          <select value={rating} onChange={e => setRating(e.target.value)}>
            {[1,2,3,4,5].map(v => <option key={v} value={v}>{v}</option>)}
          </select>
        </label>
        <label style={{marginLeft: 16}}>
          <input type="checkbox" checked={preferred} onChange={e => setPreferred(e.target.checked)} /> Preferred
        </label>
        <button className="primary-btn" onClick={handleRate}>Submit Rating</button>
        {status && <div className="success">{status}</div>}
        {error && <div className="error">{error}</div>}
      </div>
    </div>
  );
}

export default RateDriverPage;
