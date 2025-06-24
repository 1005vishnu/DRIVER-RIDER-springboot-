import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

function LoginPage() {
  const [form, setForm] = useState({
    id: "",
    role: "rider"
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const params = new URLSearchParams({
        id: form.id,
        role: form.role
      });
      const res = await fetch('http://localhost:8080/auth/login?' + params.toString(), {
        method: "POST"
      });
      const data = await res.json(); 
      if (res.status === 200 && data.status === 'success') {
        localStorage.setItem('userId', form.id);
        localStorage.setItem('role', form.role);
        if (form.role === 'driver') {
          navigate('/driver/dashboard');
        } else {
          navigate('/rider/book');
        }
      } else {
        setError(data.message || 'Login failed');
      }
    } catch (err) {
      setError("Connection error: Please check if the server is running");
    }
  };

  return (
    <div className="login-container">
      <h2>Login</h2>
      <p style={{ marginBottom: '20px' }}>
        Don't have an account? <a href="/signup" style={{ color: '#0066cc', textDecoration: 'none' }}>Sign up here</a>
      </p>
      <form onSubmit={handleSubmit}>
        <input
          name="id"
          type="text"
          placeholder="User ID"
          value={form.id}
          onChange={handleChange}
          required
        />
        <select name="role" value={form.role} onChange={handleChange}>
          <option value="rider">Rider</option>
          <option value="driver">Driver</option>
        </select>
        <button type="submit">Login</button>
      </form>
      {error && (
        <div className="error" style={{ color: 'red', marginTop: '10px', padding: '10px', backgroundColor: '#ffebee', borderRadius: '4px' }}>
          {error}
          {error.includes('User ID not found') && (
            <div style={{ marginTop: '10px' }}>
              <span>Don't have an account? </span>
              <a href="/signup" style={{ color: '#0066cc', textDecoration: 'underline' }}>Sign up here</a>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default LoginPage;
