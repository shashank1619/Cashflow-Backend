import { useNavigate } from 'react-router-dom'

function StatsCard({ icon, iconClass, value, label, prefix = '', link }) {
    const navigate = useNavigate()

    const handleClick = () => {
        if (link) {
            navigate(link)
        }
    }

    return (
        <div
            className={`stat-card ${link ? 'clickable' : ''}`}
            onClick={handleClick}
            style={link ? { cursor: 'pointer' } : {}}
        >
            <div className={`stat-card-icon ${iconClass}`}>
                <i className={`bi ${icon}`}></i>
            </div>
            <div className="stat-card-value">
                {prefix}{typeof value === 'number' ? value.toLocaleString() : value}
            </div>
            <div className="stat-card-label">{label}</div>
            {link && (
                <div className="stat-card-link">
                    <i className="bi bi-arrow-right"></i>
                </div>
            )}
        </div>
    )
}

export default StatsCard
