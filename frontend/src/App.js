import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import DriverDashboardPage from './pages/DriverDashboardPage';
import BookRidePage from './pages/BookRidePage';
import PaymentPage from './pages/PaymentPage';
import RateDriverPage from './pages/RateDriverPage';
import './App.css';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/driver/dashboard" element={<DriverDashboardPage />} />
        <Route path="/rider/book" element={<BookRidePage />} />
        <Route path="/pay/:rideId" element={<PaymentPage />} />
        <Route path="/rate/:rideId" element={<RateDriverPage />} />
      </Routes>
    </Router>
  );
}

export default App;
