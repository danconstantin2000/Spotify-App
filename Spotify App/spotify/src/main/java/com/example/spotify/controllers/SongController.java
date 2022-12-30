package com.example.spotify.controllers;

import com.example.spotify.DTO.ArtistDTO;
import com.example.spotify.DTO.SongDTO;
import com.example.spotify.entities.Song;
import com.example.spotify.enums.Genre;
import com.example.spotify.exceptions.SongBadRequestException;
import com.example.spotify.services.interfaces.ISongService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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
public class SongController {
    @Autowired
    private ISongService songService;
    @GetMapping("/songs")
    public ResponseEntity<?> all(@RequestParam(required = false, name="title") String title,
                                 @RequestParam(required = false, name="matching") String matching,
                                 @RequestParam(required = false, name="year")Integer year,
                                 @RequestParam(required = false,name="gen") Genre gen,
                                @RequestParam(required = false,name="page") Integer page,
                                @RequestParam(required = false,name="items_per_page") Integer itemsPerPage)
    {
        if(!StringUtils.isEmpty(page)){

                if(StringUtils.isEmpty(itemsPerPage)){
                   itemsPerPage=3;
                }
                List<SongDTO> songs = songService.findAll();
                if(page<=0) {
                    return  new ResponseEntity(HttpStatus.BAD_REQUEST);
                }
                List<SongDTO> rightSongs= new ArrayList<>();
                for(int i=itemsPerPage*(page-1);i<itemsPerPage*page;i++){

                    rightSongs.add(songs.get(i));
                }
                List<EntityModel<SongDTO>> newSongs=rightSongs.stream()
                        .map(song -> EntityModel.of(song,
                                linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withRel("songs"),
                                linkTo(methodOn(SongController.class).getAllFromSong(song.getId())).withRel("artists")

                        )).collect(Collectors.toList());

                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(newSongs, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withSelfRel(),
                        BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs?page="+(page+1)+"&items_per_page="+itemsPerPage).withRel("next"),
                        BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs?page="+(page-1)+"&items_per_page="+itemsPerPage).withRel("prev")));

        }
        else {
            if (!StringUtils.isEmpty(title) && StringUtils.isEmpty(year) && StringUtils.isEmpty(gen)) {

                if (StringUtils.isEmpty(matching)) {
                    List<EntityModel<SongDTO>> songs = songService.findAllByTitle(title, matching).stream()
                            .map(song -> EntityModel.of(song,
                                    linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                                    BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/songs").withRel("songs"),
                                    linkTo(methodOn(SongController.class).getAllFromSong(song.getId())).withRel("artists")
                            )).collect(Collectors.toList());

                    return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(songs, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/songs").slash("?title=" + title.replace(" ", "%20")).withSelfRel()));
                } else {
                    List<EntityModel<SongDTO>> songs = songService.findAllByTitle(title, matching).stream()
                            .map(song -> EntityModel.of(song,
                                    linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                                    BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/songs").withRel("songs"),
                                    linkTo(methodOn(SongController.class).getAllFromSong(song.getId())).withRel("artists")
                            )).collect(Collectors.toList());

                    return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(songs, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/songs").slash("?title=" + title.replace(" ", "%20") + "&matching=" + matching.replace(" ", "%20")).withSelfRel()));
                }
            } else if (StringUtils.isEmpty(title) && !StringUtils.isEmpty(year) && StringUtils.isEmpty(gen)) {

                List<EntityModel<SongDTO>> songs = songService.findAllByYear(year).stream()
                        .map(song -> EntityModel.of(song,
                                linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/songs").withRel("songs"),
                                linkTo(methodOn(SongController.class).getAllFromSong(song.getId())).withRel("artists")
                        )).collect(Collectors.toList());

                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(songs, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/songs").slash("?year=" + year).withSelfRel()));
            } else if (StringUtils.isEmpty(title) && StringUtils.isEmpty(year) && !StringUtils.isEmpty(gen)) {

                List<EntityModel<SongDTO>> songs = songService.findAllByGenre(gen).stream()
                        .map(song -> EntityModel.of(song,
                                linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/songs").withRel("songs"),
                                linkTo(methodOn(SongController.class).getAllFromSong(song.getId())).withRel("artists")
                        )).collect(Collectors.toList());

                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(songs, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/songs").slash("?gen=" + gen).withSelfRel()));
            } else {
                List<EntityModel<SongDTO>> songs = songService.findAll().stream()
                        .map(song -> EntityModel.of(song,
                                linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withRel("songs"),
                                linkTo(methodOn(SongController.class).getAllFromSong(song.getId())).withRel("artists")
                        )).collect(Collectors.toList());

                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(songs, BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withSelfRel()));

            }
        }


    }

    @GetMapping("/songs/{id}")
    ResponseEntity<?> one(@PathVariable int id) {
        SongDTO song=songService.findById(id);
        return ResponseEntity.status((HttpStatus.OK)).body(EntityModel.of(song,
                linkTo(methodOn(SongController.class).one(id)).withSelfRel(),
                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withRel("songs"),
                linkTo(methodOn(SongController.class).getAllFromSong(song.getId())).withRel("artists")
                )
        );
    }
    @DeleteMapping("/songs/{id}")
    ResponseEntity<?> deleteSong(@PathVariable int id){
        SongDTO song = songService.findById(id);

        ResponseEntity<EntityModel<SongDTO>> response=ResponseEntity.status((HttpStatus.OK)).body(EntityModel.of(song,
                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withRel("songs")));
        songService.deleteById(id);
        return response;
    }

    @PostMapping("/songs")
    ResponseEntity<?> newSong(@RequestBody SongDTO newSong){
        JSONObject response=new JSONObject();
        SongDTO song = null;
        try {
            song = songService.insert(newSong);
        }
        catch (DataIntegrityViolationException ex){
            response.put("message","A constraint was violated!");
            return new ResponseEntity<>(response,HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(EntityModel.of(song,
                linkTo(methodOn(SongController.class).one(song.getId())).withSelfRel(),
                BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection").slash("songs").withRel("songs")
        ),HttpStatus.CREATED);
    }

    @PutMapping("/songs/{id}")
    ResponseEntity<?> updateSong(@PathVariable int id,@RequestBody SongDTO modifiedSong){

        JSONObject response=new JSONObject();
        try {
            songService.update(id, modifiedSong);
        }
        catch (DataIntegrityViolationException ex) {
            response.put("message", "A constraint was violated!");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }
    @PostMapping("/songs/{id}/artists")
    ResponseEntity<?> addArtist(@PathVariable int id, @RequestBody List<ArtistDTO> artists) {
        JSONObject response=new JSONObject();
        String message="Success";
        List<ArtistDTO> successfullArtists=new ArrayList<>();
        for(ArtistDTO artist : artists){
            try {
                int index=artists.indexOf(artist);
                artist.setUuid(UUID.randomUUID().toString());
                ArtistDTO createdArtist=songService.addArtist(id, artist);
                artists.set(index,createdArtist );
                successfullArtists.add(createdArtist);
            }
            catch (DataIntegrityViolationException ex) {

                message="A constraint was violated!";
            }

        }
        response.put("message",message);
        if(successfullArtists.isEmpty()){
            return new ResponseEntity<>(response,HttpStatus.NOT_ACCEPTABLE);
        }

        List<EntityModel<ArtistDTO>> returnedArtists=successfullArtists.stream()
                .map(artist->EntityModel.of(artist,
                        linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                        linkTo(methodOn(SongController.class).getAllFromSong(id)).withRel("parent"))).collect(Collectors.toList());
        response.put("artists",returnedArtists);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @GetMapping("/songs/{id}/artists")
    ResponseEntity<?> getAllFromSong(@PathVariable int id){
        List<EntityModel<ArtistDTO>> artists=songService.getAllArtistsBySong(id).stream()
                .map(artist->EntityModel.of(artist,
                        linkTo(methodOn(ArtistController.class).one(artist.getUuid())).withSelfRel(),
                        BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists"),
                        linkTo(methodOn(SongController.class).one(id)).withRel("song")
                        )).collect(Collectors.toList());

        return new ResponseEntity<>(CollectionModel.of(artists,BasicLinkBuilder.linkToCurrentMapping().slash("/api/songcollection/artists").withRel("artists")),HttpStatus.OK);

    }
    @PutMapping("/songs/{id}/artists/{uuid}")
    ResponseEntity<?> assignSongToArtist(@PathVariable int id,@PathVariable String uuid){

        songService.assignArtist(id,uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/songs/{id}/artists/{uuid}")
    ResponseEntity<?> deleteSongFromArtist(@PathVariable int id,@PathVariable String uuid){
        songService.deleteArtist(id,uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
