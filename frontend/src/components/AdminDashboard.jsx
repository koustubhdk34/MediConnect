import React, { useEffect, useState } from 'react'
import {
  createDoctor,
  getAdminAppointments,
  getAdminStats,
  updateAppointmentStatus,
} from '../services/api'
import './AdminDashboard.css'

const AdminDashboard = () => {
  const [stats, setStats] = useState(null)
  const [appointments, setAppointments] = useState([])
  const [doctorName, setDoctorName] = useState('')
  const [specialization, setSpecialization] = useState('')
  const [message, setMessage] = useState(null)

  const loadStats = async () => {
    const data = await getAdminStats()
    setStats(data)
  }

  const loadAppointments = async () => {
    const data = await getAdminAppointments()
    setAppointments(data)
  }

  useEffect(() => {
    loadStats()
    loadAppointments()
  }, [])

  const handleCreateDoctor = async (e) => {
    e.preventDefault()
    setMessage(null)
    if (!doctorName || !specialization) {
      setMessage('Please enter doctor name and specialization')
      return
    }
    try {
      await createDoctor(doctorName, specialization)
      setMessage('Doctor created successfully')
      setDoctorName('')
      setSpecialization('')
      loadStats()
    } catch (err) {
      setMessage(err?.response?.data?.message || 'Failed to create doctor')
    }
  }

  const handleStatusChange = async (id, status) => {
    try {
      await updateAppointmentStatus(id, status)
      setAppointments((prev) =>
        prev.map((a) => (a.id === id ? { ...a, status } : a)),
      )
    } catch (err) {
      setMessage(err?.response?.data?.message || 'Failed to update status')
    }
  }

  return (
    <div className="admin-dashboard">
      <h2>Admin Dashboard</h2>

      {stats && (
        <div className="stats-grid">
          <div className="stat-card">
            <h3>Total Patients</h3>
            <p>{stats.totalPatients}</p>
          </div>
          <div className="stat-card">
            <h3>Total Doctors</h3>
            <p>{stats.totalDoctors}</p>
          </div>
          <div className="stat-card">
            <h3>Total Appointments</h3>
            <p>{stats.totalAppointments}</p>
          </div>
          <div className="stat-card">
            <h3>Appointments Today</h3>
            <p>{stats.appointmentsToday}</p>
          </div>
        </div>
      )}

      <section className="admin-section">
        <h3>Add Doctor</h3>
        <form className="doctor-form" onSubmit={handleCreateDoctor}>
          <input
            type="text"
            placeholder="Doctor name"
            value={doctorName}
            onChange={(e) => setDoctorName(e.target.value)}
          />
          <input
            type="text"
            placeholder="Specialization"
            value={specialization}
            onChange={(e) => setSpecialization(e.target.value)}
          />
          <button type="submit">Create Doctor</button>
        </form>
      </section>

      {message && <p className="feedback-message">{message}</p>}

      <section className="admin-section">
        <h3>All Appointments</h3>
        {appointments.length === 0 && <p>No appointments yet.</p>}
        {appointments.length > 0 && (
          <table className="appointments-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Doctor</th>
                <th>Patient</th>
                <th>Time</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((a) => (
                <tr key={a.id}>
                  <td>{a.id}</td>
                  <td>{a.doctorName}</td>
                  <td>{a.patientName}</td>
                  <td>{new Date(a.appointmentTime).toLocaleString()}</td>
                  <td>
                    <span className={`status-badge status-${a.status.toLowerCase()}`}>{a.status}</span>
                  </td>
                  <td className="actions-cell">
                    <button
                      type="button"
                      disabled={a.status === 'CONFIRMED'}
                      onClick={() => handleStatusChange(a.id, 'CONFIRMED')}
                    >
                      Confirm
                    </button>
                    <button
                      type="button"
                      disabled={a.status === 'CANCELLED'}
                      onClick={() => handleStatusChange(a.id, 'CANCELLED')}
                    >
                      Cancel
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  )
}

export default AdminDashboard

