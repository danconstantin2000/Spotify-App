package pos.mongodb_application.playlist.controllers;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.mongodb_application.playlist.UserAuthorization;
import pos.mongodb_application.playlist.dto.InputPlaylistDto;
import pos.mongodb_application.playlist.dto.InputUserPlaylistDto;
import pos.mongodb_application.playlist.models.Playlist;
import pos.mongodb_application.playlist.models.UserPlaylist;
import pos.mongodb_application.playlist.services.interfaces.IUserPlaylistService;

import java.util.List;

@RestController
@RequestMapping("/api/pos_playlist")
public class UserPlaylistController {

    @Autowired
    private IUserPlaylistService userPlaylistService;
    @PostMapping("/userPlaylist")
    public ResponseEntity<?> createUserPlaylist(@RequestBody InputUserPlaylistDto inputUserPlaylistDto,
                                                @RequestHeader (required = false, name="Authorization") String authorization){

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
        boolean isClient=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==3){
                isClient=true;
            }

        }
        if(UID==inputUserPlaylistDto.getUid() && isClient) {
            UserPlaylist userPlaylist = userPlaylistService.createUserPLayList(inputUserPlaylistDto);
            if (userPlaylist == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(userPlaylist, HttpStatus.CREATED);

        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @PostMapping("/userPlaylist/{userPlaylistId}/playlist")
    public ResponseEntity<?> addNewPlaylist(@PathVariable String userPlaylistId, @RequestBody InputPlaylistDto inputPlaylistDto,
                                            @RequestHeader (required = false, name="Authorization") String authorization){

        int userId=userPlaylistService.findById(userPlaylistId).getUid();
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
        boolean isClient=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==3){
                isClient=true;
            }

        }
        if(userId==UID && isClient) {
            UserPlaylist userPlaylist = userPlaylistService.addNewPlayList(inputPlaylistDto, userPlaylistId);
            if (userPlaylist == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(userPlaylist, HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/userPlaylist/{userPlaylistId}")
    public ResponseEntity<?> getUserPlaylistById(@PathVariable String userPlaylistId,
                                                 @RequestHeader (required = false, name="Authorization") String authorization){
        UserPlaylist userPlaylist = userPlaylistService.findById(userPlaylistId);
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
        boolean isClient=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==3){
                isClient=true;
            }

        }
        if(isClient && UID==userPlaylist.getUid()) {
            if (userPlaylist == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(userPlaylist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @GetMapping("/userPlaylist/user/{uid}")
    public ResponseEntity<?> getUserPlaylistByUid(@PathVariable int uid, @RequestHeader (required = false, name="Authorization") String authorization ){
        UserPlaylist userPlaylist = userPlaylistService.findByUid(uid);
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
        boolean isClient=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==3){
                isClient=true;
            }

        }
        if(isClient && UID==uid) {
            if (userPlaylist == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(userPlaylist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @PutMapping("/userPlaylist/{userPlaylistId}/playlist/{playlistId}/songs/{songId}")
    public  ResponseEntity<?> addSongToPlaylist(@PathVariable String userPlaylistId, @PathVariable String playlistId, @PathVariable int songId,
                                                @RequestHeader (required = false, name="Authorization") String authorization){
        int userId=userPlaylistService.findById(userPlaylistId).getUid();
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
        boolean isClient=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==3){
                isClient=true;
            }

        }
        if(isClient && UID==userId) {
            UserPlaylist userPlaylist = userPlaylistService.putSongToPlaylist(userPlaylistId, playlistId, songId);
            if (userPlaylist == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userPlaylist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @DeleteMapping("/userPlaylist/{userPlaylistId}/playlist/{playlistId}/songs/{songId}")
    public  ResponseEntity<?> deleteSongForPlaylist(@PathVariable String userPlaylistId, @PathVariable String playlistId, @PathVariable int songId,
                                                    @RequestHeader (required = false, name="Authorization") String authorization) {
        int userId=userPlaylistService.findById(userPlaylistId).getUid();
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
        boolean isClient=false;
        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==3){
                isClient=true;
            }

        }
        if(isClient && UID==userId) {


            UserPlaylist userPlaylist = userPlaylistService.deleteSongFromPlaylist(userPlaylistId, playlistId, songId);
            if (userPlaylist == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userPlaylist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("userPlaylist/{userPlaylistId}/playlist/{playlistId}")

    public  ResponseEntity<?> deletePlaylist(@PathVariable String userPlaylistId, @PathVariable String playlistId,
                                             @RequestHeader (required = false, name="Authorization") String authorization) {
        int userId = userPlaylistService.findById(userPlaylistId).getUid();
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user = UserAuthorization.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID = user.getInt("uid");
        JSONArray roles = user.getJSONArray("roles");
        boolean isClient = false;
        for (int i = 0; i < roles.length(); i++) {
            int role = roles.getInt(i);
            if (role == 3) {
                isClient = true;
            }

        }
        if (userId == UID && isClient) {
            UserPlaylist userPlaylist = userPlaylistService.deletePlaylist(userPlaylistId, playlistId);
            if (userPlaylist == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userPlaylist, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("userPlaylist/{userPlaylistId}/playlist/{playlistId}")
    public  ResponseEntity<?> getPlaylist(@PathVariable String userPlaylistId, @PathVariable String playlistId,
                                             @RequestHeader (required = false, name="Authorization") String authorization) {
        int userId = userPlaylistService.findById(userPlaylistId).getUid();
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user = UserAuthorization.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID = user.getInt("uid");
        JSONArray roles = user.getJSONArray("roles");
        boolean isClient = false;
        for (int i = 0; i < roles.length(); i++) {
            int role = roles.getInt(i);
            if (role == 3) {
                isClient = true;
            }

        }
        if (userId == UID && isClient) {

            Playlist playlist = userPlaylistService.getPlaylist(userPlaylistId,playlistId);
            if(playlist==null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            else{
                return  new ResponseEntity<>(playlist,HttpStatus.OK);
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }
    @GetMapping("userPlaylist/{userPlaylistId}/playlists")
    public ResponseEntity<?> getAllPlaylists(@PathVariable String userPlaylistId, @RequestHeader (required = false, name="Authorization") String authorization){
        int userId = userPlaylistService.findById(userPlaylistId).getUid();
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        UserAuthorization.authorize(jwt_token);
        org.json.JSONObject user = UserAuthorization.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID = user.getInt("uid");
        JSONArray roles = user.getJSONArray("roles");
        boolean isClient = false;
        for (int i = 0; i < roles.length(); i++) {
            int role = roles.getInt(i);
            if (role == 3) {
                isClient = true;
            }

        }
        if (userId == UID && isClient) {
            List<Playlist> playlists = userPlaylistService.getPlaylists(userPlaylistId);
            if(playlists==null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            else{
                return  new ResponseEntity<>(playlists,HttpStatus.OK);
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }




}
