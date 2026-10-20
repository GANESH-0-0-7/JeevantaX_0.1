
package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.UserResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class DoctorListService {

    private final UserRepository userRepository;

    public DoctorListService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getDoctors() {
        List<User> doctors = userRepository.findByRole("doctor");

        return doctors.stream()
                .map(UserResponse::new)
                .toList();
    }
}
