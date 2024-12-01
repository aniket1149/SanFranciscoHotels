package hotelapp.controller;

import hotelapp.models.HotelDTO;

import java.util.*;

/*
Hotel Class collects hotels parsed by json processor and creates hotel object.
HotelMap : maps HotelId with hotel object
findHotel returns Hotel obj if found else throws Exception
 */

public class HotelCollection {
    private TreeMap<String, HotelDTO> hotelIDMap;
    private TreeSet<HotelDTO> hotelSet;

    public HotelCollection(Set<HotelDTO> hotelSet) {
        this.hotelIDMap = new TreeMap<>((a,b)->a.compareTo(b));
        this.hotelSet = new TreeSet<HotelDTO>(hotelSet);
        initializeMap(hotelSet);
    }
    private void initializeMap(Set<HotelDTO> hotels){
        for(HotelDTO hotel : hotels){
            hotelIDMap.put(hotel.getId(), hotel);
        }
    }

    public SortedSet<HotelDTO> getHotelDtos(){
        return Collections.unmodifiableSortedSet(hotelSet);
    }

    public String findHotel(String query){
        if(hotelIDMap.containsKey(query))
        return hotelIDMap.get(query).toString();
        else return "Hotel not found";
    }

    public HotelDTO findHotelById(String id){
        if(hotelIDMap.containsKey(id)){
            return hotelIDMap.get(id);
        }
        else return null;
    }
}
