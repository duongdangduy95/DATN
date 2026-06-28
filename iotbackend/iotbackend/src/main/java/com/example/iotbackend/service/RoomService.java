package com.example.iotbackend.service;

import com.example.iotbackend.dto.CreateRoomRequest;
import com.example.iotbackend.entity.Home;
import com.example.iotbackend.entity.Room;
import com.example.iotbackend.entity.User;
import com.example.iotbackend.repository.HomeRepository;
import com.example.iotbackend.repository.RoomRepository;
import com.example.iotbackend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HomeRepository homeRepository;
    private final SecurityUtils securityUtils;

    public Room createRoom(CreateRoomRequest req) {

        User user = securityUtils.getCurrentUser();

        Home home = homeRepository.findById(req.getHomeId())
                .orElseThrow(() -> new RuntimeException("Home not found"));

        if (!home.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền");
        }

        Room room = new Room();
        room.setName(req.getName());
        room.setHome(home);

        return roomRepository.save(room);
    }
}
