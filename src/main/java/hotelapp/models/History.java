package hotelapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class History {
    private String hotelId;
    private String userName;
    private String time;
    private String hotelName;
    private String link;

    public History(String hotelId, String userName, String time){
        this.hotelId = hotelId;
        this.userName = userName;
        this.time = time;
    }
}
