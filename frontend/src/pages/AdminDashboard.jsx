import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiService } from '../services/api';
import StatusBadge from '../components/StatusBadge';
import ReviewModal from '../components/ReviewModal';
import { ShieldCheck, Clock, CheckCircle2, XCircle, Users, RefreshCw, Eye } from 'lucide-react';

const AdminDashboard = () => {
  const { user, isAdmin } = useAuth();
  const navigate = useNavigate();

  const [events, setEvents] = useState([]);
  const [stats, setStats] = useState(null);
  const [filter, setFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [selectedEvent, setSelectedEvent] = useState(null);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const [eventsRes, statsRes] = await Promise.all([
        apiService.getAdminEvents(filter),
        apiService.getAdminStats(),
      ]);
      setEvents(eventsRes.data || []);
      setStats(statsRes.data || null);
    } catch (err) {
      setError('Unable to load admin data. Ensure backend is running.');
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    if (!user || !isAdmin) {
      navigate('/login');
      return;
    }
    loadData();
  }, [user, isAdmin, loadData, navigate]);

  const handleApprove = async (eventId) => {
    const res = await apiService.processApproval(eventId, 'Approved');
    if (res.success) {
      setSuccessMsg(`Event #${eventId} has been approved and is now Hosted for tickets!`);
      loadData();
      setTimeout(() => setSuccessMsg(''), 4000);
    }
  };

  const handleReject = async (eventId, rejectionReason) => {
    const res = await apiService.processApproval(eventId, 'Rejected', rejectionReason);
    if (res.success) {
      setSuccessMsg(`Event #${eventId} has been marked as Rejected with feedback sent.`);
      loadData();
      setTimeout(() => setSuccessMsg(''), 4000);
    }
  };

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <ShieldCheck size={28} color="#db2777" />
            <h1 style={{ fontSize: '1.8rem', fontWeight: 800, color: '#0f172a' }}>
              Admin Moderation & Approval Console
            </h1>
          </div>
          <p style={{ color: '#64748b', fontSize: '0.95rem', marginTop: '0.2rem' }}>
            Review, verify, and host event proposals submitted by artists.
          </p>
        </div>

        <button onClick={loadData} className="btn btn-secondary btn-sm">
          <RefreshCw size={14} /> Refresh Queue
        </button>
      </div>

      {/* Metrics Cards */}
      <div className="stat-grid">
        <div className="stat-card">
          <div className="stat-icon warning">
            <Clock size={22} />
          </div>
          <div>
            <div className="stat-value">{stats ? stats.pendingEvents : 0}</div>
            <div className="stat-label">Pending Review (Action Required)</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon success">
            <CheckCircle2 size={22} />
          </div>
          <div>
            <div className="stat-value">{stats ? stats.approvedEvents : 0}</div>
            <div className="stat-label">Approved & Hosted Events</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon danger">
            <XCircle size={22} />
          </div>
          <div>
            <div className="stat-value">{stats ? stats.rejectedEvents : 0}</div>
            <div className="stat-label">Rejected Events</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon primary">
            <Users size={22} />
          </div>
          <div>
            <div className="stat-value">{stats ? stats.totalOrganizers : 0}</div>
            <div className="stat-label">Registered Artists</div>
          </div>
        </div>
      </div>

      {successMsg && <div className="alert alert-success">{successMsg}</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      {/* Filter Tabs */}
      <div className="card" style={{ padding: '1.25rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.75rem' }}>
          <h2 style={{ fontSize: '1.2rem', fontWeight: 700 }}>
            Event Submissions Management
          </h2>

          <div className="tabs" style={{ margin: 0 }}>
            {['ALL', 'PENDING', 'APPROVED', 'REJECTED'].map((st) => (
              <button
                key={st}
                className={`tab-btn ${filter === st ? 'active' : ''}`}
                onClick={() => setFilter(st)}
              >
                {st === 'ALL' ? 'All Submissions' : st.charAt(0) + st.slice(1).toLowerCase()}
              </button>
            ))}
          </div>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
            Loading event records...
          </div>
        ) : events.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem 1rem', color: '#64748b' }}>
            No events found under the <strong>{filter}</strong> category.
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Event Name & Type</th>
                  <th>Artist / Organizer</th>
                  <th>Performer</th>
                  <th>Ticket Price</th>
                  <th>Submitted Date</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {events.map((evt) => (
                  <tr key={evt.eventId}>
                    <td>
                      <div style={{ fontWeight: 600, color: '#0f172a' }}>{evt.eventName}</div>
                      <div>
                        <span className="event-meta-tag">{evt.eventType}</span>
                        <span className="event-meta-tag">{evt.eventGenre}</span>
                      </div>
                    </td>
                    <td>
                      <div style={{ fontWeight: 500 }}>{evt.organizerName || `Artist #${evt.organizerId}`}</div>
                      <div style={{ fontSize: '0.8rem', color: '#64748b' }}>{evt.organizerEmail}</div>
                      <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>📞 {evt.organizerContact}</div>
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
                        {evt.submissionDate ? new Date(evt.submissionDate).toLocaleDateString() : 'N/A'}
                      </span>
                    </td>
                    <td>
                      <StatusBadge status={evt.approvalStatus} />
                    </td>
                    <td>
                      <button
                        onClick={() => setSelectedEvent(evt)}
                        className="btn btn-secondary btn-sm"
                      >
                        <Eye size={13} />
                        Review / Action
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Admin Review Modal */}
      {selectedEvent && (
        <ReviewModal
          event={selectedEvent}
          onClose={() => setSelectedEvent(null)}
          onApprove={handleApprove}
          onReject={handleReject}
        />
      )}
    </div>
  );
};

export default AdminDashboard;
