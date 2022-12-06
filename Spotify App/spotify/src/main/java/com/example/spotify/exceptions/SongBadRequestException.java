package com.example.spotify.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SongBadRequestException extends RuntimeException{

    public SongBadRequestException(int id) {
        super("Bad request, "+ id+"does not exits!\n");
    }
}
