package server.repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.models.HotelDTO;
import java.util.List;

public interface HotelRepository {
    /**
     * Finds hotel by id
     * @param hotelId
     * @return hotel
     */
    HotelDTO findHotelById(String hotelId);

    /**
     * Finds hotel by partial name.
     * @param name
     * @return List of hotels with that name present
     */
    List<HotelDTO> findHotelByName(String name);

    /**
     * Gets all hotels for the intial load.
     * @return list of all hotels.
     */
    List<HotelDTO> getAllHotels();

    /**
     * Saves the hotel.
     * @param hotel
     * @return boolean no of rows affected.
     */
    boolean save(HotelDTO hotel);

    /**
     * Fetch Latitude and Longitude for all the hotels along with name.
     * @return custom jsonobject containing  lat, long, name.
     */
    List<JsonObject> fetchLatLang();
}
