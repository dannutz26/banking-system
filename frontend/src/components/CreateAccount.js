import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Dashboard.css';
import API_BASE_URL from '../config';

const CreateAccount = ({ email, onCancel, onCreateAccountSuccess }) => {
    const [currency, setCurrency] = useState('RON');
    const [availableCurrencies, setAvailableCurrencies] = useState([]); // State for API data
    const [message, setMessage] = useState('');

    // Fetch the list from your new Java endpoint
    useEffect(() => {
        const fetchCurrencies = async () => {
            try {
                const res = await axios.get(`${API_BASE_URL}/api/dashboard/currencies`);
                setAvailableCurrencies(res.data);
            } catch (err) {
                console.error("Failed to load currencies, falling back to defaults");
                setAvailableCurrencies(['RON', 'EUR', 'USD', 'GBP']); // Emergency fallback
            }
        };
        fetchCurrencies();
    }, []);

    const handleCreateAccount = async (e) => {
        e.preventDefault();
        try {
            const payload = {
                iban: "",
                currency: currency,
                ownerEmail: email
            };
            await axios.post(`${API_BASE_URL}/api/dashboard/create-account`, payload);
            setMessage("Account created successfully!");
            setTimeout(() => onCreateAccountSuccess(), 1000);
        } catch (error) {
            setMessage("Account creation failed!");
        }
    };

    return (
        <div className="register-container">
            <form onSubmit={handleCreateAccount} className="register-card">
                <h2>Open New Account</h2>

                <div className="form-group">
                    <label style={{display: 'block', marginBottom: '8px'}}>Select Currency</label>
                    <select
                        value={currency}
                        onChange={(e) => setCurrency(e.target.value)}
                        className="form-input"
                        style={{ width: '100%', padding: '12px', borderRadius: '8px' }}
                    >
                        {availableCurrencies.map(curr => (
                            <option key={curr.code} value={curr.code}>
                                {curr.code} ({curr.name})
                            </option>
                        ))}
                    </select>
                </div>

                <button type="submit" className="register-button">Open Account</button>
                <div style={{ textAlign: 'center', marginTop: '12px' }}>
                    <button type="button" className="link-button" onClick={onCancel} style={{ width: '100%' }}>
                        Go Back
                    </button>
                </div>
                {message && <p className={`message ${message.includes('failed') ? 'error' : 'success'}`}>{message}</p>}
            </form>
        </div>
    );
};

export default CreateAccount;