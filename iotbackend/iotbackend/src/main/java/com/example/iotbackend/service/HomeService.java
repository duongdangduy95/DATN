package com.example.iotbackend.service;

import com.example.iotbackend.dto.CreateHomeRequest;
import com.example.iotbackend.entity.Home;
import com.example.iotbackend.entity.User;
import com.example.iotbackend.repository.HomeRepository;
import com.example.iotbackend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;
    private final SecurityUtils securityUtils;

    public Home createHome(CreateHomeRequest req) {
        User user = securityUtils.getCurrentUser();

        Home home = new Home();
        home.setName(req.getName());
        home.setUser(user);

        return homeRepository.save(home);
    }
}
