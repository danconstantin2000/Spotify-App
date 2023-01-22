package com.example.spotify.controllers;

import com.example.spotify.DTO.ArtistDTO;
import com.example.spotify.DTO.SongDTO;
import com.example.spotify.UserAuthorization;
import com.example.spotify.entities.Song;
import com.example.spotify.services.interfaces.IArtistService;
import net.minidev.json.JSONObject;
import org.jboss.jandex.Index;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/songcollection")
public class ArtistController {
    @Autowired
    private IArtistService artistService;


    @GetMapping("/artists")
    public ResponseEntity<?> all(@RequestParam(required = false,name="name") String name,
                                 @RequestParam(required = false,name="matching") String matching,
                                 @RequestParam(required = false,name="page") Integer page,
                                 @RequestParam(required = false,name="items_per_page") Integer itemsPerPage)
    {
        if(!StringUtils.isEmpty(page)){

            if(StringUtils.isEmpty(itemsPerPage)){
                itemsPerPage=3;
            }
            List<ArtistDTO> artists = artistService.findAll();
            if(page<=0) {
                return  new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            List<ArtistDTO> fitArtists= new ArrayList<>();
            for(int i=itemsPerPage*(page-1);i<itemsPerPage*page;i++){
                try {

                    fitArtists.add(artists.get(i));
                }catch(IndexOutOfBoundsException ex){
                    ex.printStackTrace();
                }
            }
            List<EntityModel<ArtistDTO>> newArtists=fitArtists.stream()
                    .map(artist -> EntityModel.of(artist,
                            linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                            BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists").withRel("artists"),
                            linkTo(methodOn(ArtistController.class).getAllFromArtist(artist.getUuid())).withRel("songs")

                    )).collect(Collectors.toList());
            if(fitArtists.size()==0)
            {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }


            if(fitArtists.size()<itemsPerPage || itemsPerPage*page==artists.size()){
                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(newArtists, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists").withSelfRel(),
                        BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists?page="+(page-1)+"&items_per_page="+itemsPerPage).withRel("prev")));
            }

            if(page==1){
                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(newArtists, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists").withSelfRel(),
                        BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists?page="+(page+1)+"&items_per_page="+itemsPerPage).withRel("next")));

            }
            return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(newArtists, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists").withSelfRel(),
                    BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists?page="+(page+1)+"&items_per_page="+itemsPerPage).withRel("next"),
                    BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists?page="+(page-1)+"&items_per_page="+itemsPerPage).withRel("prev")));

        }
        else {
            if (!StringUtils.isEmpty(name)) {

                if (StringUtils.isEmpty(matching)) {
                    List<EntityModel<ArtistDTO>> artists = artistService.findAllByName(name, matching).stream()
                            .map(artist -> EntityModel.of(artist,
                                    linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                                    BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists"),
                                    linkTo(methodOn(ArtistController.class).getAllFromArtist(artist.getUuid())).withRel("songs")
                            )).collect(Collectors.toList());

                    return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(artists, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").slash("?name=" + name.replace(" ", "%20")).withSelfRel()));
                } else {
                    List<EntityModel<ArtistDTO>> artists = artistService.findAllByName(name, matching).stream()
                            .map(artist -> EntityModel.of(artist,
                                    linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                                    BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists"),
                                    linkTo(methodOn(ArtistController.class).getAllFromArtist(artist.getUuid())).withRel("songs")
                            )).collect(Collectors.toList());

                    return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(artists, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").slash("?name=" + name.replace(" ", "%20") + "&matching=" + matching.replace(" ", "%20")).withSelfRel()));
                }
            } else {
                List<EntityModel<ArtistDTO>> artists = artistService.findAll().stream()
                        .map(artist -> EntityModel.of(artist,
                                linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists"),
                                linkTo(methodOn(ArtistController.class).getAllFromArtist(artist.getUuid())).withRel("songs")
                        )).collect(Collectors.toList());
                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(artists, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists").withSelfRel()));
            }
        }
    }

    @GetMapping("/artists/{uuid}")
    ResponseEntity<?> one(@PathVariable String uuid) {
        ArtistDTO artist=artistService.findById(uuid);
        return ResponseEntity.status((HttpStatus.OK)).body(EntityModel.of(artist,
                linkTo(methodOn(ArtistController.class).one(uuid)).withSelfRel(),
                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists"),
                linkTo(methodOn(ArtistController.class).getAllFromArtist(artist.getUuid())).withRel("songs")));
    }

    @DeleteMapping("/artists/{uuid}")
    ResponseEntity<?> deleteArtist(@PathVariable String uuid, @RequestHeader (required = false, name="Authorization") String authorization){
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
        boolean isContentManager=false;

        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==1){
                isContentManager=true;
            }
        }
        if(isContentManager){
        ArtistDTO artist = artistService.findById(uuid);
        ResponseEntity<EntityModel<ArtistDTO>> response=ResponseEntity.status((HttpStatus.OK)).body(EntityModel.of(artist,
                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists")

        ));
        artistService.deleteById(uuid);
        return response;}
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/artists")
    ResponseEntity<?> newArtist(@RequestBody  ArtistDTO newArtist,
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
        boolean isContentManager=false;

        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==1){
                isContentManager=true;
            }
        }
        if(isContentManager) {
            JSONObject response = new JSONObject();
            newArtist.setUuid(UUID.randomUUID().toString());
            ArtistDTO artist = null;
            try {
                artist = artistService.insert(newArtist);
            } catch (DataIntegrityViolationException ex) {
                response.put("message", "A constraint was violated!");
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<>(EntityModel.of(artist,
                    linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                    BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists")
            ), HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/artists/{uuid}")
    ResponseEntity<?> updateArtist(@PathVariable String uuid,@RequestBody ArtistDTO modifiedArtist,
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
        boolean isContentManager=false;

        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==1){
                isContentManager=true;
            }
        }
        if(isContentManager) {
            JSONObject response = new JSONObject();
            try {
                artistService.update(uuid, modifiedArtist);
            } catch (DataIntegrityViolationException ex) {
                response.put("message", "A constraint was violated!");
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    //Add in songs table all songs from lists and asign to uuid artist
    @PostMapping("/artists/{uuid}/songs")
    ResponseEntity<?> addSong(@PathVariable String uuid, @RequestBody List<SongDTO> songs,
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
        boolean isContentManager=false;
        boolean isArtist=false;

        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==1){
                isContentManager=true;
            }
            if(role==2)
            {
                isArtist=true;
            }
        }
        if(!isContentManager || isArtist){
            String name=UserAuthorization.getUserName(UID);
            if(name==null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            else{
                if(name.equals(artistService.findById(uuid).getName())){
                    JSONObject response = new JSONObject();
                    String message = "Success";
                    List<SongDTO> successfullSongs = new ArrayList<>();
                    for (SongDTO song : songs) {
                        try {
                            int index = songs.indexOf(song);
                            SongDTO createdSong = artistService.addSong(uuid, song);
                            songs.set(index, createdSong);
                            successfullSongs.add(createdSong);
                        } catch (DataIntegrityViolationException ex) {
                            message = "A constraint was violated!";
                        }

                    }
                    response.put("message", message);
                    if (successfullSongs.isEmpty()) {
                        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
                    }
                    List<EntityModel<SongDTO>> returnedSongs = successfullSongs.stream()
                            .map(song -> EntityModel.of(song,
                                    linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                                    linkTo(methodOn(ArtistController.class).getAllFromArtist(uuid)).withRel("parent"))).collect(Collectors.toList());
                    response.put("songs", returnedSongs);
                    return new ResponseEntity<>(response, HttpStatus.CREATED);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }


        }
        if(isContentManager) {
            JSONObject response = new JSONObject();
            String message = "Success";
            List<SongDTO> successfullSongs = new ArrayList<>();
            for (SongDTO song : songs) {
                try {
                    int index = songs.indexOf(song);
                    SongDTO createdSong = artistService.addSong(uuid, song);
                    songs.set(index, createdSong);
                    successfullSongs.add(createdSong);
                } catch (DataIntegrityViolationException ex) {
                    message = "A constraint was violated!";
                }

            }
            response.put("message", message);
            if (successfullSongs.isEmpty()) {
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
            List<EntityModel<SongDTO>> returnedSongs = successfullSongs.stream()
                    .map(song -> EntityModel.of(song,
                            linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                            linkTo(methodOn(ArtistController.class).getAllFromArtist(uuid)).withRel("parent"))).collect(Collectors.toList());
            response.put("songs", returnedSongs);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }
    @GetMapping("/artists/{uuid}/songs")
    ResponseEntity<?> getAllFromArtist(@PathVariable String uuid){
        List<EntityModel<SongDTO>> songs=artistService.getAllSongsByArtist(uuid).stream()
                .map(song->EntityModel.of(song,
                        linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                        BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withRel("songs"),
                        linkTo(methodOn(ArtistController.class).one(uuid)).withRel("artist"))
                )
                    .collect(Collectors.toList());

        return new ResponseEntity<>(CollectionModel.of(songs,  BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withRel("songs")),HttpStatus.OK);

    }
    //To assign an existent song to an artist
    @PutMapping("/artists/{uuid}/songs/{id}")
    ResponseEntity<?> assignSongToArtist(@PathVariable String uuid, @PathVariable int id,
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
        boolean isContentManager=false;
        boolean isArtist=false;

        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==1){
                isContentManager=true;
            }
            if(role==2)
            {
                isArtist=true;
            }

        }
        if(!isContentManager || isArtist){
            String name=UserAuthorization.getUserName(UID);
            if(name==null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            else{
                if(name.equals(artistService.findById(uuid).getName())){
                    artistService.assignSong(uuid,id);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }

        }
        if(isContentManager){
            artistService.assignSong(uuid,id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/artists/{uuid}/songs/{id}")
    ResponseEntity<?> deleteSongFromArtist(@PathVariable String uuid,@PathVariable int id,
                                           @RequestHeader (required = false, name="Authorization") String authorization)
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
        boolean isContentManager=false;
        boolean isArtist=false;

        for(int i=0;i<roles.length();i++){
            int role=roles.getInt(i);
            if(role==1){
                isContentManager=true;
            }
            if(role==2)
            {
                isArtist=true;
            }

        }
        if(!isContentManager || isArtist){
            String name=UserAuthorization.getUserName(UID);
            if(name==null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            else{
                if(name.equals(artistService.findById(uuid).getName())){
                    artistService.deleteSong(uuid,id);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }

        }
        if(isContentManager){
            artistService.deleteSong(uuid,id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


    }


}
