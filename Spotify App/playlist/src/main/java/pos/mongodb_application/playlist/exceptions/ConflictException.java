package pos.mongodb_application.playlist.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException{

    public ConflictException(String name) {
        super("Conflict, "+ name +" already exits!\n");
    }
    public  ConflictException(){super();}
}
