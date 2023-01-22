package com.example.ApiGateway.dtos;

import com.example.ApiGateway.enums.Genre;
import com.example.ApiGateway.enums.Type;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SongDto {
    private String name;
    private Genre gen;
    private Type  type;
    private Integer year;



}
