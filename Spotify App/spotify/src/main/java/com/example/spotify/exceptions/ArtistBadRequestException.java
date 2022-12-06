package com.example.spotify.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ArtistBadRequestException extends RuntimeException{

    public ArtistBadRequestException(String uuid) {
        super("Bad request, "+ uuid+"does not exits!\n");
    }
}
