import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiService } from '../services/api';
import { LogIn, User, ShieldCheck } from 'lucide-react';

const LoginPage = () => {
  const [role, setRole] = useState('ARTIST'); // 'ARTIST' or 'ADMIN'
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleRoleSwitch = (newRole) => {
    setRole(newRole);
    setError('');
    if (newRole === 'ADMIN') {
      setEmail('admin@eventhosting.com');
      setPassword('admin123');
    } else {
      setEmail('');
      setPassword('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const res = await apiService.login({ email, password, role });
      if (res.success && res.data) {
        login(res.data);
        if (res.data.role === 'ADMIN') {
          navigate('/admin/dashboard');
        } else {
          navigate('/artist/dashboard');
        }
      } else {
        setError(res.message || 'Login failed');
      }
    } catch (err) {
      setError(
        err?.response?.data?.details?.[0] ||
        err?.response?.data?.message ||
        err.message ||
        'Failed to connect to the server'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '440px', margin: '3rem auto' }}>
      <div className="card">
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <h2 style={{ fontSize: '1.6rem', fontWeight: 700, color: '#0f172a' }}>Welcome Back</h2>
          <p style={{ color: '#64748b', fontSize: '0.9rem', marginTop: '0.25rem' }}>
            Sign in to access your event dashboard
          </p>
        </div>

        {/* Role Selector Tabs */}
        <div className="tabs" style={{ justifyContent: 'center', marginBottom: '1.5rem' }}>
          <button
            type="button"
            className={`tab-btn ${role === 'ARTIST' ? 'active' : ''}`}
            onClick={() => handleRoleSwitch('ARTIST')}
          >
            <User size={15} style={{ display: 'inline', marginRight: '4px' }} />
            Artist / Organizer
          </button>
          <button
            type="button"
            className={`tab-btn ${role === 'ADMIN' ? 'active' : ''}`}
            onClick={() => handleRoleSwitch('ADMIN')}
          >
            <ShieldCheck size={15} style={{ display: 'inline', marginRight: '4px' }} />
            Admin Reviewer
          </button>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              required
              className="form-control"
              placeholder={role === 'ADMIN' ? 'admin@eventhosting.com' : 'artist@band.com'}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              required
              className="form-control"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '0.5rem' }}
            disabled={loading}
          >
            <LogIn size={16} />
            {loading ? 'Authenticating...' : `Log In as ${role === 'ADMIN' ? 'Admin' : 'Artist'}`}
          </button>
        </form>

        {role === 'ARTIST' && (
          <div style={{ marginTop: '1.25rem', textAlign: 'center', fontSize: '0.875rem', color: '#64748b' }}>
            Don't have an artist account?{' '}
            <Link to="/register" style={{ color: '#4f46e5', fontWeight: 600 }}>
              Register here
            </Link>
          </div>
        )}

        {role === 'ADMIN' && (
          <div style={{ marginTop: '1rem', background: '#f8fafc', padding: '0.75rem', borderRadius: '0.35rem', fontSize: '0.8rem', color: '#64748b' }}>
            <strong>Testing Note:</strong> Configured admin credentials in <code>application.properties</code> are prefilled above.
          </div>
        )}
      </div>
    </div>
  );
};

export default LoginPage;
