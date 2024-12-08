package server.repositories;

import hotelapp.models.HotelDTO;
import java.util.List;

public interface HotelRepository {
    HotelDTO findHotelById(String hotelId);
    List<HotelDTO> findHotelByName(String name);
    List<HotelDTO> getAllHotels();
    boolean save(HotelDTO hotel);
}
