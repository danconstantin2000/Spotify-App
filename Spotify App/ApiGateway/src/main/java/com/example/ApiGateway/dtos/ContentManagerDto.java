package com.example.ApiGateway.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class ContentManagerDto {
    private String username;
    private String password;
    private String jwtToken;
}
