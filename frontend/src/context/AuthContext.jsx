import React, { createContext, useContext, useState, useMemo, useCallback } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(() => {
    try {
      const stored = localStorage.getItem('hosting_event_user');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  const loginUser = useCallback((userData) => {
    setCurrentUser(userData);
    localStorage.setItem('hosting_event_user', JSON.stringify(userData));
  }, []);

  const logoutUser = useCallback(() => {
    setCurrentUser(null);
    localStorage.removeItem('hosting_event_user');
  }, []);

  const value = useMemo(() => {
    const isArtist = currentUser?.role === 'ARTIST';
    const isAdmin = currentUser?.role === 'ADMIN';
    return {
      user: currentUser,
      login: loginUser,
      logout: logoutUser,
      isArtist,
      isAdmin,
      isAuthenticated: Boolean(currentUser),
    };
  }, [currentUser, loginUser, logoutUser]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);
