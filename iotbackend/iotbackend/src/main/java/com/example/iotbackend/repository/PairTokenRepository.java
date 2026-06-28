package com.example.iotbackend.repository;

import com.example.iotbackend.entity.PairToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PairTokenRepository
        extends JpaRepository<PairToken, Long> {

    Optional<PairToken> findByToken(
            String token
    );
}