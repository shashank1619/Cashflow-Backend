import { useState, useEffect } from 'react'
import { creditAPI } from '../services/api'

function Credits({ user }) {
    const [credits, setCredits] = useState([])
    const [totalCredits, setTotalCredits] = useState(0)
    const [loading, setLoading] = useState(true)
    const [showModal, setShowModal] = useState(false)
    const [formData, setFormData] = useState({
        amount: '',
        source: '',
        description: '',
        creditDate: new Date().toISOString().split('T')[0],
        creditType: 'SALARY'
    })

    useEffect(() => {
        loadData()
    }, [user.id])

    const loadData = async () => {
        try {
            const [creditsRes, totalRes] = await Promise.all([
                creditAPI.getByUser(user.id),
                creditAPI.getTotal(user.id)
            ])

            if (creditsRes.data.success) {
                setCredits(creditsRes.data.data || [])
            }
            if (totalRes.data.success) {
                setTotalCredits(totalRes.data.data || 0)
            }
        } catch (err) {
            console.log('Error loading credits:', err)
        } finally {
            setLoading(false)
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        try {
            const response = await creditAPI.add({
                ...formData,
                userId: user.id,
                amount: parseFloat(formData.amount)
            })

            if (response.data.success) {
                setShowModal(false)
                setFormData({
                    amount: '',
                    source: '',
                    description: '',
                    creditDate: new Date().toISOString().split('T')[0],
                    creditType: 'SALARY'
                })
                loadData()
            }
        } catch (err) {
            alert('Error adding credit: ' + (err.response?.data?.message || err.message))
        }
    }

    const handleDelete = async (id) => {
        if (confirm('Are you sure you want to delete this credit?')) {
            try {
                await creditAPI.delete(id)
                loadData()
            } catch (err) {
                alert('Error deleting credit')
            }
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
                <h1 className="page-title">Credits & Income</h1>
                <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                    <i className="bi bi-plus-lg me-2"></i>Add Credit
                </button>
            </div>

            {/* Total Credits Card */}
            <div className="card mb-4">
                <div className="card-body d-flex align-items-center gap-4">
                    <div className="stat-card-icon success" style={{ width: '64px', height: '64px', fontSize: '28px' }}>
                        <i className="bi bi-cash-stack"></i>
                    </div>
                    <div>
                        <div className="stat-card-value">₹{totalCredits.toLocaleString()}</div>
                        <div className="stat-card-label">Total Credits</div>
                    </div>
                </div>
            </div>

            <div className="card">
                <div className="card-body p-0">
                    {credits.length === 0 ? (
                        <div className="text-center py-5 text-muted">
                            <i className="bi bi-cash-coin fs-1 d-block mb-3"></i>
                            <h5>No credits yet</h5>
                            <p>Click "Add Credit" to record your income</p>
                        </div>
                    ) : (
                        <table className="table mb-0">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Source</th>
                                    <th>Description</th>
                                    <th>Type</th>
                                    <th className="text-end">Amount</th>
                                    <th className="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {credits.map((credit) => (
                                    <tr key={credit.id}>
                                        <td>{credit.creditDate}</td>
                                        <td className="fw-semibold">{credit.source}</td>
                                        <td>{credit.description || '-'}</td>
                                        <td>
                                            <span className="badge bg-success bg-opacity-10 text-success">
                                                {credit.creditType}
                                            </span>
                                        </td>
                                        <td className="text-end fw-semibold text-success">
                                            +₹{credit.amount?.toLocaleString()}
                                        </td>
                                        <td className="text-end">
                                            <button
                                                className="btn btn-sm btn-outline-danger"
                                                onClick={() => handleDelete(credit.id)}
                                            >
                                                <i className="bi bi-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>

            {/* Add Credit Modal */}
            {showModal && (
                <div className="modal d-block" style={{ background: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Add New Credit</h5>
                                <button className="btn-close" onClick={() => setShowModal(false)}></button>
                            </div>
                            <form onSubmit={handleSubmit}>
                                <div className="modal-body">
                                    <div className="mb-3">
                                        <label className="form-label">Amount *</label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            value={formData.amount}
                                            onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                                            placeholder="0.00"
                                            step="0.01"
                                            required
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Source *</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={formData.source}
                                            onChange={(e) => setFormData({ ...formData, source: e.target.value })}
                                            placeholder="e.g., Company Name"
                                            required
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Type</label>
                                        <select
                                            className="form-select"
                                            value={formData.creditType}
                                            onChange={(e) => setFormData({ ...formData, creditType: e.target.value })}
                                        >
                                            <option value="SALARY">Salary</option>
                                            <option value="BONUS">Bonus</option>
                                            <option value="INVESTMENT">Investment</option>
                                            <option value="REFUND">Refund</option>
                                            <option value="OTHER">Other</option>
                                        </select>
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Description</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={formData.description}
                                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                            placeholder="Optional description"
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Date</label>
                                        <input
                                            type="date"
                                            className="form-control"
                                            value={formData.creditDate}
                                            onChange={(e) => setFormData({ ...formData, creditDate: e.target.value })}
                                        />
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-success">
                                        <i className="bi bi-plus-lg me-2"></i>Add Credit
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

export default Credits
