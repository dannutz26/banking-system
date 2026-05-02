import React from 'react';

const SpendingGraph = ({ totalSpent, totalLimit, hasLimit }) => {
    const size = 150;
    const strokeWidth = 12;
    const center = size / 2;
    const radius = center - strokeWidth;
    const circumference = 2 * Math.PI * radius;

    const percentage = hasLimit ? Math.min((totalSpent / totalLimit) * 100, 100) : 0;
    const offset = circumference - (percentage / 100) * circumference;

    return (
        <div style={{ textAlign: 'center', padding: '20px', background: 'white', borderRadius: '16px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
            <h4 style={{ color: '#64748b', marginBottom: '15px' }}>Global Monthly Spending</h4>
            <div style={{ position: 'relative', width: size, height: size, margin: '0 auto' }}>
                <svg width={size} height={size}>
                    <circle
                        cx={center} cy={center} r={radius}
                        fill="transparent"
                        stroke="#f1f5f9"
                        strokeWidth={strokeWidth}
                    />
                    <circle
                        cx={center} cy={center} r={radius}
                        fill="transparent"
                        stroke={percentage > 90 ? "#ef4444" : "#3b82f6"}
                        strokeWidth={strokeWidth}
                        strokeDasharray={circumference}
                        strokeDashoffset={hasLimit ? offset : circumference}
                        strokeLinecap="round"
                        style={{ transition: 'stroke-dashoffset 0.5s ease' }}
                        transform={`rotate(-90 ${center} ${center})`}
                    />
                </svg>
                <div style={{
                    position: 'absolute', top: '50%', left: '50%',
                    transform: 'translate(-50%, -50%)', textAlign: 'center'
                }}>
                    <span style={{ fontSize: '1.2rem', fontWeight: 'bold', display: 'block' }}>
                        {totalSpent.toFixed(0)}€
                    </span>
                    <small style={{ color: '#94a3b8', fontSize: '0.7rem' }}>
                        {hasLimit ? `of ${totalLimit}€` : 'No Limit'}
                    </small>
                </div>
            </div>
            {hasLimit && (
                <div style={{ marginTop: '10px', fontSize: '0.85rem', color: '#475569' }}>
                    {Math.max(0, totalLimit - totalSpent).toFixed(2)}€ Remaining
                </div>
            )}
        </div>
    );
};

export default SpendingGraph;