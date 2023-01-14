package pos.mongodb_application.playlist.services;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import pos.mongodb_application.playlist.dto.InputPlaylistDto;
import pos.mongodb_application.playlist.dto.InputUserPlaylistDto;
import pos.mongodb_application.playlist.exceptions.BadRequestException;
import pos.mongodb_application.playlist.exceptions.ConflictException;
import pos.mongodb_application.playlist.models.Playlist;
import pos.mongodb_application.playlist.models.Song;
import pos.mongodb_application.playlist.models.UserPlaylist;
import pos.mongodb_application.playlist.repositories.UserPlaylistCollectionRepository;
import pos.mongodb_application.playlist.services.interfaces.IPlaylistService;
import pos.mongodb_application.playlist.services.interfaces.IUserPlaylistService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static pos.mongodb_application.playlist.parsers.Parser.convertStringToDocument;


@Service
public class UserPlaylistService implements IUserPlaylistService {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    @Autowired
    UserPlaylistCollectionRepository userPlaylistCollectionRepository;
    @Autowired
    IPlaylistService playlistService;
    @Override
    public UserPlaylist createUserPLayList(InputUserPlaylistDto inputUserPlaylistDto) {

        UserPlaylist userPlaylist = userPlaylistCollectionRepository.findByUid(inputUserPlaylistDto.getUid());
        if (userPlaylist == null) {

            userPlaylist = new UserPlaylist();
            String requestBody ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:service=\"services.IDM.soap\">\n" +
                    "    <soap11env:Body>\n" +
                    "        <service:get_user>\n" +
                    "            <service:UID>"+inputUserPlaylistDto.getUid()+"</service:UID>\n" +
                    "        </service:get_user>\n" +
                    "    </soap11env:Body>\n" +
                    "</soap11env:Envelope>";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8000/"))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Document doc = convertStringToDocument(response.body());
            if(doc.getElementsByTagName("soap11env:Fault").getLength()!=0){
                return null;
            }
            userPlaylist.setUid(inputUserPlaylistDto.getUid());
            userPlaylist.setUsername(doc.getElementsByTagName("tns:username").item(0).getTextContent());
            return userPlaylistCollectionRepository.save(userPlaylist);

        }
        else{
            throw new ConflictException();
        }

    }
    public UserPlaylist addNewPlayList(InputPlaylistDto inputPlaylistDto, String userPlaylistId){
        Optional<UserPlaylist> optionalUserPlaylist= userPlaylistCollectionRepository.findById(userPlaylistId);
        if(optionalUserPlaylist.isEmpty()){
            return null;
        }
        UserPlaylist userPlaylist=optionalUserPlaylist.get();
        Playlist playlist= playlistService.createPlayList(inputPlaylistDto);
        userPlaylist.addToPlaylist(playlist);
        return userPlaylistCollectionRepository.save(userPlaylist);

    }
    public List<UserPlaylist> findAll(){
        return userPlaylistCollectionRepository.findAll();
    }

    public UserPlaylist findById(String userPlaylistId){
        Optional<UserPlaylist> optionalUserPlaylist= userPlaylistCollectionRepository.findById(userPlaylistId);
        if(optionalUserPlaylist.isEmpty()){
            return null;
        }
        UserPlaylist userPlaylist=optionalUserPlaylist.get();
        return  userPlaylist;
    }
    public UserPlaylist findByUid(int uid){
        UserPlaylist optionalUserPlaylist= userPlaylistCollectionRepository.findByUid(uid);
        return  optionalUserPlaylist;
    }
    public UserPlaylist putSongToPlaylist(String userPlaylistId,String playlistId,int songId){
        Optional<UserPlaylist> optionalUserPlaylist= userPlaylistCollectionRepository.findById(userPlaylistId);
        if(optionalUserPlaylist.isEmpty()){
            return null;
        }
        UserPlaylist userPlaylist=optionalUserPlaylist.get();
        List<Playlist> playlists=userPlaylist.getPlaylists();
        for(Playlist playlist:playlists){
            if(playlist.getId().equals(playlistId)){
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create("http://localhost:8081/api/songcollection/songs/"+songId))
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
                song.setId(songId);
                song.setName(songName);
                song.setSelf(self);
                playlist.addSong(song);
            }
        }
        userPlaylistCollectionRepository.save(userPlaylist);
        return userPlaylist;
    }
    public UserPlaylist deleteSongFromPlaylist(String userPlaylistId,String playlistId,int songId)
    {
        Optional<UserPlaylist> optionalUserPlaylist= userPlaylistCollectionRepository.findById(userPlaylistId);
        if(optionalUserPlaylist.isEmpty()){
            return null;
        }
        UserPlaylist userPlaylist=optionalUserPlaylist.get();
        List<Playlist> playlists=userPlaylist.getPlaylists();
        for(Playlist playlist:playlists){
            if(playlist.getId().equals(playlistId)) {
                List<Song> songs = playlist.getSongs();
                int index = -1;
                for(Song song:songs){
                    if(song.getId()==songId){
                        index=songs.indexOf(song);
                        break;
                    }
                }
                if(index!=-1) {
                    songs.remove(index);
                }
                else{
                    return null;
                }
            }
        }
        userPlaylistCollectionRepository.save(userPlaylist);
        return userPlaylist;
    }
    public UserPlaylist deletePlaylist(String userPlaylistId, String playlistId){

        Optional<UserPlaylist> optionalUserPlaylist= userPlaylistCollectionRepository.findById(userPlaylistId);
        if(optionalUserPlaylist.isEmpty()){
            return null;
        }
        UserPlaylist userPlaylist=optionalUserPlaylist.get();
        List<Playlist> playlists=userPlaylist.getPlaylists();
        int index=-1;
        for(Playlist playlist:playlists){

            if(playlist.getId().equals(playlistId)) {
               index=playlists.indexOf(playlist);
            }

        }
        if(index!=-1){
            playlists.remove(index);
        }
        else{
            return null;
        }
        userPlaylistCollectionRepository.save(userPlaylist);
        return userPlaylist;
    }
    public Playlist getPlaylist(String userPlaylistId, String playlistId){

        Optional<UserPlaylist> optionalUserPlaylist= userPlaylistCollectionRepository.findById(userPlaylistId);
        if(optionalUserPlaylist.isEmpty()){
            return null;
        }
        UserPlaylist userPlaylist=optionalUserPlaylist.get();
        List<Playlist> playlists=userPlaylist.getPlaylists();
        int index=-1;
        for(Playlist playlist:playlists){

            if(playlist.getId().equals(playlistId)) {
                index=playlists.indexOf(playlist);
            }

        }
        if(index!=-1){
            return playlists.get(index);
        }
        else{
            return null;
        }
    }
}
