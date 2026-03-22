import React, { useState } from 'react';
import axios from 'axios';
import './Dashboard.css';
import API_BASE_URL from '../config';

const TransferMoney = ({ email, accounts, onCancel, onTransferSuccess }) => {
    const [fromIban, setFromIban] = useState(accounts[0]?.iban || '');
    const [toIban, setToIban] = useState('');
    const [amount, setAmount] = useState('');
    const [isInternal, setIsInternal] = useState(true);
    const [message, setMessage] = useState('');

    const handleTransfer = async (e) => {
        e.preventDefault();
        try {
            const payload = {
                fromIban,
                toIban,
                amount: parseFloat(amount),
                ownerEmail: email
            };

            await axios.post(`${API_BASE_URL}/api/dashboard/transfer`, payload);
            setMessage("Transfer Successful!");
            setTimeout(() => onTransferSuccess(), 1500);
        } catch (error) {
            setMessage(error.response?.data || "Transfer Failed.");
        }
    };

    return (
        <div className="register-container">
            <form onSubmit={handleTransfer} className="transfer-card">
                <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>Transfer Funds</h2>

                <div className="form-group">
                    <label style={{ fontWeight: '600', color: '#475569' }}>From Account</label>
                    <select
                        value={fromIban}
                        onChange={(e) => setFromIban(e.target.value)}
                        className="form-input"
                    >
                        {accounts.map(acc => (
                            <option key={acc.iban} value={acc.iban}>
                                {acc.iban.match(/.{1,4}/g).join(' ')} ({acc.balance.toFixed(2)} {acc.currency})
                            </option>
                        ))}
                    </select>
                </div>

                <div className="form-group" style={{ display: 'flex', gap: '12px', margin: '20px 0' }}>
                    <button type="button"
                            className={`action-btn ${isInternal ? '' : 'secondary'}`}
                            style={{ flex: 1, padding: '10px' }}
                            onClick={() => setIsInternal(true)}>Internal</button>
                    <button type="button"
                            className={`action-btn ${!isInternal ? '' : 'secondary'}`}
                            style={{ flex: 1, padding: '10px' }}
                            onClick={() => setIsInternal(false)}>External</button>
                </div>

                <div className="form-group">
                    <label style={{ fontWeight: '600', color: '#475569' }}>To Account</label>
                    {isInternal ? (
                        <select value={toIban} onChange={(e) => setToIban(e.target.value)} className="form-input" required>
                            <option value="">Select Target Account</option>
                            {accounts.filter(acc => acc.iban !== fromIban).map(acc => (
                                <option key={acc.iban} value={acc.iban}>
                                    {acc.iban.match(/.{1,4}/g).join(' ')} ({acc.currency})
                                </option>
                            ))}
                        </select>
                    ) : (
                        <input
                            type="text"
                            placeholder="RO00 BANK 0000..."
                            value={toIban}
                            onChange={(e) => setToIban(e.target.value)}
                            className="form-input"
                            required
                        />
                    )}
                </div>

                <div className="form-group">
                    <label style={{ fontWeight: '600', color: '#475569' }}>Amount</label>
                    <input
                        type="number"
                        min="0.01"
                        step="0.01"
                        placeholder="0.00"
                        value={amount}
                        onChange={(e) => {
                            const val = parseFloat(e.target.value);
                            if (val < 0) setAmount(0);
                            else setAmount(e.target.value);
                        }}
                        className="form-input amount-input"
                        required
                    />
                </div>

                <button type="submit" className="register-button" style={{ marginTop: '24px' }}>
                    Confirm Transfer
                </button>

                <button type="button" className="link-button" onClick={onCancel} style={{ marginTop: '12px', width: '100%' }}>
                    Back to Dashboard
                </button>

                {message && <p className={`message ${message.includes('Successful') ? 'success' : 'error'}`}>{message}</p>}
            </form>
        </div>
    );
};

export default TransferMoney;