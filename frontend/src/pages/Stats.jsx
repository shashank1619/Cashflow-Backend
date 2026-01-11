import { useState, useEffect } from 'react'
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    ArcElement,
    Title,
    Tooltip,
    Legend,
    Filler
} from 'chart.js'
import { Line, Doughnut, Bar } from 'react-chartjs-2'
import { statsAPI } from '../services/api'
import TopNavbar from '../components/TopNavbar'

// Register ChartJS components
ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    ArcElement,
    Title,
    Tooltip,
    Legend,
    Filler
)

function Stats({ user, theme, onToggleTheme }) {
    const [loading, setLoading] = useState(true)
    const [monthlyStats, setMonthlyStats] = useState(null)
    const [trends, setTrends] = useState([])
    const [selectedYear, setSelectedYear] = useState(new Date().getFullYear())
    const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1)
    const [selectedCategory, setSelectedCategory] = useState(null) // null = All Categories

    const months = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ]

    useEffect(() => {
        loadData()
    }, [user.id, selectedYear, selectedMonth, selectedCategory])

    const loadData = async () => {
        setLoading(true)
        try {
            const [statsRes, trendsRes] = await Promise.all([
                statsAPI.getMonthlyStats(user.id, selectedYear, selectedMonth),
                statsAPI.getTrends(user.id, 6, selectedCategory)
            ])

            if (statsRes.data.success) {
                setMonthlyStats(statsRes.data.data)
            }
            if (trendsRes.data.success) {
                setTrends(trendsRes.data.data || [])
            }
        } catch (err) {
            console.error('Error loading stats:', err)
        } finally {
            setLoading(false)
        }
    }

    // Chart options with smart scaling
    const lineChartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            tooltip: {
                callbacks: {
                    label: (ctx) => `₹${Math.round(ctx.parsed.y).toLocaleString()}`
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                grace: '10%', // Adds padding above max value
                ticks: {
                    precision: 0,
                    maxTicksLimit: 6, // Limits ticks for cleaner look
                    callback: (val) => {
                        // Format large numbers nicely
                        if (val >= 100000) return `₹${(val / 100000).toFixed(1)}L`
                        if (val >= 1000) return `₹${(val / 1000).toFixed(0)}K`
                        return `₹${val.toLocaleString()}`
                    }
                }
            },
            x: {
                grid: { display: false }
            }
        }
    }

    const doughnutOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { position: 'right', labels: { boxWidth: 12 } },
            tooltip: {
                callbacks: {
                    label: (ctx) => `${ctx.label}: ₹${ctx.parsed.toLocaleString()} (${ctx.raw})`
                }
            }
        },
        cutout: '60%'
    }

    // Prepare chart data
    const trendChartData = {
        labels: trends.map(t => t.monthName),
        datasets: [{
            label: 'Monthly Spending',
            data: trends.map(t => t.totalSpent),
            borderColor: '#6366f1',
            backgroundColor: 'rgba(99, 102, 241, 0.1)',
            fill: true,
            tension: 0.4
        }]
    }

    const categoryChartData = monthlyStats?.categoryBreakdown ? {
        labels: monthlyStats.categoryBreakdown.map(c => c.categoryName),
        datasets: [{
            data: monthlyStats.categoryBreakdown.map(c => c.amount),
            backgroundColor: monthlyStats.categoryBreakdown.map(c => c.color),
            borderWidth: 0
        }]
    } : { labels: [], datasets: [] }

    const dailyChartData = monthlyStats?.dailyBreakdown ? {
        labels: monthlyStats.dailyBreakdown.map(d => d.day),
        datasets: [{
            label: 'Daily Spending',
            data: monthlyStats.dailyBreakdown.map(d => d.amount),
            backgroundColor: 'rgba(34, 197, 94, 0.5)',
            borderColor: '#22c55e',
            borderWidth: 2
        }]
    } : { labels: [], datasets: [] }

    if (loading) {
        return (
            <div>
                <TopNavbar title="Statistics" user={user} theme={theme} onToggleTheme={onToggleTheme} />
                <div className="stats-skeleton">
                    <div className="skeleton-cards">
                        {[1, 2, 3, 4].map(i => (
                            <div key={i} className="skeleton-card pulse"></div>
                        ))}
                    </div>
                    <div className="skeleton-charts">
                        <div className="skeleton-chart pulse"></div>
                        <div className="skeleton-chart pulse"></div>
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div>
            <TopNavbar title="Statistics" user={user} theme={theme} onToggleTheme={onToggleTheme}>
                <select
                    className="form-select form-select-sm"
                    style={{ width: 'auto', minWidth: '160px' }}
                    value={`${selectedYear}-${selectedMonth}`}
                    onChange={(e) => {
                        const [y, m] = e.target.value.split('-')
                        setSelectedYear(parseInt(y))
                        setSelectedMonth(parseInt(m))
                    }}
                >
                    {[0, 1, 2, 3, 4, 5].map(offset => {
                        const d = new Date()
                        d.setMonth(d.getMonth() - offset)
                        return (
                            <option key={offset} value={`${d.getFullYear()}-${d.getMonth() + 1}`}>
                                {months[d.getMonth()]} {d.getFullYear()}
                            </option>
                        )
                    })}
                </select>
            </TopNavbar>

            {/* Stats Cards */}
            <div className="stats-grid mb-4">
                <div className="stat-card">
                    <div className="stat-card-icon primary">
                        <i className="bi bi-wallet2"></i>
                    </div>
                    <div className="stat-card-value">₹{monthlyStats?.totalSpent?.toLocaleString() || 0}</div>
                    <div className="stat-card-label">Total Spent</div>
                </div>
                <div className="stat-card">
                    <div className="stat-card-icon" style={{ background: 'linear-gradient(135deg, #06b6d4, #0891b2)' }}>
                        <i className="bi bi-calendar-day"></i>
                    </div>
                    <div className="stat-card-value">₹{monthlyStats?.avgDaily?.toLocaleString() || 0}</div>
                    <div className="stat-card-label">Daily Average</div>
                </div>
                <div className="stat-card">
                    <div className="stat-card-icon" style={{ background: 'linear-gradient(135deg, #f97316, #ea580c)' }}>
                        <i className="bi bi-trophy"></i>
                    </div>
                    <div className="stat-card-value">{monthlyStats?.topCategoryName || '-'}</div>
                    <div className="stat-card-label">Top Category</div>
                </div>
                <div className="stat-card">
                    <div className={`stat-card-icon ${monthlyStats?.isIncrease ? 'danger' : 'success'}`}>
                        <i className={`bi ${monthlyStats?.isIncrease ? 'bi-arrow-up' : 'bi-arrow-down'}`}></i>
                    </div>
                    <div className={`stat-card-value ${monthlyStats?.isIncrease ? 'text-danger' : 'text-success'}`}>
                        {monthlyStats?.isIncrease ? '+' : ''}{monthlyStats?.changePercentage?.toFixed(1) || 0}%
                    </div>
                    <div className="stat-card-label">vs Last Month</div>
                </div>
            </div>

            {/* Charts Row */}
            <div className="row mb-4">
                <div className="col-lg-8 mb-4 mb-lg-0">
                    <div className="card h-100">
                        <div className="card-body">
                            <div className="d-flex justify-content-between align-items-center mb-4" style={{ gap: '16px' }}>
                                <h5 className="card-title mb-0" style={{ whiteSpace: 'nowrap' }}>
                                    <i className="bi bi-graph-up text-primary me-2"></i>
                                    Monthly Trend
                                </h5>
                                <select
                                    className="form-select form-select-sm"
                                    style={{ width: 'auto', minWidth: '150px' }}
                                    value={selectedCategory || ''}
                                    onChange={(e) => setSelectedCategory(e.target.value ? parseInt(e.target.value) : null)}
                                >
                                    <option value="">All Categories</option>
                                    {monthlyStats?.categoryBreakdown?.map(cat => (
                                        <option key={cat.categoryId} value={cat.categoryId}>
                                            {cat.categoryName}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div style={{ height: '300px' }}>
                                <Line data={trendChartData} options={lineChartOptions} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-lg-4">
                    <div className="card h-100">
                        <div className="card-body">
                            <h5 className="card-title mb-4">
                                <i className="bi bi-pie-chart text-primary me-2"></i>
                                By Category
                            </h5>
                            <div style={{ height: '300px' }}>
                                {monthlyStats?.categoryBreakdown?.length > 0 ? (
                                    <Doughnut data={categoryChartData} options={doughnutOptions} />
                                ) : (
                                    <div className="d-flex align-items-center justify-content-center h-100 text-muted">
                                        No data for this month
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Daily Chart */}
            <div className="card">
                <div className="card-body">
                    <h5 className="card-title mb-4">
                        <i className="bi bi-bar-chart text-primary me-2"></i>
                        Daily Spending
                    </h5>
                    <div style={{ height: '250px' }}>
                        <Bar data={dailyChartData} options={{
                            ...lineChartOptions,
                            scales: {
                                ...lineChartOptions.scales,
                                x: { grid: { display: false } }
                            }
                        }} />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Stats
