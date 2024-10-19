import { useState, useEffect } from 'react'
import { thresholdAPI, categoryAPI } from '../services/api'
import AlertBanner from '../components/AlertBanner'

function Thresholds({ user }) {
    const [thresholds, setThresholds] = useState([])
    const [alerts, setAlerts] = useState([])
    const [categories, setCategories] = useState([])
    const [loading, setLoading] = useState(true)
    const [showModal, setShowModal] = useState(false)
    const [formData, setFormData] = useState({
        limitAmount: '',
        categoryId: '',
        thresholdType: 'MONTHLY',
        alertPercentage: 80
    })

    useEffect(() => {
        loadData()
    }, [user.id])

    const loadData = async () => {
        try {
            const [thresholdsRes, alertsRes, categoriesRes] = await Promise.all([
                thresholdAPI.getByUser(user.id),
                thresholdAPI.getAlerts(user.id),
                categoryAPI.getByUser(user.id)
            ])

            if (thresholdsRes.data.success) {
                setThresholds(thresholdsRes.data.data || [])
            }
            if (alertsRes.data.success) {
                setAlerts(alertsRes.data.data || [])
            }
            if (categoriesRes.data.success) {
                setCategories(categoriesRes.data.data || [])
            }
        } catch (err) {
            console.log('Error loading thresholds:', err)
        } finally {
            setLoading(false)
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        try {
            const response = await thresholdAPI.set({
                userId: user.id,
                limitAmount: parseFloat(formData.limitAmount),
                categoryId: formData.categoryId || null,
                thresholdType: formData.thresholdType,
                alertPercentage: parseInt(formData.alertPercentage)
            })

            if (response.data.success) {
                setShowModal(false)
                setFormData({
                    limitAmount: '',
                    categoryId: '',
                    thresholdType: 'MONTHLY',
                    alertPercentage: 80
                })
                loadData()
            }
        } catch (err) {
            alert('Error setting threshold: ' + (err.response?.data?.message || err.message))
        }
    }

    const handleDelete = async (id) => {
        if (confirm('Delete this threshold?')) {
            try {
                await thresholdAPI.delete(id)
                loadData()
            } catch (err) {
                alert('Error deleting threshold')
            }
        }
    }

    const handleToggle = async (id) => {
        try {
            await thresholdAPI.toggle(id)
            loadData()
        } catch (err) {
            alert('Error toggling threshold')
        }
    }

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '50vh' }}>
                <div className="spinner-border text-primary" role="status"></div>
            </div>
        )
    }

    return (
        <div>
            <div className="top-navbar">
                <h1 className="page-title">Thresholds & Alerts</h1>
                <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                    <i className="bi bi-plus-lg me-2"></i>Set Threshold
                </button>
            </div>

            {/* Active Alerts */}
            {alerts.length > 0 && (
                <div className="mb-4">
                    <h5 className="mb-3">
                        <i className="bi bi-exclamation-triangle text-warning me-2"></i>
                        Active Alerts ({alerts.length})
                    </h5>
                    {alerts.map((alert, index) => (
                        <AlertBanner
                            key={index}
                            type={alert.alertType === 'BREACH' ? 'danger' : 'warning'}
                            message={alert.message}
                        />
                    ))}
                </div>
            )}

            {/* Thresholds List */}
            <div className="row">
                {thresholds.length === 0 ? (
                    <div className="col-12">
                        <div className="card">
                            <div className="card-body text-center py-5 text-muted">
                                <i className="bi bi-bell fs-1 d-block mb-3"></i>
                                <h5>No thresholds set</h5>
                                <p>Set spending limits to get alerts when you're overspending</p>
                            </div>
                        </div>
                    </div>
                ) : (
                    thresholds.map((threshold) => (
                        <div key={threshold.id} className="col-md-6 mb-4">
                            <div className={`card ${threshold.isBreached ? 'border-danger' : ''}`}>
                                <div className="card-body">
                                    <div className="d-flex justify-content-between align-items-start mb-3">
                                        <div>
                                            <h5 className="mb-1">
                                                {threshold.categoryName || 'Overall Budget'}
                                            </h5>
                                            <span className={`badge ${threshold.isActive ? 'bg-success' : 'bg-secondary'}`}>
                                                {threshold.isActive ? 'Active' : 'Inactive'}
                                            </span>
                                            {threshold.isBreached && (
                                                <span className="badge bg-danger ms-2">Breached!</span>
                                            )}
                                        </div>
                                        <div className="btn-group">
                                            <button
                                                className="btn btn-sm btn-outline-primary"
                                                onClick={() => handleToggle(threshold.id)}
                                                title={threshold.isActive ? 'Disable' : 'Enable'}
                                            >
                                                <i className={`bi ${threshold.isActive ? 'bi-pause' : 'bi-play'}`}></i>
                                            </button>
                                            <button
                                                className="btn btn-sm btn-outline-danger"
                                                onClick={() => handleDelete(threshold.id)}
                                            >
                                                <i className="bi bi-trash"></i>
                                            </button>
                                        </div>
                                    </div>

                                    <div className="mb-3">
                                        <div className="d-flex justify-content-between mb-1">
                                            <span>₹{threshold.currentSpending?.toLocaleString() || 0}</span>
                                            <span>₹{threshold.limitAmount?.toLocaleString()}</span>
                                        </div>
                                        <div className="progress" style={{ height: '10px' }}>
                                            <div
                                                className={`progress-bar ${threshold.usagePercentage >= 100 ? 'bg-danger' :
                                                        threshold.usagePercentage >= 80 ? 'bg-warning' : 'bg-success'
                                                    }`}
                                                style={{ width: `${Math.min(threshold.usagePercentage || 0, 100)}%` }}
                                            ></div>
                                        </div>
                                        <div className="text-muted mt-1" style={{ fontSize: '12px' }}>
                                            {threshold.usagePercentage?.toFixed(1)}% used •
                                            {threshold.thresholdType} limit •
                                            Alert at {threshold.alertPercentage}%
                                        </div>
                                    </div>

                                    <div className="d-flex justify-content-between text-muted" style={{ fontSize: '14px' }}>
                                        <span>Remaining:</span>
                                        <span className={`fw-semibold ${threshold.remainingAmount < 0 ? 'text-danger' : 'text-success'}`}>
                                            ₹{threshold.remainingAmount?.toLocaleString() || 0}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* Set Threshold Modal */}
            {showModal && (
                <div className="modal d-block" style={{ background: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Set Spending Threshold</h5>
                                <button className="btn-close" onClick={() => setShowModal(false)}></button>
                            </div>
                            <form onSubmit={handleSubmit}>
                                <div className="modal-body">
                                    <div className="mb-3">
                                        <label className="form-label">Limit Amount *</label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            value={formData.limitAmount}
                                            onChange={(e) => setFormData({ ...formData, limitAmount: e.target.value })}
                                            placeholder="e.g., 5000"
                                            step="0.01"
                                            required
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Category (optional)</label>
                                        <select
                                            className="form-select"
                                            value={formData.categoryId}
                                            onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                                        >
                                            <option value="">Overall Budget (All categories)</option>
                                            {categories.map((cat) => (
                                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                                            ))}
                                        </select>
                                        <div className="form-text">Leave empty for overall spending limit</div>
                                    </div>
                                    <div className="row">
                                        <div className="col-6 mb-3">
                                            <label className="form-label">Period</label>
                                            <select
                                                className="form-select"
                                                value={formData.thresholdType}
                                                onChange={(e) => setFormData({ ...formData, thresholdType: e.target.value })}
                                            >
                                                <option value="DAILY">Daily</option>
                                                <option value="WEEKLY">Weekly</option>
                                                <option value="MONTHLY">Monthly</option>
                                                <option value="YEARLY">Yearly</option>
                                            </select>
                                        </div>
                                        <div className="col-6 mb-3">
                                            <label className="form-label">Alert at %</label>
                                            <input
                                                type="number"
                                                className="form-control"
                                                value={formData.alertPercentage}
                                                onChange={(e) => setFormData({ ...formData, alertPercentage: e.target.value })}
                                                min="50"
                                                max="100"
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        <i className="bi bi-bell me-2"></i>Set Threshold
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default Thresholds
