import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { userAPI } from '../services/api'

function Login({ onLogin }) {
    const navigate = useNavigate()
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    })
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value })
        setError('')
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true)
        setError('')

        try {
            // Call login API with username and password
            const response = await userAPI.login({
                username: formData.username,
                password: formData.password
            })

            if (response.data.success && response.data.data) {
                onLogin(response.data.data)
                navigate('/')
            } else {
                setError(response.data.message || 'Login failed')
            }
        } catch (err) {
            // Handle specific error messages from backend
            if (err.response?.status === 401) {
                setError(err.response?.data?.message || 'Invalid username or password')
            } else if (err.response?.status === 404) {
                setError('User not found. Please register first.')
            } else {
                setError('Login failed. Please try again.')
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-logo">
                    <i className="bi bi-currency-dollar"></i>
                    <h2>Cashflow</h2>
                    <p className="text-muted">Sign in to your account</p>
                </div>

                {error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Username</label>
                        <input
                            type="text"
                            className="form-control"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            placeholder="Enter your username"
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label className="form-label">Password</label>
                        <input
                            type="password"
                            className="form-control"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Enter your password"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary w-100 mb-3"
                        disabled={loading}
                    >
                        {loading ? (
                            <span className="spinner-border spinner-border-sm me-2"></span>
                        ) : (
                            <i className="bi bi-box-arrow-in-right me-2"></i>
                        )}
                        Sign In
                    </button>
                </form>

                <p className="text-center text-muted mb-0">
                    Don't have an account?{' '}
                    <Link to="/register" className="text-primary text-decoration-none">
                        Register
                    </Link>
                </p>
            </div>
        </div>
    )
}

export default Login
