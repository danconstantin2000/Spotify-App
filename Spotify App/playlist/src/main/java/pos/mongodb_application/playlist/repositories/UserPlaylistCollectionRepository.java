package pos.mongodb_application.playlist.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pos.mongodb_application.playlist.models.Playlist;
import pos.mongodb_application.playlist.models.UserPlaylist;

@Repository
public interface UserPlaylistCollectionRepository extends MongoRepository<UserPlaylist, String>
{
        UserPlaylist findByUid(int id);
}
