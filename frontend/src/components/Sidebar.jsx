import { NavLink } from 'react-router-dom'

function Sidebar({ user, onLogout }) {
    const navItems = [
        { path: '/', icon: 'bi-grid-1x2-fill', label: 'Dashboard' },
        { path: '/expenses', icon: 'bi-wallet2', label: 'Expenses' },
        { path: '/categories', icon: 'bi-tags-fill', label: 'Categories' },
        { path: '/credits', icon: 'bi-cash-stack', label: 'Credits' },
        { path: '/thresholds', icon: 'bi-bell-fill', label: 'Alerts' }
    ]

    return (
        <aside className="sidebar">
            <div className="sidebar-brand">
                <i className="bi bi-currency-dollar"></i>
                <h4>Cashflow</h4>
            </div>

            <nav>
                <ul className="sidebar-nav">
                    {navItems.map((item) => (
                        <li key={item.path}>
                            <NavLink
                                to={item.path}
                                className={({ isActive }) => isActive ? 'active' : ''}
                            >
                                <i className={`bi ${item.icon}`}></i>
                                <span>{item.label}</span>
                            </NavLink>
                        </li>
                    ))}
                </ul>
            </nav>

            <div style={{
                position: 'absolute',
                bottom: '24px',
                left: '16px',
                right: '16px'
            }}>
                <div className="d-flex align-items-center gap-3 p-3 rounded mb-2"
                    style={{ background: 'rgba(255,255,255,0.05)' }}>
                    <div className="user-avatar">
                        {user?.username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <div className="flex-grow-1" style={{ overflow: 'hidden' }}>
                        <div className="text-white fw-semibold" style={{ fontSize: '14px' }}>
                            {user?.username || 'User'}
                        </div>
                        <div className="text-muted text-truncate" style={{ fontSize: '12px' }}>
                            {user?.email || ''}
                        </div>
                    </div>
                </div>
                <button
                    className="btn btn-outline-danger w-100 d-flex align-items-center justify-content-center gap-2"
                    onClick={onLogout}
                    style={{ padding: '10px' }}
                >
                    <i className="bi bi-box-arrow-right"></i>
                    <span>Logout</span>
                </button>
            </div>
        </aside>
    )
}

export default Sidebar
