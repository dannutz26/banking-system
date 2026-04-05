import React, { useState, useEffect } from 'react';
import axios from 'axios';
import API_BASE_URL from '../config';

const CardsView = ({ email, accounts, onCancel, onLogout }) => {
    const [cards, setCards] = useState([]);
    const [selectedIban, setSelectedIban] = useState(accounts[0]?.iban || '');
    const [cardType, setCardType] = useState('VISA');

    const fetchCards = async () => {
        const res = await axios.get(`${API_BASE_URL}/api/dashboard/cards?email=${email}`);
        setCards(res.data);
    };

    useEffect(() => { fetchCards(); }, []);

    const handleCreateCard = async () => {
        try {
            await axios.post(`${API_BASE_URL}/api/dashboard/cards/create?iban=${selectedIban}&type=${cardType}`);
            fetchCards();
        } catch (err) {
            alert(err.response?.data || "Failed to create card");
        }
    };

    return (
        <div className="dashboard-container">
            <nav className="dashboard-nav">
                <h2 className="brand-name">DAN BANK</h2>
                <div className="nav-actions" style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                    <button className="logout-btn" onClick={onLogout}>Logout</button>
                </div>
            </nav>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px', marginTop: '20px' }}>
                <h2>My Virtual Cards</h2>
                <button className="link-button" onClick={onCancel}>Back to Dashboard</button>
            </div>

            <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                {cards.map(card => (
                    <div key={card.id} className="stat-card" style={{ background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)', color: 'white', minHeight: '180px' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <small>{card.cardType}</small>
                            <small style={{ color: card.blocked ? '#ef4444' : '#10b981' }}>{card.blocked ? 'BLOCKED' : 'ACTIVE'}</small>
                        </div>
                        <h3 style={{ margin: '20px 0', letterSpacing: '2px' }}>
                            {card.cardNumber.replace(/(\d{4})/g, '$1 ').trim()}
                        </h3>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 'auto' }}>
                            <div>
                                <small style={{ opacity: 0.7 }}>Holder</small>
                                <p style={{ fontSize: '0.9rem' }}>{card.cardHolderName}</p>
                            </div>
                            <div>
                                <small style={{ opacity: 0.7 }}>Expires</small>
                                <p style={{ fontSize: '0.9rem' }}>{card.expiryDate.substring(5, 7)}/{card.expiryDate.substring(2, 4)}</p>
                            </div>
                            <div>
                                <small style={{ opacity: 0.7 }}>CVV</small>
                                <p style={{ fontSize: '0.9rem' }}>***</p>
                            </div>
                        </div>
                    </div>
                ))}

                <div className="stat-card" style={{ border: '2px dashed #cbd5e1', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                    <h4 style={{ marginBottom: '10px' }}>Issue New Card</h4>
                    <select className="form-input" value={selectedIban} onChange={e => setSelectedIban(e.target.value)}>
                        {accounts.map(acc => <option key={acc.iban} value={acc.iban}>{acc.iban} ({acc.currency})</option>)}
                    </select>
                    <button className="action-btn" style={{ marginTop: '10px' }} onClick={handleCreateCard}>Generate Card</button>
                </div>
            </div>
        </div>
    );
};

export default CardsView;