import React, { useEffect, useState } from 'react'
import { getDoctors, bookAppointment } from '../services/api'
import './DoctorList.css'

const DoctorList = () => {
  const [doctors, setDoctors] = useState([])
  const [selectedTime, setSelectedTime] = useState('')
  const [minDateTime, setMinDateTime] = useState('')
  const [search, setSearch] = useState('')
  const [message, setMessage] = useState(null)

  useEffect(() => {
    const fetchDoctors = async () => {
      const data = await getDoctors()
      setDoctors(data)
    }
    fetchDoctors()
  }, [])

  useEffect(() => {
    const now = new Date()
    const iso = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
    setMinDateTime(iso)
  }, [])

  const handleBook = async (doctorId: number) => {
    if (!selectedTime) {
      setMessage('Please select a date and time')
      return
    }
    const chosen = new Date(selectedTime)
    const now = new Date()
    if (chosen <= now) {
      setMessage('Please choose a future date and time')
      return
    }
    try {
      await bookAppointment(doctorId, selectedTime)
      setMessage('Appointment booked successfully')
    } catch (err) {
      setMessage(err?.response?.data?.message || 'Failed to book appointment')
    }
  }

  const filteredDoctors = doctors.filter((doctor) => {
    const term = search.toLowerCase().trim()
    if (!term) return true
    return (
      doctor.name.toLowerCase().includes(term) ||
      doctor.specialization.toLowerCase().includes(term)
    )
  })

  return (
    <div className="doctor-list-container">
      <h2>Available Doctors</h2>

      <div className="doctor-filters">
        <input
          type="text"
          className="doctor-search-input"
          placeholder="Search by doctor name or specialization..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="datetime-input">
        <label>Select appointment time:</label>
        <input
          type="datetime-local"
          value={selectedTime}
          min={minDateTime}
          onChange={(e) => setSelectedTime(e.target.value)}
        />
      </div>

      {message && <p className="feedback-message">{message}</p>}

      <div className="doctor-cards">
        {filteredDoctors.map((doctor) => (
          <div key={doctor.id} className="doctor-card">
            <h3>{doctor.name}</h3>
            <p>{doctor.specialization}</p>
            <button onClick={() => handleBook(doctor.id)}>Book</button>
          </div>
        ))}
      </div>
    </div>
  )
}

export default DoctorList

