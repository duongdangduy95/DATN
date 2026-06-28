package com.example.iotbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {
    private String name;
    private Long homeId;
}
