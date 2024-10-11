import { Routes, Route, Navigate } from 'react-router-dom'
import { useState, useEffect } from 'react'
import Sidebar from './components/Sidebar'
import Dashboard from './pages/Dashboard'
import Expenses from './pages/Expenses'
import Categories from './pages/Categories'
import Credits from './pages/Credits'
import Thresholds from './pages/Thresholds'
import Login from './pages/Login'
import Register from './pages/Register'

function App() {
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        // Check for stored user
        const storedUser = localStorage.getItem('cashflow_user')
        if (storedUser) {
            setUser(JSON.parse(storedUser))
        }
        setLoading(false)
    }, [])

    const handleLogin = (userData) => {
        setUser(userData)
        localStorage.setItem('cashflow_user', JSON.stringify(userData))
    }

    const handleLogout = () => {
        setUser(null)
        localStorage.removeItem('cashflow_user')
    }

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        )
    }

    // If not logged in, show login/register routes
    if (!user) {
        return (
            <Routes>
                <Route path="/login" element={<Login onLogin={handleLogin} />} />
                <Route path="/register" element={<Register onLogin={handleLogin} />} />
                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        )
    }

    // Logged in - show dashboard
    return (
        <div className="app-container">
            <Sidebar user={user} onLogout={handleLogout} />
            <main className="main-content">
                <Routes>
                    <Route path="/" element={<Dashboard user={user} />} />
                    <Route path="/expenses" element={<Expenses user={user} />} />
                    <Route path="/categories" element={<Categories user={user} />} />
                    <Route path="/credits" element={<Credits user={user} />} />
                    <Route path="/thresholds" element={<Thresholds user={user} />} />
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </main>
        </div>
    )
}

export default App
