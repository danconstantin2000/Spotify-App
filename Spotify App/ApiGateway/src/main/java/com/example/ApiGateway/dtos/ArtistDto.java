package com.example.ApiGateway.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class ArtistDto {
    private String username;
    private String password;
    private Boolean active;
}
