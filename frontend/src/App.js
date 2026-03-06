import React, { useState } from 'react';
import Login from './components/Login';
import Register from "./components/Register";
import Dashboard from "./components/Dashboard";

function App() {
    const [view, setView] = useState('login');
    const [user, setUser] = useState(null);

    const handleLoginSuccess = (userData) => {
        setUser(userData);
        setView('dashboard');
    };

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
                    onLogout={() => {
                        setUser(null);
                        setView('login');
                    }}
                />
            )}
        </div>
    );
}

export default App;