package pos.mongodb_application.playlist.services.interfaces;

import pos.mongodb_application.playlist.dto.InputPlaylistDto;
import pos.mongodb_application.playlist.dto.InputUserPlaylistDto;
import pos.mongodb_application.playlist.models.UserPlaylist;

import java.util.List;

public interface IUserPlaylistService {
    UserPlaylist createUserPLayList(InputUserPlaylistDto inputUserPlaylistDto);
    UserPlaylist addNewPlayList(InputPlaylistDto inputPlaylistDto, String userPlaylistId);
    List<UserPlaylist> findAll();
}
