package com.example.spotify.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ArtistNotFoundException extends RuntimeException
{
    public ArtistNotFoundException(String uuid) {
        super("Could not find artist " + uuid);
    }
}
