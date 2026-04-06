import React, { useState, useEffect, useCallback, useRef } from 'react';
import axios from 'axios';
import API_BASE_URL from '../config';

const CardsView = ({ email, accounts, onCancel, onLogout }) => {
    const [cards, setCards] = useState([]);
    const [selectedIban, setSelectedIban] = useState(accounts[0]?.iban || '');
    const [cardType, setCardType] = useState('VISA');
    const [isDisposable, setIsDisposable] = useState(false);
    const [revealedCardId, setRevealedCardId] = useState(null);

    const [activeMenu, setActiveMenu] = useState(null);
    const [showLimitForm, setShowLimitForm] = useState(false);
    const [limitData, setLimitData] = useState({ period: 'DAILY', amount: '', exists: false });
    const menuRef = useRef(null);

    const fetchCards = useCallback(async () => {
        try {
            const res = await axios.get(`${API_BASE_URL}/api/dashboard/cards`, {
                params: { email: email }
            });
            setCards(res.data);
        } catch (err) {
            console.error("Failed to fetch cards:", err);
        }
    }, [email]);

    useEffect(() => {
        if (email) fetchCards();
    }, [email, fetchCards]);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setActiveMenu(null);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const openMenu = async (e, id, type, isRightClick = false) => {
        if (isRightClick) e.preventDefault();

        try {
            const res = await axios.get(`${API_BASE_URL}/api/limits/${type.toLowerCase()}s/${id}`);
            setLimitData(prev => ({ ...prev, exists: res.data && res.data.length > 0 }));
        } catch (err) {
            setLimitData(prev => ({ ...prev, exists: false }));
        }

        setActiveMenu({ id, type, x: e.clientX, y: e.clientY });
    };

    const handleSetLimitClick = () => {
        setShowLimitForm(true);
    };

    const handleSaveLimit = async () => {
        if (!limitData.amount || limitData.amount <= 0) {
            alert("Please enter a valid amount");
            return;
        }

        if (limitData.exists && !window.confirm("A limit already exists. Do you want to modify it?")) return;

        try {
            const { id, type } = activeMenu;
            await axios.post(`${API_BASE_URL}/api/limits/${type.toLowerCase()}s/${id}`, {
                period: limitData.period,
                limitAmount: parseFloat(limitData.amount)
            });
            alert("Spending limit updated successfully!");
            setShowLimitForm(false);
            setActiveMenu(null);
            setLimitData({ period: 'DAILY', amount: '', exists: false });
        } catch (err) {
            alert(err?.response?.data || "Error saving limit");
        }
    };

    const handleRemoveLimit = async () => {
        const { id, type } = activeMenu;
        if (!window.confirm(`Are you sure you want to remove all spending limits for this ${type.toLowerCase()}?`)) return;

        try {
            await axios.delete(`${API_BASE_URL}/api/limits/${type.toLowerCase()}s/${id}`);
            alert("Spending limits removed.");
            setActiveMenu(null);
        } catch (err) {
            alert("Failed to remove limits");
        }
    };

    const handleCreateCard = async () => {
        try {
            const params = new URLSearchParams();
            params.append('iban', selectedIban);
            params.append('type', cardType);
            params.append('disposable', isDisposable);

            await axios.post(`${API_BASE_URL}/api/dashboard/cards/create?${params.toString()}`);
            setIsDisposable(false);
            await fetchCards();
        } catch (err) {
            alert(err?.response?.data || "Failed to create card");
        }
    };

    const handleToggleBlock = async (cardId) => {
        try {
            await axios.post(`${API_BASE_URL}/api/dashboard/cards/toggle-block`, null, {
                params: { cardId: cardId }
            });
            await fetchCards();
        } catch (err) {
            alert("Failed to update card status");
        }
    };

    const handleDeleteCard = async (cardId) => {
        if (!window.confirm("Are you sure you want to cancel this card permanently?")) return;
        try {
            await axios.delete(`${API_BASE_URL}/api/dashboard/cards/delete`, {
                params: { cardId: cardId }
            });
            await fetchCards();
        } catch (err) {
            alert("Failed to cancel card");
        }
    };

    return (
        <div className="dashboard-container" style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', position: 'relative' }}>

            {activeMenu && !showLimitForm && (
                <div
                    ref={menuRef}
                    style={{
                        position: 'fixed', top: activeMenu.y, left: activeMenu.x,
                        background: 'white', border: '1px solid #cbd5e1', borderRadius: '8px',
                        boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)', zIndex: 1000, width: '180px', padding: '4px'
                    }}
                >
                    <div
                        onClick={handleSetLimitClick}
                        style={{ padding: '10px 12px', cursor: 'pointer', fontSize: '13px', borderRadius: '4px', display: 'flex', alignItems: 'center', gap: '8px', transition: 'background 0.2s' }}
                        onMouseEnter={(e) => e.target.style.background = '#f1f5f9'}
                        onMouseLeave={(e) => e.target.style.background = 'transparent'}
                    >
                        Set Spending Limit
                    </div>
                    <div
                        onClick={limitData.exists ? handleRemoveLimit : null}
                        style={{
                            padding: '10px 12px', fontSize: '13px', borderRadius: '4px', display: 'flex', alignItems: 'center', gap: '8px',
                            cursor: limitData.exists ? 'pointer' : 'not-allowed',
                            color: limitData.exists ? '#ef4444' : '#cbd5e1'
                        }}
                        onMouseEnter={(e) => limitData.exists && (e.target.style.background = '#fef2f2')}
                        onMouseLeave={(e) => e.target.style.background = 'transparent'}
                    >
                        Remove Limit
                    </div>
                </div>
            )}

            {showLimitForm && (
                <div style={{ position: 'fixed', inset: 0, background: 'rgba(15, 23, 42, 0.6)', backdropFilter: 'blur(4px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1001 }}>
                    <div style={{ background: 'white', padding: '32px', borderRadius: '16px', width: '380px', boxShadow: '0 25px 50px -12px rgba(0,0,0,0.25)' }}>
                        <h3 style={{ marginBottom: '8px', color: '#1e293b' }}>Configure Limit</h3>
                        <p style={{ fontSize: '13px', color: '#64748b', marginBottom: '24px' }}>Set a maximum spending threshold for this {activeMenu?.type.toLowerCase()}.</p>

                        <label style={{ fontSize: '12px', fontWeight: 'bold', color: '#475569', display: 'block', marginBottom: '6px' }}>Time Period</label>
                        <select className="form-input" value={limitData.period} onChange={e => setLimitData({ ...limitData, period: e.target.value })} style={{ width: '100%', marginBottom: '16px' }}>
                            <option value="DAILY">Daily</option>
                            <option value="WEEKLY">Weekly</option>
                            <option value="MONTHLY">Monthly</option>
                        </select>

                        <label style={{ fontSize: '12px', fontWeight: 'bold', color: '#475569', display: 'block', marginBottom: '6px' }}>Limit Amount</label>
                        <input className="form-input" type="number" value={limitData.amount} onChange={e => setLimitData({ ...limitData, amount: e.target.value })} style={{ width: '100%', marginBottom: '24px' }} placeholder="0.00" />

                        <div style={{ display: 'flex', gap: '12px' }}>
                            <button className="action-btn" onClick={() => { setShowLimitForm(false); setActiveMenu(null); }} style={{ flex: 1, background: '#f1f5f9', color: '#475569' }}>Cancel</button>
                            <button className="action-btn" onClick={handleSaveLimit} style={{ flex: 1 }}>{limitData.exists ? 'Update' : 'Add Limit'}</button>
                        </div>
                    </div>
                </div>
            )}

            <nav className="dashboard-nav">
                <h2 className="brand-name">DAN BANK</h2>
                <div className="nav-actions">
                    <button className="logout-btn" onClick={onLogout}>Logout</button>
                </div>
            </nav>

            <div style={{ flex: 1, padding: '40px 20px' }}>
                <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
                        <h2 style={{ color: '#1e293b' }}>Card Management</h2>
                        <button className="link-button" onClick={onCancel} style={{ cursor: 'pointer', fontWeight: '600' }}>
                            Back to Dashboard
                        </button>
                    </div>

                    <div className="stat-card" style={{ border: '2px dashed #cbd5e1', background: '#f8fafc', marginBottom: '40px', padding: '30px' }}>
                        <h3 style={{ marginBottom: '20px', color: '#475569', textAlign: 'center' }}>Issue New Virtual Card</h3>
                        <div style={{ display: 'flex', gap: '20px', alignItems: 'flex-end', justifyContent: 'center', flexWrap: 'wrap' }}>
                            <div style={{ flex: '1', minWidth: '250px' }}>
                                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748b' }}>Link to Account</label>
                                <select className="form-input" value={selectedIban} onChange={e => setSelectedIban(e.target.value)} style={{ width: '100%', marginTop: '5px' }}>
                                    {accounts.map(acc => (
                                        <option key={acc.iban} value={acc.iban}>{acc.iban} ({acc.currency})</option>
                                    ))}
                                </select>
                            </div>
                            <div style={{ flex: '1', minWidth: '200px' }}>
                                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748b' }}>Card Type</label>
                                <select className="form-input" value={cardType} onChange={e => setCardType(e.target.value)} style={{ width: '100%', marginTop: '5px' }}>
                                    <option value="VISA">Visa</option>
                                    <option value="MASTERCARD">Mastercard</option>
                                </select>
                            </div>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', height: '45px' }}>
                                <input type="checkbox" id="disposable" checked={isDisposable} onChange={(e) => setIsDisposable(e.target.checked)} />
                                <label htmlFor="disposable" style={{ fontSize: '13px', color: '#64748b', cursor: 'pointer' }}>Disposable</label>
                            </div>
                            <button className="action-btn" onClick={handleCreateCard} style={{ height: '45px', padding: '0 30px' }}>Generate Card</button>
                        </div>
                    </div>

                    {accounts.map(account => {
                        const accountCards = cards.filter(c => c.accountId === account.id);

                        return (
                            <div key={account.iban} style={{ marginBottom: '40px' }}>
                                <div style={{ borderBottom: '2px solid #e2e8f0', marginBottom: '20px', paddingBottom: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <div>
                                        <h3 style={{ color: '#334155' }}>Account: {account.iban}</h3>
                                        <small style={{ color: '#64748b' }}>Balance: {account.balance?.toFixed(2) || '0.00'} {account.currency}</small>
                                    </div>
                                    <button
                                        onClick={(e) => openMenu(e, account.id, 'ACCOUNT')}
                                        style={{ background: 'none', border: 'none', fontSize: '24px', cursor: 'pointer', color: '#94a3b8', padding: '5px' }}
                                    >
                                        ⋮
                                    </button>
                                </div>

                                <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '25px' }}>
                                    {accountCards.length > 0 ? (
                                        accountCards.map(card => (
                                            <div
                                                key={card.id}
                                                className="stat-card"
                                                onContextMenu={(e) => openMenu(e, card.id, 'CARD', true)}
                                                style={{
                                                    background: card.blocked ? '#94a3b8' : 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)',
                                                    color: 'white', minHeight: '200px', minWidth: '340px' , display: 'flex', flexDirection: 'column',
                                                    cursor: 'context-menu', transition: 'transform 0.2s'
                                                }}
                                            >
                                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                                                    <div>
                                                        <span style={{ fontSize: '0.8rem', fontWeight: 'bold' }}>{card.cardType}</span>
                                                        {card.disposable && (
                                                            <span style={{ marginLeft: '10px', fontSize: '10px', background: '#3b82f6', padding: '2px 8px', borderRadius: '4px' }}>
                                                                SINGLE-USE
                                                            </span>
                                                        )}
                                                    </div>
                                                    <div style={{ display: 'flex', gap: '8px' }}>
                                                        <button onClick={() => handleToggleBlock(card.id)} style={{ background: 'rgba(255,255,255,0.2)', border: '1px solid white', color: 'white', borderRadius: '4px', padding: '2px 6px', fontSize: '10px', cursor: 'pointer' }}>
                                                            {card.blocked ? 'Unfreeze' : 'Freeze'}
                                                        </button>
                                                        <button onClick={() => handleDeleteCard(card.id)} style={{ background: 'rgba(239, 68, 68, 0.4)', border: '1px solid #ef4444', color: 'white', borderRadius: '4px', padding: '2px 6px', fontSize: '10px', cursor: 'pointer' }}>
                                                            Cancel
                                                        </button>
                                                    </div>
                                                </div>

                                                <h3 style={{ margin: '35px 0', textAlign: 'center', letterSpacing: '3px', fontSize: '1.2rem' }}>
                                                    {card?.cardNumber?.replace(/(\d{4})/g, '$1 ').trim()}
                                                </h3>

                                                <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 'auto', alignItems: 'flex-end' }}>
                                                    <div>
                                                        <small style={{ opacity: 0.7, display: 'block', fontSize: '0.65rem' }}>HOLDER</small>
                                                        <span style={{ fontSize: '0.85rem' }}>{card.cardHolderName}</span>
                                                    </div>
                                                    <div style={{ textAlign: 'center', cursor: 'pointer' }} onClick={() => setRevealedCardId(revealedCardId === card.id ? null : card.id)}>
                                                        <small style={{ opacity: 0.7, display: 'block', fontSize: '0.65rem' }}>CVV</small>
                                                        <span style={{ fontSize: '0.85rem' }}>{revealedCardId === card.id ? card.cvv : '***'}</span>
                                                    </div>
                                                    <div style={{ textAlign: 'right' }}>
                                                        <small style={{ opacity: 0.7, display: 'block', fontSize: '0.65rem' }}>EXPIRES</small>
                                                        <span style={{ fontSize: '0.85rem' }}>{card?.expiryDate?.substring(5, 7)}/{card?.expiryDate?.substring(2, 4)}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        ))
                                    ) : (
                                        <div style={{ padding: '20px', color: '#94a3b8', fontStyle: 'italic' }}>
                                            No cards linked to this account.
                                        </div>
                                    )}
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
};

export default CardsView;