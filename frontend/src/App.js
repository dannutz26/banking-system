import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Login from './components/Login';
import Register from "./components/Register";
import Dashboard from "./components/Dashboard";
import API_BASE_URL from './config';

axios.defaults.withCredentials = true;

function App() {
    const [view, setView] = useState('login');
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkSession = async () => {
            try {
                const response = await axios.get(`${API_BASE_URL}/api/auth/me`);

                if (response.status === 200) {
                    setUser({ email: response.data });
                    setView('dashboard');
                }
            } catch (err) {
                console.log("No active session found via cookie.");
            } finally {
                setLoading(false);
            }
        };

        checkSession();
    }, []);

    const handleLoginSuccess = (userData) => {
        setUser(userData);
        setView('dashboard');
    };

    const handleLogout = async () => {
        try {
            await axios.post(`${API_BASE_URL}/api/auth/logout`);
        } catch (err) {
            console.error("Logout error:", err);
        } finally {
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