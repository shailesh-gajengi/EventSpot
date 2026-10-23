import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiService } from '../services/api';
import { UserPlus, CheckSquare } from 'lucide-react';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    organizerName: '',
    organizerContact: '',
    organizerEmail: '',
    password: '',
    termsConsent: false,
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.termsConsent) {
      setError('You must accept the Terms and Conditions to register as an artist.');
      return;
    }

    setLoading(true);

    try {
      const res = await apiService.register(formData);
      if (res.success) {
        setSuccess('Registration successful! Redirecting to login...');
        setTimeout(() => {
          navigate('/login');
        }, 1500);
      } else {
        setError(res.message || 'Registration failed');
      }
    } catch (err) {
      setError(
        err?.response?.data?.details?.[0] ||
        err?.response?.data?.message ||
        err.message ||
        'Registration failed'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '500px', margin: '2.5rem auto' }}>
      <div className="card">
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <h2 style={{ fontSize: '1.6rem', fontWeight: 700, color: '#0f172a' }}>Artist / Organizer Registration</h2>
          <p style={{ color: '#64748b', fontSize: '0.9rem', marginTop: '0.25rem' }}>
            Create your account to submit and host events for ticket booking
          </p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name / Band / Organization Name *</label>
            <input
              type="text"
              name="organizerName"
              required
              className="form-control"
              placeholder="e.g. John Sterling or Stellar Productions"
              value={formData.organizerName}
              onChange={handleChange}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Email Address *</label>
              <input
                type="email"
                name="organizerEmail"
                required
                className="form-control"
                placeholder="artist@domain.com"
                value={formData.organizerEmail}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Contact Number *</label>
              <input
                type="tel"
                name="organizerContact"
                required
                className="form-control"
                placeholder="+1 555-0199"
                value={formData.organizerContact}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Password * (Min 6 characters)</label>
            <input
              type="password"
              name="password"
              required
              minLength={6}
              className="form-control"
              placeholder="••••••••"
              value={formData.password}
              onChange={handleChange}
            />
          </div>

          {/* Terms & Conditions Consent (as specified in ER diagram) */}
          <div className="form-group" style={{ marginTop: '1.25rem' }}>
            <label className="checkbox-label">
              <input
                type="checkbox"
                name="termsConsent"
                checked={formData.termsConsent}
                onChange={handleChange}
              />
              <span>
                I agree to the <strong>Terms & Conditions</strong> and consent to platform moderation and ticket commission policies.
              </span>
            </label>
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '0.5rem' }}
            disabled={loading}
          >
            <UserPlus size={16} />
            {loading ? 'Registering...' : 'Complete Artist Registration'}
          </button>
        </form>

        <div style={{ marginTop: '1.25rem', textAlign: 'center', fontSize: '0.875rem', color: '#64748b' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ color: '#4f46e5', fontWeight: 600 }}>
            Sign In here
          </Link>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
