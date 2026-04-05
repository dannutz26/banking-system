import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Dashboard.css';
import API_BASE_URL from '../config';

const CreateAccount = ({ email, onCancel, onCreateAccountSuccess, onLogout }) => {
    const [currency, setCurrency] = useState('RON');
    const [availableCurrencies, setAvailableCurrencies] = useState([]);
    const [message, setMessage] = useState('');

    useEffect(() => {
        const fetchCurrencies = async () => {
            try {
                const res = await axios.get(`${API_BASE_URL}/api/dashboard/currencies`);
                setAvailableCurrencies(res.data);
            } catch (err) {
                setAvailableCurrencies([{code: 'EUR', name: 'Euro'}, {code: 'USD', name: 'US Dollar'}]);
            }
        };
        fetchCurrencies();
    }, []);

    const handleCreateAccount = async (e) => {
        e.preventDefault();
        try {
            const payload = { currency, ownerEmail: email };
            await axios.post(`${API_BASE_URL}/api/dashboard/create-account`, payload);
            setMessage("Account created successfully!");
            setTimeout(() => onCreateAccountSuccess(), 1000);
        } catch (error) {
            setMessage("Account creation failed!");
        }
    };

    return (
        <div className="dashboard-container" style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
            <nav className="dashboard-nav">
                <h2 className="brand-name">DAN BANK</h2>
                <div className="nav-actions" style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                    <button className="logout-btn" onClick={onLogout}>Logout</button>
                </div>
            </nav>

            <div style={{ flex: 1, display: 'flex', justifyContent: 'center', padding: '40px 20px' }}>
                <form onSubmit={handleCreateAccount} className="transfer-card" style={{ width: '100%', maxWidth: '500px', height: 'fit-content' }}>
                    <h2 style={{ textAlign: 'center', marginBottom: '1.5rem', color: '#1e293b' }}>Open New Account</h2>

                    <div className="form-group">
                        <label style={{ fontWeight: '600', color: '#475569', display: 'block', marginBottom: '8px' }}>Select Currency</label>
                        <select value={currency} onChange={(e) => setCurrency(e.target.value)} className="form-input" style={{ width: '100%' }}>
                            {availableCurrencies.map(curr => (
                                <option key={curr.code} value={curr.code}>{curr.code} ({curr.name})</option>
                            ))}
                        </select>
                    </div>

                    <button type="submit" className="register-button" style={{ marginTop: '32px', width: '100%' }}>
                        Open Account
                    </button>
                    <button type="button" className="link-button" onClick={onCancel} style={{ marginTop: '16px', width: '100%', background: 'none', border: 'none', color: '#64748b', cursor: 'pointer' }}>
                        Back to Dashboard
                    </button>
                    {message && <p className={`message ${message.includes('failed') ? 'error' : 'success'}`} style={{ textAlign: 'center', marginTop: '15px' }}>{message}</p>}
                </form>
            </div>
        </div>
    );
};

export default CreateAccount;