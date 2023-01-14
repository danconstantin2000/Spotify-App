package com.example.spotify.services;


import com.example.spotify.DTO.ArtistDTO;
import com.example.spotify.DTO.SongDTO;
import com.example.spotify.entities.Artist;
import com.example.spotify.entities.Song;
import com.example.spotify.enums.Genre;
import com.example.spotify.exceptions.ArtistBadRequestException;
import com.example.spotify.exceptions.ArtistNotFoundException;
import com.example.spotify.exceptions.SongBadRequestException;
import com.example.spotify.exceptions.SongNotFoundException;
import com.example.spotify.repositories.ArtistRepository;
import com.example.spotify.repositories.SongRepository;
import com.example.spotify.services.interfaces.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SongService implements ISongService {
    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    public static SongDTO convertToDTO(Song song) {
        return new SongDTO(song.getId(),song.getName(),song.getGen(),song.getType(),song.getYear());
    }

    public static Song convertToEntity(SongDTO song) {
        return new Song(song.getId(),song.getName(),song.getGen(),song.getType(),song.getYear());
    }

    @Override
    public List<SongDTO> findAll() {
        List<Song> songs=songRepository.findAll();
        List<SongDTO> songsDTO= new ArrayList<>();
        for(Song song : songs){
            songsDTO.add(convertToDTO(song));
        }
        return songsDTO;
    }
    public List<SongDTO> findAllByTitle(String title,String matching){
        List<SongDTO> songsDTO=new ArrayList<>();

        if(matching==null){
            List<Song> songs = songRepository.findByNameContains(title);
            for(Song song : songs){
                songsDTO.add(convertToDTO(song));
            }
        }
        else if (matching.equals("exact")) {
            List<Song> songs=songRepository.findByName(title);
            for(Song song : songs){
                songsDTO.add(convertToDTO(song));
            }
        }

        return songsDTO;
    }
    public List<SongDTO> findAllByGenre(Genre gen){
        List<SongDTO> songsDTO=new ArrayList<>();
        List<Song> songs = songRepository.findByGen(gen);
        for(Song song : songs){
            songsDTO.add(convertToDTO(song));
        }
        return songsDTO;
    }
    public List<SongDTO> findAllByYear(int year){
        List<SongDTO> songsDTO=new ArrayList<>();
        List<Song> songs = songRepository.findByYear(year);
        for(Song song : songs){
            songsDTO.add(convertToDTO(song));
        }
        return songsDTO;
    }

    @Override
    public SongDTO findById(int id) {
        Song song = songRepository.findById(id).orElseThrow(() -> new SongNotFoundException(id));
        return convertToDTO(song);
    }

    @Override
    public void deleteById(int id) {

        Song song=songRepository.findById(id).orElseThrow(()->new SongNotFoundException(id));
        for(Artist artist:song.getArtists()){
            artist.removeSong(song);
            artistRepository.save(artist);
        }
        songRepository.deleteById(id);
    }

    @Override
    public SongDTO insert(SongDTO newSong) {
        return convertToDTO(songRepository.save(convertToEntity(newSong)));
    }

    @Override
    public void update(int id, SongDTO modifiedSong) {
        Song song = songRepository.findById(id).orElseThrow(()->new SongBadRequestException(id));
        SongDTO newSong=convertToDTO(song);
        if(modifiedSong.getName()!=null) {
            newSong.setName(modifiedSong.getName());
        }
        if(modifiedSong.getGen()!=null){
        newSong.setGen(modifiedSong.getGen());
        }
        if(modifiedSong.getType()!=null) {
            newSong.setType(modifiedSong.getType());
        }
        if(modifiedSong.getYear()!=0) {
            newSong.setYear(modifiedSong.getYear());
        }
        songRepository.save(convertToEntity(newSong));
    }

    @Override
    public ArtistDTO addArtist(int id, ArtistDTO artistRequest) {
        Song song = songRepository.findById(id).orElseThrow(()->new SongNotFoundException(id));
        Artist artist= ArtistService.convertToEntity(artistRequest);
        artist.addSong(song);
        Artist  returnedArtist=artistRepository.save(artist);
        song.addArtist(artist);
        songRepository.save(song);
        return ArtistService.convertToDTO(returnedArtist);

    }
    @Override
    public List<ArtistDTO> getAllArtistsBySong(int id){
        List<ArtistDTO> list=new ArrayList<>();
        Song song=songRepository.findById(id).orElseThrow(()->new SongNotFoundException(id));
        for(Artist artist:song.getArtists()){
            list.add(ArtistService.convertToDTO(artist));
        }
        return list;

    }

    @Override
    public void assignArtist(int id, String uuid){


        Song  song= songRepository.findById(id).orElseThrow(()->new SongNotFoundException(id));
        Artist artist = artistRepository.findById(uuid).orElseThrow(()->new ArtistNotFoundException(uuid));
        artist.addSong(song);
        Artist  returnedArtist=artistRepository.save(artist);
        song.addArtist(artist);
        songRepository.save(song);
    }

    @Override
    public void deleteArtist(int id,String uuid){
        Song  song= songRepository.findById(id).orElseThrow(()->new SongNotFoundException(id));
        Artist artist = artistRepository.findById(uuid).orElseThrow(()->new ArtistNotFoundException(uuid));

        if(!song.getArtists().contains(artist)){
            throw new ArtistNotFoundException(uuid);
        }
        else{
            song.removeArtist(artist);
            artist.removeSong(song);
            artistRepository.save(artist);
            songRepository.save(song);
        }
    }
}
