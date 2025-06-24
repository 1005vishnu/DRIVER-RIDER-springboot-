import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './HomePage.css';

function DriverDashboardPage() {
  const [driver, setDriver] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [availability, setAvailability] = useState(false);
  const [updating, setUpdating] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Assume driverId is stored in localStorage after login
    const driverId = localStorage.getItem('userId');
    if (!driverId) {
      navigate('/login');
      return;
    }
    fetch(`http://localhost:8080/drivers/${driverId}`)
      .then(async res => {
        if (!res.ok) throw new Error('Failed to load driver');
        try {
          const data = await res.json();
          setDriver(data);
          setAvailability(data.available);
        } catch (err) {
          const text = await res.text();
          setError(text);
        }
        setLoading(false);
      })
      .catch(e => {
        setError(e.toString());
        setLoading(false);
      });
  }, [navigate]);

  const handleToggleAvailability = () => {
    setUpdating(true);
    const driverId = localStorage.getItem('userId');
    fetch(`/drivers/${driverId}/availability?available=${!availability}`, {
      method: 'PUT',
    })
      .then(res => res.ok ? res.text() : Promise.reject('Failed to update availability'))
      .then(() => {
        setAvailability(!availability);
        setUpdating(false);
      })
      .catch(e => {
        setError(e.toString());
        setUpdating(false);
      });
  };

  if (loading) return <div className="dashboard-container"><div className="loader"></div></div>;
  if (error) return <div className="dashboard-container"><div className="error">{error}</div></div>;

  return (
    <div className="dashboard-container">
      <h2>Welcome, {driver.name || driver.id}</h2>
      <div className="dashboard-card">
        <div><b>Email:</b> {driver.email}</div>
        <div><b>Status:</b> {availability ? 'Available' : 'Unavailable'}</div>
        <button className="primary-btn" onClick={handleToggleAvailability} disabled={updating}>
          {updating ? 'Updating...' : (availability ? 'Go Offline' : 'Go Online')}
        </button>
      </div>
      <div className="dashboard-section">
        <h3>Ride History</h3>
        <div className="info">(Coming soon)</div>
      </div>
    </div>
  );
}

export default DriverDashboardPage;
