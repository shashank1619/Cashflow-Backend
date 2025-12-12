import { useState, useEffect } from 'react'
import { expenseAPI, categoryAPI } from '../services/api'
import ConfirmModal from '../components/ConfirmModal'
import TopNavbar from '../components/TopNavbar'

function Expenses({ user, theme, onToggleTheme }) {
    const [expenses, setExpenses] = useState([])
    const [categories, setCategories] = useState([])
    const [loading, setLoading] = useState(true)
    const [showModal, setShowModal] = useState(false)
    const [showDeleteModal, setShowDeleteModal] = useState(false)
    const [deleteId, setDeleteId] = useState(null)
    const [formData, setFormData] = useState({
        amount: '',
        description: '',
        categoryId: '',
        expenseDate: new Date().toISOString().split('T')[0],
        paymentMethod: 'CASH'
    })

    useEffect(() => {
        loadData()
    }, [user.id])

    const loadData = async () => {
        try {
            const [expensesRes, categoriesRes] = await Promise.all([
                expenseAPI.getByUser(user.id),
                categoryAPI.getByUser(user.id)
            ])

            if (expensesRes.data.success) {
                setExpenses(expensesRes.data.data || [])
            }
            if (categoriesRes.data.success) {
                setCategories(categoriesRes.data.data || [])
            }
        } catch (err) {
            console.log('Error loading expenses:', err)
        } finally {
            setLoading(false)
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        try {
            const response = await expenseAPI.add({
                ...formData,
                userId: user.id,
                amount: parseFloat(formData.amount)
            })

            if (response.data.success) {
                setShowModal(false)
                setFormData({
                    amount: '',
                    description: '',
                    categoryId: '',
                    expenseDate: new Date().toISOString().split('T')[0],
                    paymentMethod: 'CASH'
                })
                loadData()
            }
        } catch (err) {
            alert('Error adding expense: ' + (err.response?.data?.message || err.message))
        }
    }

    const handleDelete = (e, id) => {
        e.preventDefault()
        e.stopPropagation()
        setDeleteId(id)
        setShowDeleteModal(true)
    }

    const confirmDelete = async () => {
        try {
            await expenseAPI.delete(deleteId)
            setShowDeleteModal(false)
            setDeleteId(null)
            loadData()
        } catch (err) {
            console.error('Delete error:', err)
            alert('Error deleting expense: ' + (err.response?.data?.message || err.message))
            setShowDeleteModal(false)
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
            <TopNavbar title="Expenses" user={user} theme={theme} onToggleTheme={onToggleTheme}>
                <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                    <i className="bi bi-plus-lg me-2"></i>Add Expense
                </button>
            </TopNavbar>

            <div className="card">
                <div className="card-body p-0">
                    {expenses.length === 0 ? (
                        <div className="text-center py-5 text-muted">
                            <i className="bi bi-wallet2 fs-1 d-block mb-3"></i>
                            <h5>No expenses yet</h5>
                            <p>Click "Add Expense" to start tracking your spending</p>
                        </div>
                    ) : (
                        <table className="table mb-0">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Category</th>
                                    <th>Payment</th>
                                    <th className="text-end">Amount</th>
                                    <th className="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {expenses.map((expense) => (
                                    <tr key={expense.id}>
                                        <td>{expense.expenseDate}</td>
                                        <td>{expense.description || '-'}</td>
                                        <td>
                                            <span className="badge bg-light text-dark">
                                                {expense.categoryName}
                                            </span>
                                        </td>
                                        <td>{expense.paymentMethod || '-'}</td>
                                        <td className="text-end fw-semibold text-danger">
                                            -₹{expense.amount?.toLocaleString()}
                                        </td>
                                        <td className="text-end">
                                            <button
                                                type="button"
                                                className="btn btn-sm btn-outline-danger"
                                                onClick={(e) => handleDelete(e, expense.id)}
                                            >
                                                <i className="bi bi-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>

            {/* Add Expense Modal */}
            {showModal && (
                <div className="modal d-block" style={{ background: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Add New Expense</h5>
                                <button className="btn-close" onClick={() => setShowModal(false)}></button>
                            </div>
                            <form onSubmit={handleSubmit}>
                                <div className="modal-body">
                                    <div className="mb-3">
                                        <label className="form-label">Amount *</label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            value={formData.amount}
                                            onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                                            placeholder="0.00"
                                            step="0.01"
                                            required
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Category *</label>
                                        <select
                                            className="form-select"
                                            value={formData.categoryId}
                                            onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                                            required
                                        >
                                            <option value="">Select category</option>
                                            {categories.map((cat) => (
                                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Description</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={formData.description}
                                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                            placeholder="What was this expense for?"
                                        />
                                    </div>
                                    <div className="row">
                                        <div className="col-6 mb-3">
                                            <label className="form-label">Date</label>
                                            <input
                                                type="date"
                                                className="form-control"
                                                value={formData.expenseDate}
                                                onChange={(e) => setFormData({ ...formData, expenseDate: e.target.value })}
                                            />
                                        </div>
                                        <div className="col-6 mb-3">
                                            <label className="form-label">Payment Method</label>
                                            <select
                                                className="form-select"
                                                value={formData.paymentMethod}
                                                onChange={(e) => setFormData({ ...formData, paymentMethod: e.target.value })}
                                            >
                                                <option value="CASH">Cash</option>
                                                <option value="CARD">Card</option>
                                                <option value="UPI">UPI</option>
                                                <option value="BANK">Bank Transfer</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        <i className="bi bi-plus-lg me-2"></i>Add Expense
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}

            {/* Delete Confirmation Modal */}
            <ConfirmModal
                isOpen={showDeleteModal}
                title="Delete Expense"
                message="Are you sure you want to delete this expense? This action cannot be undone."
                onConfirm={confirmDelete}
                onCancel={() => setShowDeleteModal(false)}
            />
        </div>
    )
}

export default Expenses
