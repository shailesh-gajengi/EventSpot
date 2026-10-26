import React, { memo } from 'react';
import StatusBadge from './StatusBadge';
import { MapPin, User, Tag, CalendarClock, AlertCircle } from 'lucide-react';

const EventCard = ({ event, showStatus = false, onAction = null, actionLabel = 'Review' }) => {
  return (
    <div className="event-card">
      <div className="event-card-header">
        <div>
          <h3 className="event-title">{event.eventName}</h3>
          <div>
            <span className="event-meta-tag">{event.eventType}</span>
            <span className="event-meta-tag">{event.eventGenre}</span>
          </div>
        </div>
        {showStatus && <StatusBadge status={event.approvalStatus} />}
      </div>

      <div className="event-card-body">
        {event.description && (
          <p className="event-desc">{event.description}</p>
        )}

        <div className="event-detail-item">
          <User size={15} style={{ color: '#6366f1' }} />
          <span>
            <strong>{event.attendeeName}</strong> ({event.attendeeRole})
          </span>
        </div>

        {event.venue && (
          <div className="event-detail-item">
            <MapPin size={15} style={{ color: '#f59e0b' }} />
            <span>{event.venue}</span>
          </div>
        )}

        {event.eventDate && (
          <div className="event-detail-item">
            <CalendarClock size={15} style={{ color: '#10b981' }} />
            <span>{event.eventDate}</span>
          </div>
        )}

        {/* Display Rejection Reason if Rejected */}
        {event.approvalStatus?.toLowerCase() === 'rejected' && event.rejectionReason && (
          <div className="rejection-box">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontWeight: 600 }}>
              <AlertCircle size={14} />
              Rejection Reason from Admin:
            </div>
            <div style={{ marginTop: '0.25rem' }}>{event.rejectionReason}</div>
          </div>
        )}
      </div>

      <div className="event-card-footer">
        <div>
          <span style={{ fontSize: '0.75rem', color: '#64748b', display: 'block' }}>Base Price</span>
          <span className="ticket-price">
            {event.baseTicketPrice ? `$${Number(event.baseTicketPrice).toFixed(2)}` : 'Free'}
          </span>
        </div>

        {onAction ? (
          <button onClick={() => onAction(event)} className="btn btn-secondary btn-sm">
            {actionLabel}
          </button>
        ) : (
          <button
            onClick={() => alert(`Ready for ticket booking for ${event.eventName}! Base Price: $${event.baseTicketPrice || '0.00'}`)}
            className="btn btn-primary btn-sm"
          >
            Book Ticket
          </button>
        )}
      </div>
    </div>
  );
};

export default memo(EventCard);
