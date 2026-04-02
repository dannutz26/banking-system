import React, { useState } from 'react';

const Settings = ({ email, accounts, currentPrefs, onSave, onCancel }) => {
    const [prefCurrency, setPrefCurrency] = useState(currentPrefs.currency || 'EUR');
    const [primaryIban, setPrimaryIban] = useState(currentPrefs.primaryIban || '');

    return (
        <div className="register-container">
            <nav className="dashboard-nav">
                <h2 className="brand-name">DAN BANK</h2>
                <div className="nav-actions" style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                    <button className="settings-icon-btn" onClick={() => setView('settings')} title="Settings">
                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <circle cx="12" cy="12" r="3"></circle>
                            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
                        </svg>
                    </button>
                    <button className="logout-btn" onClick={onLogout}>Logout</button>
                </div>
            </nav>

            <div className="transfer-card">
                <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>Display Settings</h2>

                <div className="form-group">
                    <label style={{ fontWeight: '600', color: '#475569' }}>Preferred Currency (Total Wealth)</label>
                    <select
                        className="form-input"
                        value={prefCurrency}
                        onChange={(e) => setPrefCurrency(e.target.value)}
                    >
                        <option value="EUR">EUR (€)</option>
                        <option value="RON">RON (lei)</option>
                        <option value="USD">USD ($)</option>
                        <option value="YER">YER (﷼)</option>
                    </select>
                </div>

                <div className="form-group" style={{ marginTop: '20px' }}>
                    <label style={{ fontWeight: '600', color: '#475569' }}>Primary Account</label>
                    <select
                        className="form-input"
                        value={primaryIban}
                        onChange={(e) => setPrimaryIban(e.target.value)}
                    >
                        <option value="">Select Primary Account</option>
                        {accounts.map(acc => (
                            <option key={acc.iban} value={acc.iban}>{acc.iban} ({acc.currency})</option>
                        ))}
                    </select>
                </div>

                <button className="register-button" style={{ marginTop: '24px' }}
                        onClick={() => onSave(prefCurrency, primaryIban)}>
                    Save Preferences
                </button>
                <button className="link-button" style={{ marginTop: '12px', width: '100%' }}
                        onClick={onCancel}>
                    Back to Dashboard
                </button>
            </div>
        </div>
    );
};

export default Settings;