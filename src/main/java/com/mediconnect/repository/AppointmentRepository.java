package com.mediconnect.repository;

import com.mediconnect.model.Appointment;
import com.mediconnect.model.Doctor;
import com.mediconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("select count(a) from Appointment a " +
           "where a.doctor = :doctor and date(a.appointmentTime) = :day")
    long countByDoctorAndDay(@Param("doctor") Doctor doctor, @Param("day") LocalDate day);

    List<Appointment> findByPatientOrderByAppointmentTimeAsc(User patient);

    @Query("select count(a) from Appointment a where date(a.appointmentTime) = :day")
    long countByDay(@Param("day") LocalDate day);
}

