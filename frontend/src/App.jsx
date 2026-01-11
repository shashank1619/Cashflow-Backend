import { Routes, Route, Navigate, useSearchParams, useNavigate, useLocation } from 'react-router-dom'
import { useState, useEffect } from 'react'
import Sidebar from './components/Sidebar'
import Dashboard from './pages/Dashboard'
import Expenses from './pages/Expenses'
import Categories from './pages/Categories'
import Credits from './pages/Credits'
import Thresholds from './pages/Thresholds'
import Stats from './pages/Stats'
import AiChat from './components/AiChat'
import Login from './pages/Login'
import Register from './pages/Register'

// OAuth2 Success Handler Component
function OAuth2Success({ onLogin }) {
    const [searchParams] = useSearchParams()
    const navigate = useNavigate()

    useEffect(() => {
        const userId = searchParams.get('userId')
        const username = searchParams.get('username')
        const email = searchParams.get('email')

        if (userId && username && email) {
            const userData = {
                id: parseInt(userId),
                username: username,
                email: email
            }
            onLogin(userData)
            navigate('/')
        } else {
            navigate('/login?error=oauth_failed')
        }
    }, [searchParams, onLogin, navigate])

    return (
        <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
            <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Signing in with Google...</span>
            </div>
        </div>
    )
}

function App() {
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)
    const [theme, setTheme] = useState(localStorage.getItem('cashflow_theme') || 'light')

    useEffect(() => {
        // Check for stored user
        const storedUser = localStorage.getItem('cashflow_user')
        if (storedUser) {
            setUser(JSON.parse(storedUser))
        }
        setLoading(false)
    }, [])

    useEffect(() => {
        document.documentElement.setAttribute('data-theme', theme)
        localStorage.setItem('cashflow_theme', theme)
    }, [theme])

    const toggleTheme = () => {
        setTheme(prev => prev === 'light' ? 'dark' : 'light')
    }

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
                <Route path="/oauth2/callback" element={<OAuth2Success onLogin={handleLogin} />} />
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
                    <Route path="/" element={<Dashboard user={user} theme={theme} onToggleTheme={toggleTheme} />} />
                    <Route path="/expenses" element={<Expenses user={user} theme={theme} onToggleTheme={toggleTheme} />} />
                    <Route path="/categories" element={<Categories user={user} theme={theme} onToggleTheme={toggleTheme} />} />
                    <Route path="/credits" element={<Credits user={user} theme={theme} onToggleTheme={toggleTheme} />} />
                    <Route path="/thresholds" element={<Thresholds user={user} theme={theme} onToggleTheme={toggleTheme} />} />
                    <Route path="/stats" element={<Stats user={user} theme={theme} onToggleTheme={toggleTheme} />} />
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </main>
            <AiChat user={user} />
        </div>
    )
}

export default App
