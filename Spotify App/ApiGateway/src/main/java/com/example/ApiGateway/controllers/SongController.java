package com.example.ApiGateway.controllers;


import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/gateway")
public class SongController
{    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .build();
    @GetMapping("/songs")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getSongs( @RequestParam(required = true,name="page") Integer page){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8081/api/songcollection/songs?page="+page))
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        JSONObject body= new JSONObject(response.body());
        HttpStatusCode statusCode= HttpStatusCode.valueOf(response.statusCode());
        return new ResponseEntity<>(body.toMap(),statusCode);
    }

    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getSong(@PathVariable Integer id){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8081/api/songcollection/songs/"+id))
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        JSONObject body= new JSONObject(response.body());
        HttpStatusCode statusCode= HttpStatusCode.valueOf(response.statusCode());
        return new ResponseEntity<>(body.toMap(),statusCode);
    }


    @GetMapping("/songs/{id}/artists")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> getArtists(@PathVariable Integer id){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8081/api/songcollection/songs/"+id+"/artists"))
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        JSONObject body= new JSONObject(response.body());
        HttpStatusCode statusCode= HttpStatusCode.valueOf(response.statusCode());
        return new ResponseEntity<>(body.toMap(),statusCode);
    }
}
