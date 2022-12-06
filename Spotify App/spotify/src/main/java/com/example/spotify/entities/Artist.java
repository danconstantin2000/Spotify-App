package com.example.spotify.entities;

import com.example.spotify.DTO.ArtistDTO;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="artists")
public class Artist {
    @Id
    @Column(name="uuid")
    private String uuid;
    @Column(name="name",unique=true,nullable = false)
    private String name;

    @Column(name="active" ,nullable = false)
    private Boolean active;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "artists_songs",
            joinColumns = { @JoinColumn(name = "artist_id") },
            inverseJoinColumns = { @JoinColumn(name = "song_id") })
    private Set<Song> songs = new HashSet<>();
    public Artist(){

    }
    public Artist(String uuid, String name, Boolean active) {
        this.uuid = uuid;
        this.name = name;
        this.active = active;

    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song){
        this.songs.add(song);
    }

    public void removeSong(Song song){
        this.songs.remove(song);
    }


}
