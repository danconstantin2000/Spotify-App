package pos.mongodb_application.playlist.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.mongodb_application.playlist.dto.InputPlaylistDto;
import pos.mongodb_application.playlist.models.Playlist;
import pos.mongodb_application.playlist.services.interfaces.IPlaylistService;


@RestController
@RequestMapping("/api/pos_playlist")
public class PlaylistController {

//    @Autowired
//    IPlaylistService playlistService;
//    @PostMapping("/playlist")
//    ResponseEntity<?> newPlayList(@RequestBody InputPlaylistDto newPlayList){
//
//        UserPlaylist userPlaylist =playlistService.createPlayList(newPlayList);
//        if(userPlaylist==null) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        return new ResponseEntity<>(userPlaylist,HttpStatus.CREATED);
//
//    }
//    @PutMapping("/playlist/")

    @Autowired
    IPlaylistService playlistService;

    @PostMapping("/playlist")
    ResponseEntity<?> newPlayList(@RequestBody InputPlaylistDto inputPlaylistDto) {
        Playlist playlist = playlistService.createPlayList(inputPlaylistDto);
        if (playlist == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(playlist, HttpStatus.CREATED);

    }

    @PutMapping("/playlist/{id}/songcollection/songs/{songId}")
    ResponseEntity<?> addSongToPlaylist(@PathVariable int songId,@PathVariable String id) {

        playlistService.addSongToPlayList(songId,id);
        return  new ResponseEntity<> (HttpStatus.NO_CONTENT);
    }

    @GetMapping("/playlist")
    ResponseEntity<?> getAllPlaylists()
    {
        return new ResponseEntity<>(playlistService.getAllPlaylists(),HttpStatus.OK);
    }

    @GetMapping("/playlist/{id}")
    ResponseEntity<?> getPlaylist(@PathVariable  String id){
        Playlist playlist=playlistService.getPlaylist(id);
        if(playlist==null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(playlist,HttpStatus.OK);
    }

    @DeleteMapping("/playlist/{id}")
    ResponseEntity<?> deletePlaylist(@PathVariable String id){
        Playlist playlist=playlistService.deletePlaylist(id);
        if(playlist==null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(playlist,HttpStatus.OK);
    }

    @DeleteMapping("/playlist/{id}/songcollection/songs/{songId}")
    ResponseEntity<?> removeFromPlaylist(@PathVariable int songId,@PathVariable String id) {

        Playlist playlist=playlistService.removeFromPlaylist(id,songId);
        if(playlist==null)return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return  new ResponseEntity<> (playlist,HttpStatus.OK);
    }
}


