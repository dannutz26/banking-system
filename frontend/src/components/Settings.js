import React, { useState } from 'react';

const Settings = ({ email, accounts, currentPrefs, onSave, onCancel }) => {
    const [prefCurrency, setPrefCurrency] = useState(currentPrefs.currency || 'EUR');
    const [primaryIban, setPrimaryIban] = useState(currentPrefs.primaryIban || '');

    return (
        <div className="register-container">
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