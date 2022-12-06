package com.example.spotify.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SongNotFoundException extends RuntimeException{

    public SongNotFoundException(int id){
        super("Could not find the song " + id);
    }
}
