package pos.mongodb_application.playlist.services.interfaces;

import pos.mongodb_application.playlist.dto.InputPlaylistDto;
import pos.mongodb_application.playlist.models.Playlist;
import pos.mongodb_application.playlist.models.UserPlaylist;

import java.util.List;

public interface IPlaylistService {

     Playlist createPlayList(InputPlaylistDto inputPlaylistDto);
     void addSongToPlayList(int id,String playlistId);
     public List<Playlist> getAllPlaylists();
     public Playlist getPlaylist(String id);
     public Playlist deletePlaylist(String id);
     public Playlist removeFromPlaylist(String id,int songId);
}
