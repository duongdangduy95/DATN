package com.example.iotbackend.controller;

import com.example.iotbackend.dto.CreateRoomRequest;
import com.example.iotbackend.entity.Room;
import com.example.iotbackend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomRequest req) {
        return ResponseEntity.ok(roomService.createRoom(req));
    }
}
