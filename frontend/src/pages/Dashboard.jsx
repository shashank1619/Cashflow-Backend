import { useState, useEffect } from 'react'
import StatsCard from '../components/StatsCard'
import AlertBanner from '../components/AlertBanner'
import { expenseAPI, thresholdAPI } from '../services/api'

function Dashboard({ user }) {
    const [summary, setSummary] = useState(null)
    const [alerts, setAlerts] = useState([])
    const [recentExpenses, setRecentExpenses] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        loadDashboardData()
    }, [user.id])

    const loadDashboardData = async () => {
        try {
            // Load expense summary
            const summaryRes = await expenseAPI.getSummary(user.id)
            if (summaryRes.data.success) {
                setSummary(summaryRes.data.data)
            }

            // Load alerts
            const alertsRes = await thresholdAPI.getAlerts(user.id)
            if (alertsRes.data.success) {
                setAlerts(alertsRes.data.data || [])
            }

            // Load recent expenses
            const expensesRes = await expenseAPI.getByUser(user.id)
            if (expensesRes.data.success) {
                setRecentExpenses((expensesRes.data.data || []).slice(0, 5))
            }
        } catch (err) {
            console.log('Dashboard data loading error:', err)
            // Set demo data for display
            setSummary({
                totalExpenses: 0,
                totalCredits: 0,
                netBalance: 0,
                expenseCount: 0,
                categoryBreakdown: []
            })
        } finally {
            setLoading(false)
        }
    }

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '50vh' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        )
    }

    return (
        <div>
            <div className="top-navbar">
                <h1 className="page-title">Dashboard</h1>
                <div className="user-info">
                    <span className="text-muted">Welcome back,</span>
                    <span className="fw-semibold">{user.username}</span>
                </div>
            </div>

            {/* Alerts */}
            {alerts.length > 0 && (
                <div className="mb-4">
                    {alerts.map((alert, index) => (
                        <AlertBanner
                            key={index}
                            type={alert.alertType === 'BREACH' ? 'danger' : 'warning'}
                            message={alert.message}
                        />
                    ))}
                </div>
            )}

            {/* Stats Cards */}
            <div className="stats-grid">
                <StatsCard
                    icon="bi-cash-stack"
                    iconClass="success"
                    value={summary?.totalCredits || 0}
                    label="Total Credits"
                    prefix="₹"
                />
                <StatsCard
                    icon="bi-wallet2"
                    iconClass="danger"
                    value={summary?.totalExpenses || 0}
                    label="Total Expenses"
                    prefix="₹"
                />
                <StatsCard
                    icon="bi-graph-up-arrow"
                    iconClass="primary"
                    value={summary?.netBalance || 0}
                    label="Net Balance"
                    prefix="₹"
                />
                <StatsCard
                    icon="bi-receipt"
                    iconClass="warning"
                    value={summary?.expenseCount || 0}
                    label="Transactions"
                />
            </div>

            <div className="row">
                {/* Recent Expenses */}
                <div className="col-lg-8 mb-4">
                    <div className="card">
                        <div className="card-header d-flex justify-content-between align-items-center">
                            <span>Recent Expenses</span>
                            <a href="/expenses" className="btn btn-sm btn-primary">View All</a>
                        </div>
                        <div className="card-body p-0">
                            {recentExpenses.length === 0 ? (
                                <div className="text-center py-5 text-muted">
                                    <i className="bi bi-inbox fs-1 d-block mb-2"></i>
                                    No expenses yet. Start tracking!
                                </div>
                            ) : (
                                <table className="table mb-0">
                                    <thead>
                                        <tr>
                                            <th>Description</th>
                                            <th>Category</th>
                                            <th>Date</th>
                                            <th className="text-end">Amount</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {recentExpenses.map((expense) => (
                                            <tr key={expense.id}>
                                                <td>{expense.description || 'No description'}</td>
                                                <td>
                                                    <span className="badge bg-light text-dark">
                                                        {expense.categoryName}
                                                    </span>
                                                </td>
                                                <td>{expense.expenseDate}</td>
                                                <td className="text-end fw-semibold text-danger">
                                                    -₹{expense.amount?.toLocaleString()}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            )}
                        </div>
                    </div>
                </div>

                {/* Category Breakdown */}
                <div className="col-lg-4 mb-4">
                    <div className="card">
                        <div className="card-header">Category Breakdown</div>
                        <div className="card-body">
                            {(!summary?.categoryBreakdown || summary.categoryBreakdown.length === 0) ? (
                                <div className="text-center py-4 text-muted">
                                    <i className="bi bi-pie-chart fs-1 d-block mb-2"></i>
                                    No data available
                                </div>
                            ) : (
                                <div>
                                    {summary.categoryBreakdown.map((cat, index) => (
                                        <div key={index} className="mb-3">
                                            <div className="d-flex justify-content-between mb-1">
                                                <span>{cat.categoryName}</span>
                                                <span className="fw-semibold">₹{cat.totalAmount?.toLocaleString()}</span>
                                            </div>
                                            <div className="progress" style={{ height: '8px' }}>
                                                <div
                                                    className="progress-bar"
                                                    style={{
                                                        width: `${cat.percentage || 0}%`,
                                                        background: `hsl(${index * 60}, 70%, 50%)`
                                                    }}
                                                ></div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Dashboard
