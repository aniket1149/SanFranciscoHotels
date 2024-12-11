package hotelapp.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class HotelDTO implements Comparable{
    @SerializedName(value = "f")
    private String name;
    @SerializedName(value = "ad")
    private String streetAddress;
    @SerializedName(value = "ci")
    private String city;
    @SerializedName(value = "id")
    private String id;
    @SerializedName(value = "pr")
    private String state;
    private GeoTags ll;
    private String link;
    private String rating;

    //private Object ll;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getLat() { return  ll.getLat();}

    public String getLng() { return  ll.getLng();}
    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return
                name+": " +id+  System.lineSeparator() +
                        streetAddress + System.lineSeparator() +
                        city+", " + state + System.lineSeparator();
    }

    @Override
    public int compareTo(Object o) {
        return this.id.compareTo(((HotelDTO) o).id);
    }


}
