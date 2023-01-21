package com.example.ApiGateway.dtos;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
public class ResponseDto {
    public HttpStatusCode statusCode;
    public JSONObject body;
}
