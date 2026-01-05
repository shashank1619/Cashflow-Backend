import { useState, useEffect } from 'react'
import { categoryAPI } from '../services/api'
import TopNavbar from '../components/TopNavbar'

function Categories({ user, theme, onToggleTheme }) {
    const [categories, setCategories] = useState([])
    const [loading, setLoading] = useState(true)
    const [showModal, setShowModal] = useState(false)
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        colorCode: '#6366f1'
    })

    useEffect(() => {
        loadCategories()
    }, [user.id])

    const loadCategories = async () => {
        try {
            const response = await categoryAPI.getByUser(user.id)
            if (response.data.success) {
                setCategories(response.data.data || [])
            }
        } catch (err) {
            console.log('Error loading categories:', err)
        } finally {
            setLoading(false)
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        try {
            const response = await categoryAPI.create({
                ...formData,
                userId: user.id
            })
            if (response.data.success) {
                setShowModal(false)
                setFormData({ name: '', description: '', colorCode: '#6366f1' })
                loadCategories()
            }
        } catch (err) {
            alert('Error creating category: ' + (err.response?.data?.message || err.message))
        }
    }

    const handleDelete = async (id) => {
        if (confirm('Delete this category? Expenses in this category will also be affected.')) {
            try {
                await categoryAPI.delete(id)
                loadCategories()
            } catch (err) {
                alert('Error deleting category')
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
            <TopNavbar title="Categories" user={user} theme={theme} onToggleTheme={onToggleTheme}>
                <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                    <i className="bi bi-plus-lg me-2"></i>Add Category
                </button>
            </TopNavbar>

            <div className="row">
                {categories.length === 0 ? (
                    <div className="col-12">
                        <div className="card">
                            <div className="card-body text-center py-5 text-muted">
                                <i className="bi bi-tags fs-1 d-block mb-3"></i>
                                <h5>No categories yet</h5>
                                <p>Create categories to organize your expenses</p>
                            </div>
                        </div>
                    </div>
                ) : (
                    categories.map((category) => (
                        <div key={category.id} className="col-md-6 col-lg-4 mb-4">
                            <div className="card h-100">
                                <div className="card-body">
                                    <div className="d-flex align-items-start justify-content-between">
                                        <div className="d-flex align-items-center gap-3">
                                            <div
                                                className="category-dot"
                                                style={{
                                                    width: '40px',
                                                    height: '40px',
                                                    borderRadius: '10px',
                                                    background: category.colorCode || '#6366f1'
                                                }}
                                            ></div>
                                            <div>
                                                <h5 className="mb-1">{category.name}</h5>
                                                <p className="text-muted mb-0" style={{ fontSize: '14px' }}>
                                                    {category.description || 'No description'}
                                                </p>
                                            </div>
                                        </div>
                                        {!category.isDefault && (
                                            <button
                                                className="btn btn-sm btn-outline-danger"
                                                onClick={() => handleDelete(category.id)}
                                            >
                                                <i className="bi bi-trash"></i>
                                            </button>
                                        )}
                                    </div>
                                    <hr />
                                    <div className="d-flex justify-content-between text-muted" style={{ fontSize: '14px' }}>
                                        <span>{category.expenseCount || 0} expenses</span>
                                        <span className="fw-semibold">₹{category.totalExpenseAmount?.toLocaleString() || 0}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* Add Category Modal */}
            {showModal && (
                <div className="modal d-block" style={{ background: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Add New Category</h5>
                                <button className="btn-close" onClick={() => setShowModal(false)}></button>
                            </div>
                            <form onSubmit={handleSubmit}>
                                <div className="modal-body">
                                    <div className="mb-3">
                                        <label className="form-label">Category Name *</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={formData.name}
                                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                            placeholder="e.g., Groceries"
                                            required
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Description</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={formData.description}
                                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                            placeholder="What's this category for?"
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Color</label>
                                        <input
                                            type="color"
                                            className="form-control form-control-color"
                                            value={formData.colorCode}
                                            onChange={(e) => setFormData({ ...formData, colorCode: e.target.value })}
                                            style={{ width: '80px', height: '40px' }}
                                        />
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        <i className="bi bi-plus-lg me-2"></i>Create Category
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

export default Categories
