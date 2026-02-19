import React, { useState } from 'react';
import axios from 'axios';

const Login = ({ onSwitch }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                email,
                password
            });
            setMessage("Login Successful! Welcome back.");
            console.log(response.data);
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