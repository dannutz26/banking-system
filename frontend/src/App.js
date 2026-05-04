import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Login from './components/auth/Login';
import Register from "./components/auth/Register";
import Dashboard from "./components/dashboard/Dashboard";
import API_BASE_URL from './config';

const AUTH_TOKEN_KEY = 'BANKING_AUTH_TOKEN';

function setAuthHeader(token) {
    if (token) {
        axios.defaults.headers.common.Authorization = `Bearer ${token}`;
    } else {
        delete axios.defaults.headers.common.Authorization;
    }
}

function App() {
    const [view, setView] = useState('login');
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem(AUTH_TOKEN_KEY);
        if (token) {
            setAuthHeader(token);
        }

        const checkSession = async () => {
            try {
                const response = await axios.get(`${API_BASE_URL}/api/auth/me`);
                if (response.status === 200) {
                    setUser({ email: response.data.email });
                    setView('dashboard');
                }
            } catch (err) {
                localStorage.removeItem(AUTH_TOKEN_KEY);
                setAuthHeader(null);
                setView('login');
            } finally {
                setLoading(false);
            }
        };

        checkSession();
    }, []);

    const handleLoginSuccess = ({ email, token }) => {
        localStorage.setItem(AUTH_TOKEN_KEY, token);
        setAuthHeader(token);
        setUser({ email });
        setView('dashboard');
    };

    const handleLogout = async () => {
        try {
            await axios.post(`${API_BASE_URL}/api/auth/logout`);
        } catch (err) {
            console.error("Logout error:", err);
        } finally {
            localStorage.removeItem(AUTH_TOKEN_KEY);
            setAuthHeader(null);
            setUser(null);
            setView('login');
        }
    };

    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#f1f5f9' }}>
                <div className="loader">Loading Security Session...</div>
            </div>
        );
    }

    return (
        <div className="App">
            {view === 'login' && (
                <Login
                    onSwitch={() => setView('register')}
                    onLoginSuccess={handleLoginSuccess}
                />
            )}

            {view === 'register' && (
                <Register onSwitch={() => setView('login')} />
            )}

            {view === 'dashboard' && user && (
                <Dashboard
                    email={user.email}
                    onLogout={handleLogout}
                />
            )}
        </div>
    );
}

export default App;