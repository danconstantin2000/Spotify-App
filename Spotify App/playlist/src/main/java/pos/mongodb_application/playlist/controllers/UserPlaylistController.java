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

    @GetMapping("/userPlaylist/{userPlaylistId}")
    public ResponseEntity<?> getUserPlaylistById(@PathVariable String userPlaylistId){
        UserPlaylist userPlaylist = userPlaylistService.findById(userPlaylistId);
        if(userPlaylist==null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(userPlaylist,HttpStatus.OK);
    }
    @GetMapping("/userPlaylist/user/{uid}")
    public ResponseEntity<?> getUserPlaylistByUid(@PathVariable int uid){
        UserPlaylist userPlaylist = userPlaylistService.findByUid(uid);
        if(userPlaylist==null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(userPlaylist,HttpStatus.OK);
    }
    @PutMapping("/userPlaylist/{userPlaylistId}/playlist/{playlistId}/songs/{songId}")
    public  ResponseEntity<?> addSongToPlaylist(@PathVariable String userPlaylistId, @PathVariable String playlistId, @PathVariable int songId ){

        UserPlaylist userPlaylist=userPlaylistService.putSongToPlaylist(userPlaylistId,playlistId,songId);
        if(userPlaylist==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userPlaylist,HttpStatus.OK);

    }

    @DeleteMapping("/userPlaylist/{userPlaylistId}/playlist/{playlistId}/songs/{songId}")
    public  ResponseEntity<?> deleteSongForPlaylist(@PathVariable String userPlaylistId, @PathVariable String playlistId, @PathVariable int songId ) {
        UserPlaylist userPlaylist=userPlaylistService.deleteSongFromPlaylist(userPlaylistId,playlistId,songId);
        if(userPlaylist==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userPlaylist,HttpStatus.OK);
    }

    @DeleteMapping("userPlaylist/{userPlaylistId}/playlist/{playlistId}")

    public  ResponseEntity<?> deletePlaylist(@PathVariable String userPlaylistId, @PathVariable String playlistId) {
        UserPlaylist userPlaylist=userPlaylistService.deletePlaylist(userPlaylistId,playlistId);
        if(userPlaylist==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userPlaylist,HttpStatus.OK);
    }

}
