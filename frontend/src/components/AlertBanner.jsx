function AlertBanner({ type, message, onClose }) {
    const iconMap = {
        warning: 'bi-exclamation-triangle-fill',
        danger: 'bi-x-circle-fill',
        success: 'bi-check-circle-fill',
        info: 'bi-info-circle-fill'
    }

    return (
        <div className={`alert-card ${type}`}>
            <i className={`bi ${iconMap[type] || iconMap.info}`}></i>
            <div className="flex-grow-1">
                <p className="mb-0">{message}</p>
            </div>
            {onClose && (
                <button className="btn btn-sm" onClick={onClose}>
                    <i className="bi bi-x-lg"></i>
                </button>
            )}
        </div>
    )
}

export default AlertBanner
