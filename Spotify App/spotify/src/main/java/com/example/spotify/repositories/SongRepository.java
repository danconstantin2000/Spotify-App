package com.example.spotify.repositories;

import com.example.spotify.entities.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer > {


}
