package com.example.spotify.repositories;

import com.example.spotify.entities.Song;
import com.example.spotify.enums.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer > {
    List<Song> findByName(String name);
    List<Song> findByNameContains(String name);
    List<Song> findByGen(Genre gen);
    List<Song> findByYear(int year);
}
