package pos.mongodb_application.playlist.dto;

import lombok.Data;

@Data
public class InputPlaylistDto {
    private String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
