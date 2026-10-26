import React, { useEffect, useState, useMemo, useCallback } from 'react';
import { apiService } from '../services/api';
import EventCard from '../components/EventCard';
import { Search, Sparkles, Filter } from 'lucide-react';

const GENRES = ['ALL', 'Rock', 'Classical', 'Jazz', 'Electronic', 'Stand-up', 'Theatre', 'Folk', 'Other'];

const HomePage = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('ALL');

  const fetchHostedEvents = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const res = await apiService.getHostedEvents();
      setEvents(res.data || []);
    } catch (err) {
      setError('Unable to load hosted events. Ensure the backend and database are running.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchHostedEvents();
  }, [fetchHostedEvents]);

  const filteredEvents = useMemo(() => {
    const query = search.trim().toLowerCase();
    const genreFilter = selectedGenre.toLowerCase();

    return events.filter((evt) => {
      const matchesSearch = !query ||
        (evt.eventName && evt.eventName.toLowerCase().includes(query)) ||
        (evt.attendeeName && evt.attendeeName.toLowerCase().includes(query)) ||
        (evt.venue && evt.venue.toLowerCase().includes(query));

      const matchesGenre = selectedGenre === 'ALL' ||
        (evt.eventGenre && evt.eventGenre.toLowerCase() === genreFilter);

      return matchesSearch && matchesGenre;
    });
  }, [events, search, selectedGenre]);

  return (
    <div>
      {/* Hero Banner */}
      <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: '#eef2ff', color: '#4f46e5', padding: '0.35rem 0.85rem', borderRadius: '9999px', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.75rem' }}>
          <Sparkles size={14} /> Live Hosted Events & Ticket Booking
        </div>
        <h1 style={{ fontSize: '2.4rem', fontWeight: 800, color: '#0f172a', marginBottom: '0.5rem' }}>
          Discover & Book Extraordinary Events
        </h1>
        <p style={{ color: '#64748b', fontSize: '1.05rem', maxWidth: '650px', margin: '0 auto' }}>
          Every event here is curated and verified by our platform admins. Explore concerts, comedy, exhibitions, and book your tickets directly.
        </p>
      </div>

      {/* Filters Bar */}
      <div className="card" style={{ marginBottom: '2rem', padding: '1rem' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flex: '1', minWidth: '240px' }}>
            <Search size={18} color="#94a3b8" />
            <input
              type="text"
              placeholder="Search by event name, artist, or venue..."
              className="form-control"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={{ border: 'none', boxShadow: 'none' }}
            />
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 600, color: '#64748b', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
              <Filter size={14} /> Genre:
            </span>
            <div className="tabs" style={{ margin: 0, border: 'none' }}>
              {GENRES.map((g) => (
                <button
                  key={g}
                  className={`tab-btn ${selectedGenre === g ? 'active' : ''}`}
                  onClick={() => setSelectedGenre(g)}
                  style={{ padding: '0.35rem 0.75rem', fontSize: '0.85rem' }}
                >
                  {g}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Events List */}
      {error && <div className="alert alert-danger">{error}</div>}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
          Loading hosted events...
        </div>
      ) : filteredEvents.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3.5rem 1.5rem' }}>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 600, color: '#334155', marginBottom: '0.5rem' }}>
            No Hosted Events Available Yet
          </h3>
          <p style={{ color: '#64748b', maxWidth: '450px', margin: '0 auto 1.5rem' }}>
            Artists can register and submit their event proposals. Once reviewed and approved by the admin, they will appear here for ticket booking!
          </p>
        </div>
      ) : (
        <div className="events-grid">
          {filteredEvents.map((evt) => (
            <EventCard key={evt.eventId} event={evt} />
          ))}
        </div>
      )}
    </div>
  );
};

export default HomePage;
