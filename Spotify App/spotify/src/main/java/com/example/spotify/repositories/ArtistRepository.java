package com.example.spotify.repositories;

import com.example.spotify.entities.Artist;
import com.example.spotify.entities.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String > {
    List<Artist> findByName(String name);
    List<Artist> findByNameContains(String name);
}
