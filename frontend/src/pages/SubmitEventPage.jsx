import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiService } from '../services/api';
import { PlusCircle, Calendar, MapPin, DollarSign, User, FileText } from 'lucide-react';

const EVENT_TYPES = [
  'Concert',
  'Stand-up Comedy',
  'Exhibition',
  'Theatre & Drama',
  'Workshop',
  'Music Festival',
  'Conference',
  'Dance Performance'
];

const EVENT_GENRES = [
  'Rock',
  'Classical',
  'Jazz',
  'Electronic',
  'Stand-up',
  'Fine Arts',
  'Drama',
  'Hip-Hop',
  'Folk & Acoustic',
  'Other'
];

const SubmitEventPage = () => {
  const { user, isArtist } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    eventName: '',
    eventType: EVENT_TYPES[0],
    eventGenre: EVENT_GENRES[0],
    description: '',
    baseTicketPrice: '',
    attendeeName: user?.name || '',
    attendeeRole: 'Lead Performer',
    venue: '',
    eventDate: '',
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Redirect if not logged in as artist
  if (!user || !isArtist) {
    return (
      <div className="card" style={{ maxWidth: '500px', margin: '3rem auto', textAlign: 'center' }}>
        <h3>Artist Access Required</h3>
        <p style={{ color: '#64748b', marginTop: '0.5rem' }}>
          Please log in with an artist account to submit an event for hosting.
        </p>
        <button onClick={() => navigate('/login')} className="btn btn-primary" style={{ marginTop: '1rem' }}>
          Go to Login
        </button>
      </div>
    );
  }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const payload = {
        organizerId: user.userId,
        eventName: formData.eventName.trim(),
        eventType: formData.eventType,
        eventGenre: formData.eventGenre,
        description: formData.description.trim() || null,
        baseTicketPrice: formData.baseTicketPrice ? parseFloat(formData.baseTicketPrice) : 0,
        attendeeName: formData.attendeeName.trim(),
        attendeeRole: formData.attendeeRole.trim(),
        venue: formData.venue.trim() || null,
        eventDate: formData.eventDate.trim() || null,
      };

      const res = await apiService.submitEvent(payload);
      if (res.success) {
        navigate('/artist/dashboard');
      } else {
        setError(res.message || 'Failed to submit event');
      }
    } catch (err) {
      setError(
        err?.response?.data?.details?.[0] ||
        err?.response?.data?.message ||
        err.message ||
        'Error submitting event proposal'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '700px', margin: '1.5rem auto' }}>
      <div className="card">
        <div style={{ marginBottom: '1.5rem' }}>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#0f172a' }}>
            Submit Event for Platform Hosting
          </h2>
          <p style={{ color: '#64748b', fontSize: '0.9rem', marginTop: '0.25rem' }}>
            Fill in your event details. Our admin team will examine the proposal and host it on the website for ticket booking once approved.
          </p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit}>
          {/* Event Name */}
          <div className="form-group">
            <label className="form-label">Event Title / Name *</label>
            <input
              type="text"
              name="eventName"
              required
              className="form-control"
              placeholder="e.g. Electric Symphony Live Concert"
              value={formData.eventName}
              onChange={handleChange}
            />
          </div>

          {/* Event Type & Genre (Dropdown constraints from ER Diagram) */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Event Type (Category) *</label>
              <select
                name="eventType"
                required
                className="form-control"
                value={formData.eventType}
                onChange={handleChange}
              >
                {EVENT_TYPES.map((type) => (
                  <option key={type} value={type}>{type}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Event Genre *</label>
              <select
                name="eventGenre"
                required
                className="form-control"
                value={formData.eventGenre}
                onChange={handleChange}
              >
                {EVENT_GENRES.map((genre) => (
                  <option key={genre} value={genre}>{genre}</option>
                ))}
              </select>
            </div>
          </div>

          {/* Attendee / Performer Info (from ER Diagram) */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Key Performer / Artist Name (Attendee) *</label>
              <input
                type="text"
                name="attendeeName"
                required
                className="form-control"
                placeholder="e.g. John Mayer, DJ Nova, etc."
                value={formData.attendeeName}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Attendee Role *</label>
              <input
                type="text"
                name="attendeeRole"
                required
                className="form-control"
                placeholder="e.g. Lead Vocalist, Stand-up Comic, Guest DJ"
                value={formData.attendeeRole}
                onChange={handleChange}
              />
            </div>
          </div>

          {/* Ticket Price & Date */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Base Ticket Price ($ USD)</label>
              <input
                type="number"
                step="0.01"
                min="0"
                name="baseTicketPrice"
                className="form-control"
                placeholder="e.g. 25.00 (leave blank for free)"
                value={formData.baseTicketPrice}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Event Date & Time</label>
              <input
                type="text"
                name="eventDate"
                className="form-control"
                placeholder="e.g. Nov 15, 2026 - 8:00 PM"
                value={formData.eventDate}
                onChange={handleChange}
              />
            </div>
          </div>

          {/* Venue */}
          <div className="form-group">
            <label className="form-label">Venue / Location</label>
            <input
              type="text"
              name="venue"
              className="form-control"
              placeholder="e.g. Madison Square Garden, New York, NY"
              value={formData.venue}
              onChange={handleChange}
            />
          </div>

          {/* Description */}
          <div className="form-group">
            <label className="form-label">Description / Event Overview</label>
            <textarea
              name="description"
              rows="4"
              className="form-control"
              placeholder="Provide event details, schedule highlights, age restrictions, and what attendees should expect..."
              value={formData.description}
              onChange={handleChange}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate('/artist/dashboard')}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              <PlusCircle size={16} />
              {loading ? 'Submitting Proposal...' : 'Submit Event for Admin Review'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SubmitEventPage;
