import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiService } from '../services/api';
import StatusBadge from '../components/StatusBadge';
import { PlusCircle, Calendar, Clock, CheckCircle2, XCircle, AlertCircle, RefreshCw } from 'lucide-react';

const ArtistDashboard = () => {
  const { user, isArtist } = useAuth();
  const navigate = useNavigate();

  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user || !isArtist) {
      navigate('/login');
      return;
    }
    fetchMyEvents();
  }, [user, isArtist]);

  const fetchMyEvents = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await apiService.getArtistEvents(user.userId);
      setEvents(res.data || []);
    } catch (err) {
      setError('Unable to load your events. Please verify backend connection.');
    } finally {
      setLoading(false);
    }
  };

  // Metrics
  const totalCount = events.length;
  const pendingCount = events.filter((e) => e.approvalStatus?.toLowerCase() === 'pending').length;
  const hostedCount = events.filter((e) => e.approvalStatus?.toLowerCase() === 'approved').length;
  const rejectedCount = events.filter((e) => e.approvalStatus?.toLowerCase() === 'rejected').length;

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: 800, color: '#0f172a' }}>
            Artist Dashboard
          </h1>
          <p style={{ color: '#64748b', fontSize: '0.95rem' }}>
            Logged in as <strong>{user?.name}</strong> ({user?.email})
          </p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button onClick={fetchMyEvents} className="btn btn-secondary btn-sm" title="Refresh">
            <RefreshCw size={14} /> Refresh
          </button>
          <Link to="/artist/submit" className="btn btn-primary">
            <PlusCircle size={16} />
            Submit New Event
          </Link>
        </div>
      </div>

      {/* Metrics Cards */}
      <div className="stat-grid">
        <div className="stat-card">
          <div className="stat-icon primary">
            <Calendar size={22} />
          </div>
          <div>
            <div className="stat-value">{totalCount}</div>
            <div className="stat-label">Total Events Submitted</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon warning">
            <Clock size={22} />
          </div>
          <div>
            <div className="stat-value">{pendingCount}</div>
            <div className="stat-label">Pending Admin Review</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon success">
            <CheckCircle2 size={22} />
          </div>
          <div>
            <div className="stat-value">{hostedCount}</div>
            <div className="stat-label">Approved & Hosted Live</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon danger">
            <XCircle size={22} />
          </div>
          <div>
            <div className="stat-value">{rejectedCount}</div>
            <div className="stat-label">Needs Revision / Rejected</div>
          </div>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Events Table / List */}
      <div className="card" style={{ padding: '1.25rem' }}>
        <h2 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '1rem' }}>
          My Submitted Events & Statuses
        </h2>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '2.5rem', color: '#64748b' }}>
            Loading your events...
          </div>
        ) : events.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem 1rem' }}>
            <p style={{ color: '#64748b', marginBottom: '1rem' }}>
              You haven't submitted any events yet.
            </p>
            <Link to="/artist/submit" className="btn btn-primary">
              <PlusCircle size={16} /> Submit Your First Event
            </Link>
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Event Details</th>
                  <th>Type & Genre</th>
                  <th>Performer / Role</th>
                  <th>Price</th>
                  <th>Submitted On</th>
                  <th>Status</th>
                  <th>Admin Feedback</th>
                </tr>
              </thead>
              <tbody>
                {events.map((evt) => (
                  <tr key={evt.eventId}>
                    <td>
                      <div style={{ fontWeight: 600, color: '#0f172a' }}>{evt.eventName}</div>
                      {evt.venue && <div style={{ fontSize: '0.8rem', color: '#64748b' }}>📍 {evt.venue}</div>}
                    </td>
                    <td>
                      <span className="event-meta-tag">{evt.eventType}</span>
                      <span className="event-meta-tag">{evt.eventGenre}</span>
                    </td>
                    <td>
                      <div>{evt.attendeeName}</div>
                      <div style={{ fontSize: '0.8rem', color: '#64748b' }}>{evt.attendeeRole}</div>
                    </td>
                    <td>
                      <strong>${evt.baseTicketPrice ? Number(evt.baseTicketPrice).toFixed(2) : '0.00'}</strong>
                    </td>
                    <td>
                      <span style={{ fontSize: '0.85rem', color: '#64748b' }}>
                        {evt.submissionDate ? new Date(evt.submissionDate).toLocaleDateString() : 'Today'}
                      </span>
                    </td>
                    <td>
                      <StatusBadge status={evt.approvalStatus} />
                    </td>
                    <td style={{ maxWidth: '240px' }}>
                      {evt.approvalStatus?.toLowerCase() === 'rejected' && evt.rejectionReason ? (
                        <div style={{ fontSize: '0.85rem', color: '#dc2626', display: 'flex', alignItems: 'flex-start', gap: '0.25rem' }}>
                          <AlertCircle size={14} style={{ flexShrink: 0, marginTop: '2px' }} />
                          <span>{evt.rejectionReason}</span>
                        </div>
                      ) : evt.approvalStatus?.toLowerCase() === 'approved' ? (
                        <span style={{ fontSize: '0.85rem', color: '#16a34a' }}>
                          ✓ Live on Ticket Marketplace
                        </span>
                      ) : (
                        <span style={{ fontSize: '0.85rem', color: '#d97706' }}>
                          Awaiting review
                        </span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default ArtistDashboard;
