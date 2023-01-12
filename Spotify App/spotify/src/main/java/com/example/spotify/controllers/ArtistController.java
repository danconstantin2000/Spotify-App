package com.example.spotify.controllers;

import com.example.spotify.DTO.ArtistDTO;
import com.example.spotify.DTO.SongDTO;
import com.example.spotify.entities.Song;
import com.example.spotify.services.interfaces.IArtistService;
import net.minidev.json.JSONObject;
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
                                 @RequestParam(required = false,name="items_per_page") Integer itemsPerPage,
                                 @RequestHeader(required = false,name="Authorization") String authorization)
    {
        if(authorization==null || !authorization.contains("Bearer ")
        {

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        System.out.println(authorization);
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

                fitArtists.add(artists.get(i));
            }
            List<EntityModel<ArtistDTO>> newArtists=fitArtists.stream()
                    .map(artist -> EntityModel.of(artist,
                            linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                            BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("artists").withRel("artists"),
                            linkTo(methodOn(ArtistController.class).getAllFromArtist(artist.getUuid())).withRel("songs")

                    )).collect(Collectors.toList());

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
    ResponseEntity<?> deleteArtist(@PathVariable String uuid){
        ArtistDTO artist = artistService.findById(uuid);
        ResponseEntity<EntityModel<ArtistDTO>> response=ResponseEntity.status((HttpStatus.OK)).body(EntityModel.of(artist,
                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists")

        ));
        artistService.deleteById(uuid);
        return response;
    }

    @PostMapping("/artists")
    ResponseEntity<?> newArtist(@RequestBody  ArtistDTO newArtist){
        newArtist.setUuid(UUID.randomUUID().toString());
        JSONObject response=new JSONObject();
        ArtistDTO artist = null;
        try {
            artist = artistService.insert(newArtist);
        }
        catch (DataIntegrityViolationException ex){
            response.put("message","A constraint was violated!");
            return new ResponseEntity<>(response,HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(EntityModel.of(artist,
                linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists")
        ),HttpStatus.CREATED);
    }

    @PutMapping("/artists/{uuid}")
    ResponseEntity<?> updateArtist(@PathVariable String uuid,@RequestBody ArtistDTO modifiedArtist){
        JSONObject response=new JSONObject();
        try {
            artistService.update(uuid, modifiedArtist);
        }
        catch (DataIntegrityViolationException ex) {
            response.put("message", "A constraint was violated!");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    //Add in songs table all songs from lists and asign to uuid artist
    @PostMapping("/artists/{uuid}/songs")
    ResponseEntity<?> addSong(@PathVariable String uuid, @RequestBody List<SongDTO> songs) {

        JSONObject response=new JSONObject();
        String message="Success";
        List<SongDTO> successfullSongs=new ArrayList<>();
        for(SongDTO song : songs){
            try {
                int index=songs.indexOf(song);
                SongDTO createdSong=artistService.addSong(uuid, song);
                songs.set(index, createdSong);
                successfullSongs.add(createdSong);
            }
            catch (DataIntegrityViolationException ex) {
                message="A constraint was violated!";
            }

        }
        response.put("message",message);
        if(successfullSongs.isEmpty()){
            return new ResponseEntity<>(response,HttpStatus.NOT_ACCEPTABLE);
        }
        List<EntityModel<SongDTO>> returnedSongs=successfullSongs.stream()
                .map(song->EntityModel.of(song,
                        linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                        linkTo(methodOn(ArtistController.class).getAllFromArtist(uuid)).withRel("parent"))).collect(Collectors.toList());
        response.put("songs",returnedSongs);
        return new ResponseEntity<>(response, HttpStatus.OK);

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
    ResponseEntity<?> assignSongToArtist(@PathVariable String uuid, @PathVariable int id){

        artistService.assignSong(uuid,id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/artists/{uuid}/songs/{id}")
    ResponseEntity<?> deleteSongFromArtist(@PathVariable String uuid,@PathVariable int id){
        artistService.deleteSong(uuid,id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
