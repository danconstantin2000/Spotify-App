package com.example.spotify.entities;

import com.example.spotify.enums.Genre;
import com.example.spotify.enums.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name="name",unique = true)
    private String name;

    @Column(name="gen")
    @Enumerated(EnumType.STRING)
    private Genre gen;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name="year")
    private int year;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST
            },
            mappedBy = "songs")
    private Set<Artist> artists = new HashSet<>();

    public Song(){}
    public Song(int id, String name, Genre gen, Type type, int year) {
        this.id = id;
        this.name = name;
        this.gen = gen;
        this.type = type;
        this.year = year;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Genre getGen() {
        return gen;
    }

    public void setGen(Genre gen) {
        this.gen = gen;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void addArtist(Artist artist){
        artists.add(artist);
    }
    public void removeArtist(Artist artist){
        artists.remove(artist);
    }

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }
}
