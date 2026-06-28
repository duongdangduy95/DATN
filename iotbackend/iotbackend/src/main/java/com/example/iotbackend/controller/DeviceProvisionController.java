package com.example.iotbackend.controller;

import com.example.iotbackend.entity.PairToken;
import com.example.iotbackend.entity.User;
import com.example.iotbackend.repository.PairTokenRepository;
import com.example.iotbackend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/provision")
@RequiredArgsConstructor
public class DeviceProvisionController {

    private final PairTokenRepository pairTokenRepository;

    private final SecurityUtils securityUtils;

    @PostMapping("/token")
    public String createPairToken() {

        User user = securityUtils.getCurrentUser();

        PairToken token = new PairToken();

        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        pairTokenRepository.save(token);

        return token.getToken();
    }
}