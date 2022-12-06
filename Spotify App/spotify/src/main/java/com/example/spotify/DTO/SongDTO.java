package com.example.spotify.DTO;

import com.example.spotify.enums.Genre;
import com.example.spotify.enums.Type;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = false)
public class SongDTO {
    private int id;
    private String name;
    private Genre gen;
    private Type type;
    private int year;

    public SongDTO(){}

    public SongDTO(int id, String name, Genre gen, Type type, int year) {
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





}
