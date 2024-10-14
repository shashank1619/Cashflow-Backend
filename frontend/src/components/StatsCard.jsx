function StatsCard({ icon, iconClass, value, label, prefix = '' }) {
    return (
        <div className="stat-card">
            <div className={`stat-card-icon ${iconClass}`}>
                <i className={`bi ${icon}`}></i>
            </div>
            <div className="stat-card-value">
                {prefix}{typeof value === 'number' ? value.toLocaleString() : value}
            </div>
            <div className="stat-card-label">{label}</div>
        </div>
    )
}

export default StatsCard
