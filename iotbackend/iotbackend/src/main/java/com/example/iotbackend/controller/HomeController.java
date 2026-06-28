package com.example.iotbackend.controller;

import com.example.iotbackend.dto.CreateHomeRequest;
import com.example.iotbackend.entity.Home;
import com.example.iotbackend.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/homes")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @PostMapping
    public ResponseEntity<Home> createHome(@RequestBody CreateHomeRequest req) {
        return ResponseEntity.ok(homeService.createHome(req));
    }
}
