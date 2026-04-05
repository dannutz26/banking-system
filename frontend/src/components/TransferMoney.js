import React, { useState } from 'react';
import axios from 'axios';
import './Dashboard.css';
import API_BASE_URL from '../config';

const TransferMoney = ({ email, accounts, onCancel, onTransferSuccess, onLogout }) => {
    const [fromIban, setFromIban] = useState(accounts[0]?.iban || '');
    const [toIban, setToIban] = useState('');
    const [amount, setAmount] = useState('');
    const [isInternal, setIsInternal] = useState(true);
    const [message, setMessage] = useState('');

    const handleTransfer = async (e) => {
        e.preventDefault();
        try {
            const payload = { fromIban, toIban, amount: parseFloat(amount), ownerEmail: email };
            await axios.post(`${API_BASE_URL}/api/dashboard/transfer`, payload);
            setMessage("Transfer Successful!");
            setTimeout(() => onTransferSuccess(), 1500);
        } catch (error) {
            setMessage(error.response?.data || "Transfer Failed.");
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
                <form onSubmit={handleTransfer} className="transfer-card" style={{ width: '100%', maxWidth: '500px', height: 'fit-content' }}>
                    <h2 style={{ textAlign: 'center', marginBottom: '1.5rem', color: '#1e293b' }}>Transfer Funds</h2>

                    <div className="form-group">
                        <label style={{ fontWeight: '600', color: '#475569', display: 'block', marginBottom: '8px' }}>From Account</label>
                        <select value={fromIban} onChange={(e) => setFromIban(e.target.value)} className="form-input" style={{ width: '100%' }}>
                            {accounts.map(acc => (
                                <option key={acc.iban} value={acc.iban}>
                                    {acc.iban.substring(0, 15)}... ({acc.balance.toFixed(2)} {acc.currency})
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group" style={{ display: 'flex', gap: '12px', margin: '20px 0' }}>
                        <button type="button" className={`action-btn ${isInternal ? '' : 'secondary'}`} style={{ flex: 1 }} onClick={() => setIsInternal(true)}>Internal</button>
                        <button type="button" className={`action-btn ${!isInternal ? '' : 'secondary'}`} style={{ flex: 1 }} onClick={() => setIsInternal(false)}>External</button>
                    </div>

                    <div className="form-group">
                        <label style={{ fontWeight: '600', color: '#475569', display: 'block', marginBottom: '8px' }}>To Account</label>
                        {isInternal ? (
                            <select value={toIban} onChange={(e) => setToIban(e.target.value)} className="form-input" style={{ width: '100%' }} required>
                                <option value="">Select Target Account</option>
                                {accounts.filter(acc => acc.iban !== fromIban).map(acc => (
                                    <option key={acc.iban} value={acc.iban}>{acc.iban.substring(0, 15)}... ({acc.currency})</option>
                                ))}
                            </select>
                        ) : (
                            <input type="text" placeholder="IBAN Number" value={toIban} onChange={(e) => setToIban(e.target.value)} className="form-input" style={{ width: '100%' }} required />
                        )}
                    </div>

                    <div className="form-group" style={{ marginTop: '20px' }}>
                        <label style={{ fontWeight: '600', color: '#475569', display: 'block', marginBottom: '8px' }}>Amount</label>
                        <input type="number" min="0.01" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} className="form-input" style={{ width: '100%' }} required />
                    </div>

                    <button type="submit" className="register-button" style={{ marginTop: '32px', width: '100%' }}>Confirm Transfer</button>
                    <button type="button" className="link-button" onClick={onCancel} style={{ marginTop: '16px', width: '100%', background: 'none', border: 'none', color: '#64748b', cursor: 'pointer' }}>
                        Back to Dashboard
                    </button>
                    {message && <p className={`message ${message.includes('Successful') ? 'success' : 'error'}`} style={{ textAlign: 'center', marginTop: '15px' }}>{message}</p>}
                </form>
            </div>
        </div>
    );
};

export default TransferMoney;