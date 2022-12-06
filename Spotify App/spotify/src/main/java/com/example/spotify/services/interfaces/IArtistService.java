package com.example.spotify.services.interfaces;

import com.example.spotify.DTO.ArtistDTO;
import com.example.spotify.DTO.SongDTO;
import com.example.spotify.entities.Artist;
import com.example.spotify.entities.Song;

import java.util.List;
import java.util.Set;

public interface IArtistService {

    List<ArtistDTO> findAll();
    ArtistDTO findById(String uuid);
    void deleteById(String uuid);
    ArtistDTO insert(ArtistDTO newArtist);
    void update(String uuid,ArtistDTO modifiedArtist);
    List<SongDTO> getAllSongsByArtist(String uuid);
    public SongDTO addSong(String uuid, SongDTO songRequest);
    public void assignSong(String uuid, int id);
    public void deleteSong(String uuid,int id);
}
