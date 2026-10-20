
package com.example.demo.repository;

import com.example.demo.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository
        extends MongoRepository<Appointment, String> {

    List<Appointment> findByPatientIdOrderByCreatedAtDesc(
            String patientId);

    List<Appointment> findByDoctorIdOrderByCreatedAtDesc(
            String doctorId);

    Optional<Appointment> findByIdAndDoctorId(
            String id, String doctorId);

    Optional<Appointment> findByIdAndPatientIdAndStatus(
            String id, String patientId, String status);
}

