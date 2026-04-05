import React, { useState, useEffect } from 'react';
import './Dashboard.css';
import CreateAccount from "./CreateAccount";
import TransferMoney from "./TransferMoney";
import Settings from "./Settings";
import DepositMoney from "./DepositMoney";
import CardsView from "./CardsView";
import axios from 'axios';
import API_BASE_URL from '../config';

const Dashboard = ({ email, onLogout }) => {
    const [userData, setUserData] = useState(null);
    const [accounts, setAccounts] = useState([]);
    const [transactions, setTransactions] = useState([]);
    const [view, setView] = useState('main');
    const [loading, setLoading] = useState(true);

    const loadDashboardData = async () => {
        try {
            const userRes = await fetch(`${API_BASE_URL}/api/dashboard/user-data?email=${email}`);
            const userDataJson = await userRes.json();
            setUserData(userDataJson);

            const accountsRes = await fetch(`${API_BASE_URL}/api/dashboard/user-accounts?email=${email}`);
            const accountsDataJson = await accountsRes.json();
            setAccounts(accountsDataJson);

            const transRes = await fetch(`${API_BASE_URL}/api/dashboard/transactions?email=${email}`);
            const transDataJson = await transRes.json();
            setTransactions(transDataJson);

            setLoading(false);
        } catch (err) {
            console.error("Error loading dashboard:", err);
            setLoading(false);
        }
    };

    useEffect(() => {
        if (email) {
            loadDashboardData();
        }
    }, [email]);

    const handleSaveSettings = async (prefCurrency, primaryIban) => {
        try {
            await axios.post(`${API_BASE_URL}/api/settings/update?email=${encodeURIComponent(email)}`, {
                preferredCurrency: prefCurrency,
                primaryAccountIban: primaryIban,
                darkMode: false
            });

            setView('main');
            loadDashboardData();
        } catch (err) {
            console.error("Save error:", err);
            alert("Failed to save settings");
        }
    };

    if (view === 'create') return <CreateAccount email={email} onCancel={() => setView('main')} onCreateAccountSuccess={() => { setView('main'); loadDashboardData(); }} />;
    if (view === 'transfer') return <TransferMoney email={email} accounts={accounts} onCancel={() => setView('main')} onTransferSuccess={() => { setView('main'); loadDashboardData(); }} />;
    if (view === 'settings') return <Settings email={email} accounts={accounts} currentPrefs={{ currency: userData?.preferredCurrency, primaryIban: userData?.primaryIban }} onSave={handleSaveSettings} onCancel={() => setView('main')} onLogout={onLogout}/>;
    if (view === 'deposit') return <DepositMoney email={email} accounts={accounts} onCancel={() => setView('main')} onSuccess={() => { setView('main'); loadDashboardData(); }} />;
    if (view === 'cards') return <CardsView email={email} accounts={accounts} onCancel={() => setView('main')} />;

    if (loading) return <div className="dashboard-container">Connecting to secure servers...</div>;

    return (
        <div className="dashboard-container">
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

            <header className="welcome-section">
                <h1>Hello, {userData?.firstName || 'User'}!</h1>
            </header>

            <div className="stats-grid">
                <div className="stat-card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '20px' }}>
                    <div style={{ flex: 1 }}>
                        <small style={{ color: '#64748b', fontWeight: '500' }}>Total Net Worth</small>
                        <h2 className="balance-amount" style={{ margin: '5px 0' }}>
                            {userData?.totalNetWorth?.toLocaleString('en-US', {
                                style: 'currency',
                                currency: userData?.preferredCurrency || 'EUR'
                            })}
                        </h2>
                        <p style={{ color: '#64748b', fontSize: '0.85rem' }}>
                            {userData?.primaryIban ? `Primary IBAN: ${userData.primaryIban}` : "Primary account not set"}
                        </p>
                    </div>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button className="cards-circle-btn" onClick={() => setView('cards')} title="My Cards">
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <rect x="2" y="5" width="20" height="14" rx="2" ry="2"></rect>
                                <line x1="2" y1="10" x2="22" y2="10"></line>
                            </svg>
                        </button>
                        <button className="deposit-circle-btn" onClick={() => setView('deposit')} title="Deposit Money">+</button>
                    </div>
                </div>

                <div className="stat-card">
                    <h3>Quick Actions</h3>
                    <div className="action-buttons" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                        <button className="action-btn" onClick={() => setView('create')}>New Account</button>
                        <button className="action-btn" onClick={() => setView('transfer')}>Transfer</button>
                    </div>
                </div>
            </div>

            <div className="dashboard-content-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1.2fr', gap: '20px', marginTop: '30px' }}>
                <section className="transactions-section">
                    <h3>Your Accounts</h3>
                    <table className="transaction-table">
                        <thead>
                        <tr>
                            <th>IBAN</th>
                            <th>Currency</th>
                            <th>Balance</th>
                        </tr>
                        </thead>
                        <tbody>
                        {accounts.length > 0 ? (
                            accounts.map((acc) => (
                                <tr key={acc.iban} className={acc.iban === userData?.primaryIban ? "primary-row" : ""}>
                                    <td style={{fontSize: '0.8rem'}}>{acc.iban}</td>
                                    <td>{acc.currency}</td>
                                    <td className="amount-positive">{acc.balance.toFixed(2)}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="3" style={{textAlign: 'center', padding: '20px', color: '#94a3b8'}}>
                                    No accounts opened yet.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </section>

                <section className="transactions-section">
                    <h3>Recent Activity</h3>
                    <div className="history-table-container" style={{ maxHeight: '400px', overflowY: 'auto' }}>
                        <table className="transaction-table">
                            <thead>
                            <tr>
                                <th>Date</th>
                                <th>Description</th>
                                <th>Amount</th>
                            </tr>
                            </thead>
                            <tbody>
                            {transactions.length > 0 ? (
                                transactions.map((tx) => (
                                    <tr key={tx.id}>
                                        <td style={{fontSize: '0.7rem', color: '#64748b'}}>
                                            {new Date(tx.timestamp).toLocaleDateString()}
                                        </td>
                                        <td style={{fontSize: '0.8rem'}}>{tx.description}</td>
                                        <td className={tx.amount < 0 ? "amount-negative" : "amount-positive"}>
                                            {tx.amount > 0 ? `+${tx.amount.toFixed(2)}` : tx.amount.toFixed(2)} {tx.currency}
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="3" style={{textAlign: 'center', padding: '20px', color: '#94a3b8'}}>
                                        No transactions yet.
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
    );
};

export default Dashboard;