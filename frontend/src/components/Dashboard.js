import React, { useState, useEffect } from 'react';
import './Dashboard.css';
import CreateAccount from "./CreateAccount";

const Dashboard = ({ email, onLogout }) => {
    const [userData, setUserData] = useState(null);
    const [accounts, setAccounts] = useState([]);
    const [view, setView] = useState('main');
    const [loading, setLoading] = useState(true);

    const loadDashboardData = async () => {
        try {
            const userRes = await fetch(`http://localhost:8080/api/dashboard/user-data?email=${email}`);
            const userDataJson = await userRes.json();
            setUserData(userDataJson);

            const accountsRes = await fetch(`http://localhost:8080/api/dashboard/user-accounts?email=${email}`);
            const accountsDataJson = await accountsRes.json();
            setAccounts(accountsDataJson);

            setLoading(false);
        } catch (err) {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (email) {
            loadDashboardData();
        }
    }, [email]);

    if (view === 'create') {
        return (
            <CreateAccount
                email={email}
                onCancel={() => setView('main')}
                onCreateAccountSuccess={() => {
                    setView('main'); // Go back
                    loadDashboardData(); // Refresh the list to show the new IBAN!
                }}
            />
        );
    }

    if (loading) return <div className="dashboard-container">Connecting to secure servers...</div>;

    const totalBalance = accounts.reduce((acc, curr) => acc + curr.balance, 0);

    const mainAccount = accounts.length > 0 ? accounts[0] : null;

    return (
        <div className="dashboard-container">
            <nav className="dashboard-nav">
                <h2 className="brand-name">DAN BANK</h2>
                <button className="logout-btn" onClick={onLogout}>Logout</button>
            </nav>

            <header className="welcome-section">
                <h1>Hello, {userData?.firstName || 'User'}!</h1>
                <p />
            </header>

            <div className="stats-grid">
                <div className="stat-card">
                    <small>Total Net Worth</small>
                    <h2 className="balance-amount">
                        {totalBalance.toLocaleString('ro-RO', { style: 'currency', currency: mainAccount?.currency || 'RON' })}
                    </h2>
                    <p style={{color: '#64748b'}}>
                        {mainAccount ? `Primary IBAN: ${mainAccount.iban}` : "No accounts found"}
                    </p>
                </div>

                <div className="stat-card">
                    <h3>Quick Actions</h3>
                    <div className="action-buttons">
                        <button
                            className="action-btn"
                            onClick={() => setView('create')}
                        >
                            New Account
                        </button>
                        <button className="action-btn secondary">Transfer</button>
                    </div>
                </div>
            </div>

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
                            <tr key={acc.iban}>
                                <td>{acc.iban}</td>
                                <td>{acc.currency}</td>
                                <td className="amount-positive">
                                    {acc.balance.toFixed(2)}
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="3" style={{textAlign: 'center'}}>No accounts linked to this profile.</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </section>
        </div>
    );
};

export default Dashboard;