import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Calendar, PlusCircle, ShieldCheck, LogOut, LogIn, UserPlus } from 'lucide-react';

const Navbar = () => {
  const { user, logout, isArtist, isAdmin, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="brand-logo">
          <Calendar size={24} />
          <span>EventSpot</span>
        </Link>

        <div className="nav-links">
          <Link to="/" className={`nav-item ${isActive('/') ? 'active' : ''}`}>
            Explore Hosted Events
          </Link>

          {isArtist && (
            <>
              <Link
                to="/artist/dashboard"
                className={`nav-item ${isActive('/artist/dashboard') ? 'active' : ''}`}
              >
                My Events
              </Link>
              <Link
                to="/artist/submit"
                className={`btn btn-primary btn-sm ${isActive('/artist/submit') ? 'active' : ''}`}
              >
                <PlusCircle size={15} />
                Submit Event
              </Link>
            </>
          )}

          {isAdmin && (
            <Link
              to="/admin/dashboard"
              className={`nav-item ${isActive('/admin/dashboard') ? 'active' : ''}`}
            >
              <ShieldCheck size={16} style={{ display: 'inline', marginRight: '4px' }} />
              Admin Moderation
            </Link>
          )}

          {isAuthenticated ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginLeft: '0.5rem' }}>
              <div className={`user-badge ${isAdmin ? 'admin' : ''}`}>
                <span>{user?.name || user?.email}</span>
                <span style={{ opacity: 0.65 }}>({user?.role})</span>
              </div>
              <button onClick={handleLogout} className="btn btn-secondary btn-sm" title="Log out">
                <LogOut size={15} />
                Logout
              </button>
            </div>
          ) : (
            <div style={{ display: 'flex', gap: '0.5rem', marginLeft: '0.5rem' }}>
              <Link to="/login" className="btn btn-secondary btn-sm">
                <LogIn size={15} />
                Login
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                <UserPlus size={15} />
                Register as Artist
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
