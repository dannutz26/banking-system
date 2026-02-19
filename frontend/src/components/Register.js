import React, { useState } from 'react';
import axios from 'axios';

const Register = ( {onSwitch} ) => {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/auth/register', {
                firstName,
                lastName,
                email,
                password
            });
            setMessage("User Created! Welcome.");
            console.log(response.data);
        } catch (error) {
            const serverMessage = error.response?.data || "Register Failed.";
            setMessage(serverMessage);
            console.error(error);
        }
    };

    return (
        <div className="register-container">
            <form onSubmit={handleRegister} className="register-card">
                <h2>Create User</h2>
                <div className="form-group">
                    <input
                        type="text"
                        placeholder="First Name"
                        onChange={(e) => setFirstName(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <input
                        type="text"
                        placeholder="Last Name"
                        onChange={(e) => setLastName(e.target.value)}
                        required
                    />
                </div>
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
                <button type="submit" className="register-button">
                    Register
                </button>
                {message && <p className="message">{message}</p>}
                <p className="toggle-text">
                    Already have an account?
                    <button type="button" className="link-button" onClick={onSwitch}>
                        Log in
                    </button>
                </p>
            </form>
        </div>
    );
};

export default Register;