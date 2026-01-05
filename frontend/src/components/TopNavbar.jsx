import React from 'react'

function TopNavbar({ title, user, theme, onToggleTheme, children }) {
    return (
        <div className="top-navbar">
            <h1 className="page-title">{title}</h1>
            <div className="d-flex align-items-center gap-3">
                {children}
                <div className="user-info d-flex align-items-center gap-2">
                    <div className="d-none d-sm-block">
                        <span className="text-muted me-1">Welcome back,</span>
                        <span className="fw-semibold">{user?.username}</span>
                    </div>
                    <button
                        className="btn btn-link theme-toggle-btn p-0 ms-2"
                        onClick={onToggleTheme}
                        style={{ fontSize: '1.25rem', color: 'var(--gray-600)', textDecoration: 'none' }}
                        title={theme === 'light' ? 'Switch to Dark Mode' : 'Switch to Light Mode'}
                    >
                        <i className={`bi ${theme === 'light' ? 'bi-moon-stars-fill' : 'bi-sun-fill'}`}></i>
                    </button>
                </div>
            </div>
        </div>
    )
}

export default TopNavbar
