package server.repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.models.HotelDTO;
import java.util.List;

public interface HotelRepository {
    HotelDTO findHotelById(String hotelId);
    List<HotelDTO> findHotelByName(String name);
    List<HotelDTO> getAllHotels();
    boolean save(HotelDTO hotel);
    List<JsonObject> fetchLatLang();
}
