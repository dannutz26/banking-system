import React, { useState } from 'react';
import axios from 'axios';
import API_BASE_URL from '../config';

const DepositMoney = ({ email, accounts, onCancel, onSuccess, onLogout }) => {
    const [amount, setAmount] = useState('');
    const [selectedIban, setSelectedIban] = useState(accounts[0]?.iban || '');

    const handleDeposit = async () => {
        if (!amount || amount <= 0) return alert("Enter a valid amount");
        try {
            await axios.post(`${API_BASE_URL}/api/dashboard/deposit`, {
                iban: selectedIban,
                amount: parseFloat(amount),
                email: email
            });
            onSuccess();
        } catch (err) {
            alert("Deposit failed");
        }
    };

    return (
        <div className="dashboard-container">
            <nav className="dashboard-nav">
                <h2 className="brand-name">DAN BANK</h2>
                <div className="nav-actions">
                    <button className="logout-btn" onClick={onLogout}>Logout</button>
                </div>
            </nav>

            <div style={{display:'flex', justifyContent:'center', paddingTop:'60px'}}>
                <div className="transfer-card" style={{maxWidth:'400px', width:'100%'}}>
                    <h2 style={{textAlign:'center', marginBottom:'20px'}}>Deposit Funds</h2>
                    <div className="form-group">
                        <label>Target Account</label>
                        <select className="form-input" value={selectedIban} onChange={e => setSelectedIban(e.target.value)}>
                            {accounts.map(acc => (
                                <option key={acc.iban} value={acc.iban}>{acc.iban} ({acc.currency})</option>
                            ))}
                        </select>
                    </div>
                    <div className="form-group" style={{marginTop:'15px'}}>
                        <label>Amount</label>
                        <input className="form-input" type="number" value={amount} onChange={e => setAmount(e.target.value)} placeholder="0.00" />
                    </div>
                    <button className="register-button" style={{marginTop:'20px', background:'#2563eb'}} onClick={handleDeposit}>Confirm Deposit</button>
                    <button className="link-button" style={{width:'100%', marginTop:'10px'}} onClick={onCancel}>Cancel</button>
                </div>
            </div>
        </div>
    );
};

export default DepositMoney;