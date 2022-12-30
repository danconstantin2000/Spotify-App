package com.example.spotify.services;

import com.example.spotify.DTO.ArtistDTO;
import com.example.spotify.DTO.SongDTO;
import com.example.spotify.entities.Artist;
import com.example.spotify.entities.Song;
import com.example.spotify.exceptions.ArtistBadRequestException;
import com.example.spotify.exceptions.ArtistNotFoundException;
import com.example.spotify.exceptions.SongNotFoundException;
import com.example.spotify.repositories.ArtistRepository;
import com.example.spotify.repositories.SongRepository;
import com.example.spotify.services.interfaces.IArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ArtistService implements IArtistService {

    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private SongRepository songRepository;
    public static ArtistDTO convertToDTO(Artist artist)
    {
        return new ArtistDTO(artist.getUuid(),artist.getName(),artist.getActive());
    }

    public static Artist convertToEntity(ArtistDTO artist){
        return new Artist(artist.getUuid(),artist.getName(),artist.isActive());
    }

    @Override
    public List<ArtistDTO> findAll(){
        List<Artist> artists=artistRepository.findAll();
        List<ArtistDTO> artistsDTO= new ArrayList<>();
        for(Artist artist : artists){
            artistsDTO.add(convertToDTO(artist));
        }
        return artistsDTO;
    }

    @Override
    public List<ArtistDTO> findAllByName(String name,String matching){
        List<ArtistDTO> artistDTO=new ArrayList<>();

        if(matching==null){
            List<Artist> artists = artistRepository.findByNameContains(name);
            for(Artist artist : artists){
                artistDTO.add(convertToDTO(artist));
            }
        }
        else if (matching.equals("exact")) {
            List<Artist> artists=artistRepository.findByName(name);
            for(Artist artist : artists){
                artistDTO.add(convertToDTO(artist));
            }
        }
        return artistDTO;
    }
    @Override
    public ArtistDTO findById(String uuid){
        Artist artist = artistRepository.findById(uuid).orElseThrow(() -> new ArtistNotFoundException(uuid));
        return convertToDTO(artist);
    }
    @Override
    public void deleteById(String uuid){
        artistRepository.deleteById(uuid);
    }
    @Override
    public ArtistDTO insert(ArtistDTO newArtist){

       return convertToDTO(artistRepository.save(convertToEntity(newArtist)));

    }
    @Override
    public void update(String uuid,ArtistDTO modifiedArtist){
        Artist artist = artistRepository.findById(uuid).orElseThrow(()->new ArtistBadRequestException(uuid));
        ArtistDTO newArtist=convertToDTO(artist);
        newArtist.setActive(modifiedArtist.isActive());
        newArtist.setName(modifiedArtist.getName());
        artistRepository.save(convertToEntity(newArtist));

    }

    public SongDTO addSong(String uuid, SongDTO songRequest) {
            Artist artist = artistRepository.findById(uuid).orElseThrow(()->new ArtistNotFoundException(uuid));
            Song song= SongService.convertToEntity(songRequest);
            song.addArtist(artist);
            Song returnedSong=songRepository.save(song);
            artist.addSong(song);
            artistRepository.save(artist);
            return SongService.convertToDTO(returnedSong);

    }
    @Override
    public List<SongDTO> getAllSongsByArtist(String uuid){
       List<SongDTO> list=new ArrayList<>();
       Artist artist=artistRepository.findById(uuid).orElseThrow(()->new ArtistNotFoundException(uuid));
       for(Song song:artist.getSongs()){
           list.add(SongService.convertToDTO(song));
       }
       return list;

    }
    @Override
    public void assignSong(String uuid, int id){

        Artist artist = artistRepository.findById(uuid).orElseThrow(()->new ArtistNotFoundException(uuid));
        Song  song= songRepository.findById(id).orElseThrow(()->new SongNotFoundException(id));
        song.addArtist(artist);
        songRepository.save(song);
        artist.addSong(song);
        artistRepository.save(artist);
    }

    @Override
    public void deleteSong(String uuid,int id){
        Artist artist = artistRepository.findById(uuid).orElseThrow(()->new ArtistNotFoundException(uuid));
        Song  song= songRepository.findById(id).orElseThrow(()->new SongNotFoundException(id));
        if(!artist.getSongs().contains(song)){
            throw new SongNotFoundException(id);
        }
        else{
            artist.removeSong(song);
            song.removeArtist(artist);
            songRepository.save(song);
            artistRepository.save(artist);
        }
    }
}
