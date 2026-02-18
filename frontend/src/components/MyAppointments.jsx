import React, { useEffect, useState } from 'react'
import { getMyAppointments } from '../services/api'
import './MyAppointments.css'

const MyAppointments = () => {
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      try {
        const data = await getMyAppointments()
        setAppointments(data)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  return (
    <div className="appointments-container">
      <h2>My Appointments</h2>
      {loading && <p>Loading...</p>}
      {!loading && appointments.length === 0 && <p>No appointments yet.</p>}

      {!loading && appointments.length > 0 && (
        <table className="appointments-table">
          <thead>
            <tr>
              <th>Doctor</th>
              <th>Specialization</th>
              <th>Time</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {appointments.map((a) => (
              <tr key={a.id}>
                <td>{a.doctorName}</td>
                <td>{a.specialization}</td>
                <td>{new Date(a.appointmentTime).toLocaleString()}</td>
                <td>
                  <span className={`status-badge status-${a.status.toLowerCase()}`}>{a.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default MyAppointments

