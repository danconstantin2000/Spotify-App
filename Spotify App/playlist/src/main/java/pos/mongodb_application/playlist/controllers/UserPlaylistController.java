package pos.mongodb_application.playlist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.mongodb_application.playlist.dto.InputPlaylistDto;
import pos.mongodb_application.playlist.dto.InputUserPlaylistDto;
import pos.mongodb_application.playlist.models.UserPlaylist;
import pos.mongodb_application.playlist.services.interfaces.IUserPlaylistService;

@RestController
@RequestMapping("/api/pos_playlist")
public class UserPlaylistController {

    @Autowired
    private IUserPlaylistService userPlaylistService;
    @PostMapping("/userPlaylist")
    public ResponseEntity<?> createUserPlaylist(@RequestBody InputUserPlaylistDto inputUserPlaylistDto){

        UserPlaylist userPlaylist= userPlaylistService.createUserPLayList(inputUserPlaylistDto);
        if(userPlaylist==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userPlaylist, HttpStatus.CREATED);

    }
    @PostMapping("/userPlaylist/{userPlaylistId}/playlist")
    public ResponseEntity<?> addNewPlaylist(@PathVariable String userPlaylistId, @RequestBody InputPlaylistDto inputPlaylistDto){

        UserPlaylist userPlaylist= userPlaylistService.addNewPlayList(inputPlaylistDto,userPlaylistId);
        if(userPlaylist==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userPlaylist, HttpStatus.CREATED);

    }
}
