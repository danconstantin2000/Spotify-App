package pos.mongodb_application.playlist.controllers;


import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.mongodb_application.playlist.UserAuthorization;
import pos.mongodb_application.playlist.dto.InputPlaylistDto;
import pos.mongodb_application.playlist.models.Playlist;
import pos.mongodb_application.playlist.services.interfaces.IPlaylistService;


@RestController
@RequestMapping("/api/pos_playlist")
public class PlaylistController {

//Colectie ce nu are legatura cu un rol = > deci doar adminul va avea acess la metode.
    @Autowired
    IPlaylistService playlistService;

    @PostMapping("/playlist" )
    ResponseEntity<?> newPlayList(@RequestBody InputPlaylistDto inputPlaylistDto,
                                  @RequestHeader (required = false, name="Authorization") String authorization) {

        if(authorization==null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token=authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user=UserAuthorization.authorize(jwt_token);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");
        JSONArray roles=user.getJSONArray("roles");
        boolean isAdmin=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==4){
                isAdmin=true;
            }

        }
        if(isAdmin){
            Playlist playlist = playlistService.createPlayList(inputPlaylistDto);
            if (playlist == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(playlist, HttpStatus.CREATED);
        }
       else{
           return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @PutMapping("/playlist/{id}/songcollection/songs/{songId}")
    ResponseEntity<?> addSongToPlaylist(@PathVariable int songId,@PathVariable String id,
                                        @RequestHeader (required = false, name="Authorization") String authorization) {
        if(authorization==null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token=authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user=UserAuthorization.authorize(jwt_token);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");
        JSONArray roles=user.getJSONArray("roles");
        boolean isAdmin=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==4){
                isAdmin=true;
            }

        }
        if(isAdmin) {
            playlistService.addSongToPlayList(songId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/playlist")
    ResponseEntity<?> getAllPlaylists(@RequestHeader (required = false, name="Authorization") String authorization)
    {
        if(authorization==null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token=authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user=UserAuthorization.authorize(jwt_token);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");
        JSONArray roles=user.getJSONArray("roles");
        boolean isAdmin=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==4){
                isAdmin=true;
            }

        }
        if(isAdmin){
            return new ResponseEntity<>(playlistService.getAllPlaylists(),HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("/playlist/{id}")
    ResponseEntity<?> getPlaylist(@PathVariable  String id,@RequestHeader (required = false, name="Authorization") String authorization ){
        if(authorization==null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token=authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user=UserAuthorization.authorize(jwt_token);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");
        JSONArray roles=user.getJSONArray("roles");
        boolean isAdmin=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==4){
                isAdmin=true;
            }

        }
        if(isAdmin) {
            Playlist playlist = playlistService.getPlaylist(id);
            if (playlist == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(playlist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        }

    @DeleteMapping("/playlist/{id}")
    ResponseEntity<?> deletePlaylist(@PathVariable String id, @RequestHeader (required = false, name="Authorization") String authorization){
        if(authorization==null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token=authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user=UserAuthorization.authorize(jwt_token);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");
        JSONArray roles=user.getJSONArray("roles");
        boolean isAdmin=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==4){
                isAdmin=true;
            }

        }
        if(isAdmin) {
            Playlist playlist = playlistService.deletePlaylist(id);
            if (playlist == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(playlist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/playlist/{id}/songcollection/songs/{songId}")
    ResponseEntity<?> removeFromPlaylist(@PathVariable int songId,@PathVariable String id,
                                         @RequestHeader (required = false, name="Authorization") String authorization) {
        if(authorization==null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token=authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user=UserAuthorization.authorize(jwt_token);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");
        JSONArray roles=user.getJSONArray("roles");
        boolean isAdmin=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==4){
                isAdmin=true;
            }

        }
        if(isAdmin) {
            Playlist playlist = playlistService.removeFromPlaylist(id, songId);
            if (playlist == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(playlist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}


