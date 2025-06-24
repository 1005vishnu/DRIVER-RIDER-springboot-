import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './HomePage.css';

function BookRidePage() {
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedDriver, setSelectedDriver] = useState('');
  const [rideId, setRideId] = useState('');
  const [showPayButton, setShowPayButton] = useState(false);
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const riderId = localStorage.getItem('userId');
  if (!riderId) {
    navigate('/login');
    return null;
  }

  const handleMatchDrivers = () => {
    setLoading(true);
    setError('');
    fetch(`http://localhost:8080/riders/match?riderId=${riderId}`)
      .then(async res => {
        if (!res.ok) throw new Error('No drivers found');
        const text = await res.text(); // Read body ONCE
        let data;
        try {
          data = JSON.parse(text);
        } catch {
          data = text;
        }
        if (Array.isArray(data)) {
          setDrivers(data);
        } else {
          setError(typeof data === 'string' ? data : 'No drivers found');
          setDrivers([]);
        }
        setLoading(false);
      })
      .catch(e => {
        setError(e.toString());
        setLoading(false);
      });
  };

  const handleStartRide = () => {
    setStatus('');
    setError('');
    if (!selectedDriver) {
      setError('Please select a driver!');
      return;
    }
    fetch(`http://localhost:8080/rides/start?riderId=${riderId}&driverId=${selectedDriver}`, { method: 'POST' })
      .then(async res => {
        const text = await res.text();
        let data;
        try {
          data = JSON.parse(text);
        } catch {
          setError('Unexpected server response');
          return;
        }
        if (res.ok && data.rideId) {
          setRideId(data.rideId);
          setStatus('Ride started!');
        } else {
          setError(data.error || 'Could not start ride');
        }
      })
      .catch(e => setError(e.toString()));
  };

  const handleStopRide = () => {
    setStatus('');
    setError('');
    if (!rideId) return setError('No ride started!');
    fetch(`http://localhost:8080/rides/${rideId}/stop?endX=10&endY=10&timeTaken=15`, { method: 'POST' })
      .then(res => res.ok ? res.text() : Promise.reject('Failed to stop ride'))
      .then(() => {
        setStatus('Ride stopped! Please proceed to payment.');
        setShowPayButton(true);
        setDrivers([]);
      })
      .catch(e => setError(e.toString()));
  };

  const handlePay = () => {
    navigate(`/pay/${rideId}`);
  };

  const handleCancelStuckRide = () => {
    setStatus('');
    setError('');
    fetch(`http://localhost:8080/rides/cancel-active/${riderId}`, { method: 'POST' })
      .then(res => res.ok ? res.text() : Promise.reject('No stuck ride found or failed to cancel'))
      .then(msg => {
        setStatus(msg);
        setRideId('');
        setDrivers([]);
      })
      .catch(e => setError(e.toString()));
  };

  return (
    <div className="dashboard-bg">
      <div className="dashboard-container dashboard-centered">
        <h2 className="dashboard-title">Book a Ride</h2>
        <div className="dashboard-section">
          <button className="primary-btn" onClick={handleMatchDrivers} disabled={loading} style={{marginBottom:'1.2rem'}}>
            {loading ? 'Searching...' : 'Find Drivers'}
          </button>
          {error && <div className="error" style={{marginBottom:'0.8rem'}}>{error}</div>}
          {status && <div className="success" style={{marginBottom:'0.8rem'}}>{status}</div>}
          <div className="driver-list">
            {drivers.map(driver => (
              <div
                key={driver.id}
                className={`driver-item${selectedDriver === driver.id ? ' selected' : ''}`}
                onClick={() => setSelectedDriver(driver.id)}
              >
                <b>{driver.name}</b> (ID: <span style={{color:'#2c5364'}}>{driver.id}</span>, {driver.vehicleType})
              </div>
            ))}
          </div>
        </div>
        <div className="dashboard-section">
          {!rideId && (
            <button className="primary-btn" onClick={handleStartRide} disabled={!selectedDriver}>
              Start Ride
            </button>
          )}
          {rideId && !showPayButton && (
            <button className="primary-btn" onClick={handleStopRide}>
              Stop Ride
            </button>
          )}
          {showPayButton && rideId && (
            <button className="primary-btn" onClick={() => navigate(`/pay/${rideId}`)}>
              Pay
            </button>
          )}
          <button className="primary-btn cancel-btn" style={{marginTop:'1.5rem'}} onClick={handleCancelStuckRide}>
            Cancel Stuck Ride
          </button>
        </div>
      </div>
    </div>
  );
}
export default BookRidePage;
