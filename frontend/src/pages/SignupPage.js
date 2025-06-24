import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

function SignupPage() {
  const [form, setForm] = useState({
    id: "",
    email: "",
    password: "",
    x: "",
    y: "",
    role: "rider"
  });
  const [passwordError, setPasswordError] = useState("");

  const validatePassword = (password) => {
    if (password.length < 8) {
      return "Password must be at least 8 characters long";
    }
    if (!/[A-Z]/.test(password)) {
      return "Password must contain at least one uppercase letter";
    }
    if (!/[a-z]/.test(password)) {
      return "Password must contain at least one lowercase letter";
    }
    if (!/[0-9]/.test(password)) {
      return "Password must contain at least one number";
    }
    return "";
  };
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    const newPassword = e.target.name === 'password' ? e.target.value : form.password;
    setPasswordError(validatePassword(newPassword));
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    const url =
      form.role === "driver"
        ? "http://localhost:8080/drivers/add"
        : "http://localhost:8080/riders/add";
    try {
      const res = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          id: form.id,
          email: form.email,
          password: form.password,
          x: parseInt(form.x, 10),
          y: parseInt(form.y, 10)
        })
      });
      const text = await res.text();
      if (res.status === 200) {
        setSuccess(text);
        setTimeout(() => navigate("/login"), 1500);
      } else {
        setError(text);
      }
    } catch (err) {
      setError("Network error");
    }
  };

  return (
    <div className="signup-container">
      <h2>Sign Up</h2>
      <form onSubmit={handleSubmit}>
        <input
          name="id"
          placeholder="User ID"
          value={form.id}
          onChange={handleChange}
          required
        />
        <input
          name="email"
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
        />
        <input
          name="password"
          type="password"
          placeholder="Password (8+ chars, uppercase, lowercase, number)"
          value={form.password}
          onChange={handleChange}
          required
          minLength="8"
        />
        {passwordError && <div className="error">{passwordError}</div>}
        <input
          name="x"
          type="number"
          placeholder="Location X"
          value={form.x}
          onChange={handleChange}
          required
        />
        <input
          name="y"
          type="number"
          placeholder="Location Y"
          value={form.y}
          onChange={handleChange}
          required
        />
        <select name="role" value={form.role} onChange={handleChange}>
          <option value="rider">Rider</option>
          <option value="driver">Driver</option>
        </select>
        <button type="submit">Sign Up</button>
      </form>
      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}
    </div>
  );
}

export default SignupPage;
