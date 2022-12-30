package com.example.spotify.services.interfaces;

import com.example.spotify.DTO.ArtistDTO;
import com.example.spotify.DTO.SongDTO;
import com.example.spotify.entities.Artist;
import com.example.spotify.entities.Song;
import com.example.spotify.enums.Genre;

import java.util.List;

public interface ISongService {

    List<SongDTO> findAll();
    List<SongDTO> findAllByTitle(String title,String matching);
    List<SongDTO> findAllByGenre(Genre gen);
    List<SongDTO> findAllByYear(int year);
    SongDTO findById(int id);
    void deleteById(int id);
    SongDTO insert(SongDTO newSong);
    void update(int id,SongDTO modifiedSong);
    public ArtistDTO addArtist(int id, ArtistDTO artistRequest);
    public List<ArtistDTO> getAllArtistsBySong(int id);
    public void assignArtist(int id, String uuid);
    public void deleteArtist(int id,String uuid);
}
