import { useState, useRef, useEffect } from 'react'
import { aiAPI } from '../services/api'

function AiChat({ user }) {
    const [isOpen, setIsOpen] = useState(false)
    const [messages, setMessages] = useState([
        { role: 'assistant', content: 'Hi! 👋 I\'m your AI expense assistant. Ask me anything about your spending!' }
    ])
    const [input, setInput] = useState('')
    const [loading, setLoading] = useState(false)
    const messagesEndRef = useRef(null)
    const inputRef = useRef(null)

    useEffect(() => {
        if (isOpen) {
            messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
            inputRef.current?.focus()
        }
    }, [isOpen, messages])

    const handleSend = async () => {
        if (!input.trim() || loading) return

        const userMessage = input.trim()
        setInput('')
        setMessages(prev => [...prev, { role: 'user', content: userMessage }])
        setLoading(true)

        try {
            const response = await aiAPI.chat(user.id, userMessage)
            if (response.data.success) {
                setMessages(prev => [...prev, {
                    role: 'assistant',
                    content: response.data.data.response
                }])
            }
        } catch (err) {
            console.error('AI chat error:', err)
            setMessages(prev => [...prev, {
                role: 'assistant',
                content: 'Sorry, I encountered an error. Please try again.'
            }])
        } finally {
            setLoading(false)
        }
    }

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault()
            handleSend()
        }
    }

    const quickQuestions = [
        "How much did I spend this month?",
        "What's my top spending category?",
        "Give me tips to save money"
    ]

    return (
        <>
            {/* Floating Chat Button */}
            <button
                className={`ai-chat-button ${isOpen ? 'active' : ''}`}
                onClick={() => setIsOpen(!isOpen)}
            >
                {isOpen ? (
                    <i className="bi bi-x-lg"></i>
                ) : (
                    <i className="bi bi-robot"></i>
                )}
            </button>

            {/* Chat Window */}
            <div className={`ai-chat-window ${isOpen ? 'open' : ''}`}>
                <div className="ai-chat-header">
                    <div className="ai-chat-title">
                        <i className="bi bi-robot me-2"></i>
                        AI Assistant
                    </div>
                    <button
                        className="ai-chat-close"
                        onClick={() => setIsOpen(false)}
                    >
                        <i className="bi bi-dash-lg"></i>
                    </button>
                </div>

                <div className="ai-chat-messages">
                    {messages.map((msg, idx) => (
                        <div key={idx} className={`ai-message ${msg.role}`}>
                            {msg.role === 'assistant' && (
                                <div className="ai-message-avatar">
                                    <i className="bi bi-robot"></i>
                                </div>
                            )}
                            <div className="ai-message-content">
                                {msg.content}
                            </div>
                        </div>
                    ))}
                    {loading && (
                        <div className="ai-message assistant">
                            <div className="ai-message-avatar">
                                <i className="bi bi-robot"></i>
                            </div>
                            <div className="ai-message-content typing">
                                <span></span><span></span><span></span>
                            </div>
                        </div>
                    )}
                    <div ref={messagesEndRef} />
                </div>

                {/* Quick Questions */}
                {messages.length === 1 && (
                    <div className="ai-quick-questions">
                        {quickQuestions.map((q, idx) => (
                            <button
                                key={idx}
                                className="ai-quick-btn"
                                onClick={() => {
                                    setInput(q)
                                    inputRef.current?.focus()
                                }}
                            >
                                {q}
                            </button>
                        ))}
                    </div>
                )}

                <div className="ai-chat-input">
                    <input
                        ref={inputRef}
                        type="text"
                        placeholder="Ask about your expenses..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyPress={handleKeyPress}
                        disabled={loading}
                    />
                    <button
                        onClick={handleSend}
                        disabled={!input.trim() || loading}
                    >
                        <i className="bi bi-send-fill"></i>
                    </button>
                </div>
            </div>
        </>
    )
}

export default AiChat
