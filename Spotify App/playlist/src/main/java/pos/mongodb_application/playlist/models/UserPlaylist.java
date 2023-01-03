package pos.mongodb_application.playlist.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "userPlaylist")
public class UserPlaylist {

    @Id
    private String id;
    private int uid;
    private String username;


    private List<Playlist> playlists = new ArrayList<>();

    public boolean addToPlaylist(Playlist playlist){
        return playlists.add(playlist);
    }

}
