package pos.mongodb_application.playlist.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pos.mongodb_application.playlist.models.Playlist;
import pos.mongodb_application.playlist.models.UserPlaylist;

@Repository
public interface PlaylistCollectionRepository extends MongoRepository<Playlist, String>
{

    String findByTitle(String title);
}
