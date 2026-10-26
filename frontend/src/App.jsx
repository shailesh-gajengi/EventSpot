import React, { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';

// Lazy-load route pages for code-splitting and instantaneous first paint
const HomePage = lazy(() => import('./pages/HomePage'));
const LoginPage = lazy(() => import('./pages/LoginPage'));
const RegisterPage = lazy(() => import('./pages/RegisterPage'));
const ArtistDashboard = lazy(() => import('./pages/ArtistDashboard'));
const SubmitEventPage = lazy(() => import('./pages/SubmitEventPage'));
const AdminDashboard = lazy(() => import('./pages/AdminDashboard'));

const PageLoader = () => (
  <div style={{ textAlign: 'center', padding: '4rem 1rem', color: '#64748b' }}>
    <div style={{ fontSize: '1rem', fontWeight: 500 }}>Loading view...</div>
  </div>
);

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="app-container">
          <Navbar />
          <main className="main-content">
            <Suspense fallback={<PageLoader />}>
              <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/artist/dashboard" element={<ArtistDashboard />} />
                <Route path="/artist/submit" element={<SubmitEventPage />} />
                <Route path="/admin/dashboard" element={<AdminDashboard />} />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </Suspense>
          </main>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
