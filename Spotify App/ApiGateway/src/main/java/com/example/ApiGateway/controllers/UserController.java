package com.example.ApiGateway.controllers;

import com.example.ApiGateway.dtos.*;
import com.example.ApiGateway.exceptions.ConflictException;

import com.example.ApiGateway.services.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static com.example.ApiGateway.services.UserService.convertStringToDocument;


@RestController
@RequestMapping("/api/gateway")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {

        String jwt_token = userService.Login(loginDto);
        if (jwt_token != null) {
            net.minidev.json.JSONObject jsonObject = new net.minidev.json.JSONObject();
            jsonObject.put("jwt_token", jwt_token);
            JSONObject user = userService.authorize(jwt_token);
            String username = userService.getUserName(user.getInt("uid"));
            jsonObject.put("uid", user.get("uid"));
            JSONArray roles = user.getJSONArray("roles");
            jsonObject.put("roles", roles.toList());
            jsonObject.put("username", username);
            return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/logout")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> logout(@RequestBody LogoutDto logoutDto) {

        String success = userService.Logout(logoutDto);
        if (success != null) {
            if (success.equals("SUCCESS")) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

    }

    @PostMapping("/register")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> register(@RequestBody LoginDto loginDto) {
        String user_id = userService.register(loginDto);
        if (user_id != null) {
            net.minidev.json.JSONObject jsonObject = new net.minidev.json.JSONObject();
            jsonObject.put("uid", user_id);
            return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/register/contentManager")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> registerContentManager(@RequestBody LoginDto loginDto, @RequestHeader(required = false, name = "Authorization") String authorization) {
        boolean success = false;
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        JSONObject user = userService.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID = user.getInt("uid");
        JSONArray roles = user.getJSONArray("roles");
        boolean isAdmin = false;

        for (int i = 0; i < roles.length(); i++) {
            int role = roles.getInt(i);
            if (role == 4) {
                isAdmin = true;
            }
        }
        if (isAdmin) {
            try {
                success = userService.registerContentManager(loginDto, jwt_token);

            } catch (ConflictException e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);

            }
            if (success) {

                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/register/artist")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> registerArtist(@RequestBody ArtistDto artistDto, @RequestHeader(required = false, name = "Authorization") String authorization) {
        Integer response = null;
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        JSONObject user = userService.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID = user.getInt("uid");
        JSONArray roles = user.getJSONArray("roles");
        boolean isContentManager = false;

        for (int i = 0; i < roles.length(); i++) {
            int role = roles.getInt(i);
            if (role == 1) {
                isContentManager = true;
            }
        }
        if (isContentManager) {

            try {
                response = userService.registerArtist(artistDto, jwt_token);

            } catch (ConflictException e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);

            }
            if (response != null) {

                return new ResponseEntity<>(HttpStatus.valueOf(response));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/addSong")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> addSong(@RequestBody SongDto songDto, @RequestHeader(required = false, name = "Authorization") String authorization) {
        Integer response = null;
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        JSONObject user = userService.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");

        int statusCode=userService.addSong(UID,songDto,jwt_token);

        if(statusCode!=-1) {
            return new ResponseEntity<>(HttpStatus.valueOf(statusCode));

        }
        else{
            return  new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }
    @GetMapping("/artistsSongs")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getSongs(@RequestHeader(required = false, name = "Authorization") String authorization) {
        Integer response = null;
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        JSONObject user = userService.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");

        JSONArray songs = userService.getSongsFromArtist(UID);
        if(songs!=null){
            return new ResponseEntity<>(songs.toList(),HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/artistsSongs/{songId}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> deleteSong(@PathVariable int songId,@RequestHeader(required = false, name = "Authorization") String authorization) {
        Integer response = null;
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        JSONObject user = userService.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");

        int statusCode = userService.deleteSongFromArtists(UID,songId,jwt_token);
        if(statusCode!=-1){
            return new ResponseEntity<>(HttpStatus.valueOf(statusCode));
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addPlaylist")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> addPlaylist(@RequestBody AddPlaylistDto addPlaylistDto,@RequestHeader(required = false, name = "Authorization") String authorization) {
        Integer response = null;
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        JSONObject user = userService.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");

        int statusCode = userService.addPlaylist(addPlaylistDto,jwt_token,UID);
        if(statusCode!=-1){
            return new ResponseEntity<>(HttpStatus.valueOf(statusCode));
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/getPlaylists")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getPlaylists(@RequestHeader(required = false, name = "Authorization") String authorization) {
        Integer response = null;
        if (authorization == null || !authorization.matches("Bearer\\s[\\x00-\\x7F]+")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String jwt_token = authorization.split(" ")[1];
        JSONObject user = userService.authorize(jwt_token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int UID=user.getInt("uid");

        JSONArray playlists = userService.getPlaylists(jwt_token,UID);
        if(playlists!=null){
            return new ResponseEntity<>(playlists.toList(),HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

}

