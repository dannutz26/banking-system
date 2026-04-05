import React, { useState } from 'react';
import axios from 'axios';
import API_BASE_URL from '../config';

const Login = ({ onSwitch, onLoginSuccess }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [rememberMe, setRememberMe] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(
                `${API_BASE_URL}/api/auth/login?rememberMe=${rememberMe}`,
                { email, password }
            );

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

                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    marginBottom: '20px',
                    justifyContent: 'flex-start',
                    width: '100%',
                    padding: '0 5px'
                }}>
                    <input
                        type="checkbox"
                        id="rememberMe"
                        checked={rememberMe}
                        onChange={(e) => setRememberMe(e.target.checked)}
                        style={{ cursor: 'pointer', width: '16px', height: '16px' }}
                    />
                    <label
                        htmlFor="rememberMe"
                        style={{ fontSize: '14px', color: '#64748b', cursor: 'pointer', userSelect: 'none' }}
                    >
                        Remember me
                    </label>
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