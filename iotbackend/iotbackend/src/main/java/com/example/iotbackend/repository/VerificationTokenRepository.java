package com.example.iotbackend.repository;

import com.example.iotbackend.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByOtp(String otp);
    Optional<VerificationToken> findByOtpAndType(String otp, VerificationToken.TokenType type);
}
