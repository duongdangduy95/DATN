package com.example.iotbackend.service;

import com.example.iotbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CleanupService {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void cleanUnverifiedUsers() {

        LocalDateTime time = LocalDateTime.now().minusMinutes(5);

        userRepository.deleteByVerifiedFalseAndCreatedAtBefore(time);

    }
}
