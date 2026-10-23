import React, { useState } from 'react';
import { X, CheckCircle, XCircle, User, Mail, Phone, Calendar, DollarSign, MapPin } from 'lucide-react';
import StatusBadge from './StatusBadge';

const ReviewModal = ({ event, onClose, onApprove, onReject }) => {
  const [rejectionReason, setRejectionReason] = useState(event?.rejectionReason || '');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  if (!event) return null;

  const handleApprove = async () => {
    try {
      setIsSubmitting(true);
      setError('');
      await onApprove(event.eventId);
      onClose();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Failed to approve event');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleReject = async () => {
    if (!rejectionReason.trim()) {
      setError('Please provide a reason for rejecting this event.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError('');
      await onReject(event.eventId, rejectionReason.trim());
      onClose();
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Failed to reject event');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <h2 className="modal-title">Review Event Proposal</h2>
            <div style={{ marginTop: '0.25rem' }}>
              <StatusBadge status={event.approvalStatus} />
            </div>
          </div>
          <button className="close-btn" onClick={onClose} aria-label="Close">
            <X size={20} />
          </button>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {/* Event Details */}
          <div style={{ background: '#f8fafc', padding: '1rem', borderRadius: '0.5rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#0f172a' }}>{event.eventName}</h3>
            <div style={{ marginTop: '0.25rem' }}>
              <span className="event-meta-tag">{event.eventType}</span>
              <span className="event-meta-tag">{event.eventGenre}</span>
            </div>

            {event.description && (
              <p style={{ marginTop: '0.75rem', fontSize: '0.875rem', color: '#475569' }}>
                {event.description}
              </p>
            )}

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', marginTop: '0.75rem', fontSize: '0.85rem' }}>
              <div>
                <strong>Performer / Role:</strong><br/>
                {event.attendeeName} ({event.attendeeRole})
              </div>
              <div>
                <strong>Base Ticket Price:</strong><br/>
                ${event.baseTicketPrice ? Number(event.baseTicketPrice).toFixed(2) : '0.00'}
              </div>
              {event.venue && (
                <div>
                  <strong>Venue:</strong><br/>
                  {event.venue}
                </div>
              )}
              {event.eventDate && (
                <div>
                  <strong>Date:</strong><br/>
                  {event.eventDate}
                </div>
              )}
            </div>
          </div>

          {/* Organizer / Artist Info */}
          <div style={{ background: '#f1f5f9', padding: '0.85rem 1rem', borderRadius: '0.5rem', fontSize: '0.85rem' }}>
            <h4 style={{ fontWeight: 600, color: '#334155', marginBottom: '0.4rem' }}>Artist / Organizer Details</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <User size={14} /> <strong>Name:</strong> {event.organizerName || 'N/A'} (ID: #{event.organizerId})
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <Mail size={14} /> <strong>Email:</strong> {event.organizerEmail || 'N/A'}
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <Phone size={14} /> <strong>Contact:</strong> {event.organizerContact || 'N/A'}
              </div>
            </div>
          </div>

          {/* Rejection input */}
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label">
              Feedback / Rejection Reason (Mandatory if rejecting):
            </label>
            <textarea
              className="form-control"
              rows="3"
              placeholder="e.g. Please specify venue license or adjust pricing structure..."
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
            />
          </div>
        </div>

        <div className="modal-footer">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onClose}
            disabled={isSubmitting}
          >
            Cancel
          </button>

          <button
            type="button"
            className="btn btn-danger"
            onClick={handleReject}
            disabled={isSubmitting}
          >
            <XCircle size={16} />
            Reject Event
          </button>

          <button
            type="button"
            className="btn btn-success"
            onClick={handleApprove}
            disabled={isSubmitting}
          >
            <CheckCircle size={16} />
            Approve & Host Event
          </button>
        </div>
      </div>
    </div>
  );
};

export default ReviewModal;
