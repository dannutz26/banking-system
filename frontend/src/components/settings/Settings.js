import React, { useState, useEffect } from 'react';
import axios from 'axios';
import API_BASE_URL from '../../config';

const Settings = ({ email, accounts, currentPrefs, onSave, onCancel, onLogout }) => {
    const [prefCurrency, setPrefCurrency] = useState(currentPrefs.currency || 'EUR');
    const [primaryIban, setPrimaryIban] = useState(currentPrefs.primaryIban || '');
    const [currencies, setCurrencies] = useState([]);

    useEffect(() => {
        const fetchCurrencies = async () => {
            try {
                const res = await axios.get(`${API_BASE_URL}/api/dashboard/currencies`);
                setCurrencies(res.data);
            } catch (err) {
                console.error("Error fetching currencies:", err);
            }
        };
        fetchCurrencies();
    }, []);

    return (
        <div className="dashboard-container" style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
            <nav className="dashboard-nav">
                <h2 className="brand-name">DAN BANK</h2>
                <div className="nav-actions" style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                    <button className="logout-btn" onClick={onLogout}>Logout</button>
                </div>
            </nav>

            <div style={{
                flex: 1,
                display: 'flex',
                justifyContent: 'center',
                padding: '40px 20px'
            }}>
                <div className="transfer-card" style={{
                    width: '100%',
                    maxWidth: '500px',
                    height: 'fit-content'
                }}>
                    <h2 style={{ textAlign: 'center', marginBottom: '1.5rem', color: '#1e293b' }}>Display Settings</h2>

                    <div className="form-group">
                        <label style={{ fontWeight: '600', color: '#475569', display: 'block', marginBottom: '8px' }}>
                            Preferred Currency (Total Wealth)
                        </label>
                        <select
                            className="form-input"
                            value={prefCurrency}
                            onChange={(e) => setPrefCurrency(e.target.value)}
                            style={{ width: '100%' }}
                        >
                            {currencies.length > 0 ? (
                                currencies.map(c => (
                                    <option key={c.code} value={c.code}>{c.code}</option>
                                ))
                            ) : (
                                <option value="EUR">EUR</option>
                            )}
                        </select>
                    </div>

                    <div className="form-group" style={{ marginTop: '20px' }}>
                        <label style={{ fontWeight: '600', color: '#475569', display: 'block', marginBottom: '8px' }}>
                            Primary Account
                        </label>
                        <select
                            className="form-input"
                            value={primaryIban}
                            onChange={(e) => setPrimaryIban(e.target.value)}
                            style={{ width: '100%' }}
                        >
                            <option value="">Select Primary Account</option>
                            {accounts.map(acc => (
                                <option key={acc.iban} value={acc.iban}>{acc.iban} ({acc.currency})</option>
                            ))}
                        </select>
                    </div>

                    <button className="register-button" style={{ marginTop: '32px', width: '100%' }}
                            onClick={() => onSave(prefCurrency, primaryIban)}>
                        Save Preferences
                    </button>
                    <button className="link-button" style={{ marginTop: '16px', width: '100%', background: 'none', border: 'none', color: '#64748b', cursor: 'pointer' }}
                            onClick={onCancel}>
                        Back to Dashboard
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Settings;