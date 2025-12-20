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

            <div className="sidebar-footer">
                <div className="sidebar-user-info">
                    <div className="user-avatar">
                        {user?.username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <div className="user-details">
                        <div className="user-name">
                            {user?.username || 'User'}
                        </div>
                        <div className="user-email">
                            {user?.email || ''}
                        </div>
                    </div>
                </div>
                <button
                    className="sidebar-logout"
                    onClick={onLogout}
                >
                    <i className="bi bi-box-arrow-right"></i>
                    <span>Logout</span>
                </button>
            </div>
        </aside>
    )
}

export default Sidebar
