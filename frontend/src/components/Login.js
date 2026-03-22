import React, { useState } from 'react';
import axios from 'axios';
import API_BASE_URL from '../config';

const Login = ({ onSwitch, onLoginSuccess }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            await axios.post(`${API_BASE_URL}/api/auth/login`, {
                email,
                password
            });

            onLoginSuccess({ email: email });

        } catch (error) {
            setMessage("Login Failed. Check your credentials.");
        }
    };

    return (
        <div className="login-container">
            <form onSubmit={handleLogin} className="login-card">
                <h2>Bank Login</h2>
                <div className="form-group">
                    <input
                        type="email"
                        placeholder="Email"
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <input
                        type="password"
                        placeholder="Password"
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" className="login-button">
                    Login
                </button>
                {message && <p className="message">{message}</p>}
                <p className="toggle-text">
                    Don't have an account?
                    <button type="button" className="link-button" onClick={onSwitch}>
                        Register here
                    </button>
                </p>
            </form>
        </div>
    );
};

export default Login;