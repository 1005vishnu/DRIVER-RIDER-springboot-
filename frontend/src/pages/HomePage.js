import React from 'react';
import { Link } from 'react-router-dom';
import './HomePage.css';

export default function HomePage() {
  return (
    <>
      <div className="goride-bg"></div>
      <div className="homepage-container">
        <h1 className="goride-title">GORIDE</h1>
        <div className="goride-subtitle">Your Smart Ride-Hailing Solution</div>
        <div className="links">
          <Link to="/login" className="btn">Login</Link>
          <Link to="/signup" className="btn">Sign Up</Link>
        </div>
      </div>
    </>
  );
}
