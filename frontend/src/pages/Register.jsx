import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { userAPI } from '../services/api'

function Register({ onLogin }) {
    const navigate = useNavigate()
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        firstName: '',
        lastName: ''
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

        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match')
            setLoading(false)
            return
        }

        try {
            const response = await userAPI.register({
                username: formData.username,
                email: formData.email,
                password: formData.password,
                firstName: formData.firstName,
                lastName: formData.lastName
            })

            if (response.data.success && response.data.data) {
                onLogin(response.data.data)
                navigate('/')
            } else {
                setError(response.data.message || 'Registration failed')
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed. Please try again.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="login-container">
            <div className="login-card" style={{ maxWidth: '480px' }}>
                <div className="login-logo">
                    <i className="bi bi-currency-dollar"></i>
                    <h2>Cashflow</h2>
                    <p className="text-muted">Create your account</p>
                </div>

                {error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <label className="form-label">First Name</label>
                            <input
                                type="text"
                                className="form-control"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleChange}
                                placeholder="John"
                            />
                        </div>
                        <div className="col-md-6 mb-3">
                            <label className="form-label">Last Name</label>
                            <input
                                type="text"
                                className="form-control"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleChange}
                                placeholder="Doe"
                            />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Username *</label>
                        <input
                            type="text"
                            className="form-control"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            placeholder="johndoe"
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Email *</label>
                        <input
                            type="email"
                            className="form-control"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="john@example.com"
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Password *</label>
                        <input
                            type="password"
                            className="form-control"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Min 6 characters"
                            required
                            minLength={6}
                        />
                    </div>

                    <div className="mb-4">
                        <label className="form-label">Confirm Password *</label>
                        <input
                            type="password"
                            className="form-control"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            placeholder="Confirm password"
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
                            <i className="bi bi-person-plus me-2"></i>
                        )}
                        Create Account
                    </button>
                </form>

                <p className="text-center text-muted mb-0">
                    Already have an account?{' '}
                    <Link to="/login" className="text-primary text-decoration-none">
                        Sign In
                    </Link>
                </p>
            </div>
        </div>
    )
}

export default Register
