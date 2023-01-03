package pos.mongodb_application.playlist.services;

import com.sun.net.httpserver.Request;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.mongodb_application.playlist.dto.InputPlaylistDto;
import pos.mongodb_application.playlist.exceptions.BadRequestException;
import pos.mongodb_application.playlist.exceptions.ConflictException;
import pos.mongodb_application.playlist.models.Playlist;
import pos.mongodb_application.playlist.models.Song;
import pos.mongodb_application.playlist.models.UserPlaylist;
import pos.mongodb_application.playlist.repositories.PlaylistCollectionRepository;
import pos.mongodb_application.playlist.repositories.UserPlaylistCollectionRepository;
import pos.mongodb_application.playlist.services.interfaces.IPlaylistService;
import pos.mongodb_application.playlist.services.interfaces.IUserPlaylistService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static pos.mongodb_application.playlist.parsers.Parser.buildFormDataFromMap;

@Service
public class PlaylistService implements IPlaylistService {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    @Autowired
    PlaylistCollectionRepository playlistCollectionRepository;

    @Autowired
    UserPlaylistCollectionRepository userPlaylistCollectionRepository;
    @Override
    public Playlist createPlayList(InputPlaylistDto inputPlaylistDto){

            Playlist newPlaylist = new Playlist();
            newPlaylist.setTitle(inputPlaylistDto.getTitle());
            return playlistCollectionRepository.save(newPlaylist);
    }

    @Override
    public void addSongToPlayList(int id,String playlistId){


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



        JSONObject obj = new JSONObject(response.body());
        if(obj.has("error")){
            throw new BadRequestException();
        }
        String songName=obj.getString("name");
        String self=obj.getJSONObject("_links").getJSONObject("self").getString("href");
        Song song = new Song();
        song.setId(id);
        song.setName(songName);
        song.setSelf(self);
        Optional<Playlist> optionalPlaylist=playlistCollectionRepository.findById(playlistId);
        if(optionalPlaylist.isEmpty()){
            throw new BadRequestException();
        }
        Playlist playlist=optionalPlaylist.get();
        List<Song> songs=playlist.getSongs();
        for(Song s :songs){
            if(s.getId()==id){
                throw new ConflictException();
            }
        }
        playlist.addSong(song);
        playlistCollectionRepository.save(playlist);
        List<UserPlaylist> usersPlayLists=userPlaylistCollectionRepository.findAll();
        for(UserPlaylist user: usersPlayLists){
            List<Playlist> playlists=user.getPlaylists();
            for(Playlist p: playlists){
                if(p.getId().equals(playlistId)){
                    p.addSong(song);
                }
            }
            userPlaylistCollectionRepository.save(user);

        }
    }
    @Override
    public List<Playlist> getAllPlaylists(){
        return playlistCollectionRepository.findAll();
    }
    @Override
    public Playlist getPlaylist(String id){
        Optional<Playlist> optionalPlaylist= playlistCollectionRepository.findById(id);
        if(optionalPlaylist.isEmpty()){
            return null;
        }
        return optionalPlaylist.get();
    }

    @Override
    public Playlist deletePlaylist(String id){
        Optional<Playlist> optionalPlaylist= playlistCollectionRepository.findById(id);
        if(optionalPlaylist.isEmpty()){
            return null;
        }

        playlistCollectionRepository.deleteById(id);
        return optionalPlaylist.get();
    }
    @Override
    public Playlist removeFromPlaylist(String id,int songId){
        Optional<Playlist> optionalPlaylist= playlistCollectionRepository.findById(id);
        if(optionalPlaylist.isEmpty()){
            return null;
        }
        Playlist playlist=optionalPlaylist.get();
        boolean found =false;
        List<Song> songs=playlist.getSongs();
        for(int i=0;i<songs.size();i++){
            if(songs.get(i).getId()==songId){
                found = true;
            }
        }
        if(!found){
            return  null;
        }
        for(int i=0;i<songs.size();i++){
            if(songs.get(i).getId()==songId){
                songs.remove(songs.get(i));
            }
        }
        playlistCollectionRepository.save(playlist);
        return playlist;

    }

}
