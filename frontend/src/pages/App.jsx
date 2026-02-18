import React, { useState } from 'react'
import DoctorList from '../components/DoctorList'
import MyAppointments from '../components/MyAppointments'
import AdminDashboard from '../components/AdminDashboard'
import { login, register } from '../services/api'

const App = () => {
  const [mode, setMode] = useState('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [role, setRole] = useState(null)
  const [patientView, setPatientView] = useState('book')
  const [error, setError] = useState(null)
  const [info, setInfo] = useState(null)

  const resetMessages = () => {
    setError(null)
    setInfo(null)
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    resetMessages()
    try {
      const res = await login(username, password)
      setRole(res.role)
    } catch (err) {
      setError(err?.response?.data?.message || 'Login failed')
    }
  }

  const handleSignup = async (e) => {
    e.preventDefault()
    resetMessages()
    try {
      await register(username, fullName, password)
      setInfo('Signup successful. You can now log in.')
      setMode('login')
    } catch (err) {
      setError(err?.response?.data?.message || 'Signup failed')
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    setRole(null)
    setUsername('')
    setPassword('')
    setFullName('')
    resetMessages()
  }

  const showLogin = !role && mode === 'login'
  const showSignup = !role && mode === 'signup'

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>MediConnect</h1>
      </header>

      {showLogin && (
        <form className="login-form" onSubmit={handleLogin}>
          <h2>Login</h2>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="submit">Login</button>
          {error && <p className="error-text">{error}</p>}
          {info && <p className="helper-text">{info}</p>}
          <p className="helper-text">Default admin: admin / admin123</p>
          <p className="helper-text">
            Don&apos;t have an account?{' '}
            <button type="button" onClick={() => { setMode('signup'); resetMessages() }}>
              Sign up
            </button>
          </p>
        </form>
      )}

      {showSignup && (
        <form className="login-form" onSubmit={handleSignup}>
          <h2>Sign up</h2>
          <input
            type="text"
            placeholder="Full name"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
          />
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="submit">Sign up</button>
          {error && <p className="error-text">{error}</p>}
          <p className="helper-text">
            Already have an account?{' '}
            <button type="button" onClick={() => { setMode('login'); resetMessages() }}>
              Back to login
            </button>
          </p>
        </form>
      )}

      {role === 'PATIENT' && (
        <section className="content-section">
          <div className="toolbar">
            <span>Logged in as PATIENT</span>
            <div className="toolbar-actions">
              <button type="button" onClick={() => setPatientView('book')}>
                Book Appointment
              </button>
              <button type="button" onClick={() => setPatientView('myAppointments')}>
                My Appointments
              </button>
              <button type="button" onClick={handleLogout}>
                Logout
              </button>
            </div>
          </div>
          {patientView === 'book' ? <DoctorList /> : <MyAppointments />}
        </section>
      )}

      {role === 'ADMIN' && (
        <section className="content-section">
          <div className="toolbar">
            <span>Logged in as ADMIN</span>
            <button type="button" onClick={handleLogout}>
              Logout
            </button>
          </div>
          <AdminDashboard />
        </section>
      )}
    </div>
  )
}

export default App

