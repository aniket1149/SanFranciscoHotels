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
    private TreeMap<String, String> nameIDMap;

    public HotelCollection(Set<HotelDTO> hotelSet) {
        this.hotelIDMap = new TreeMap<>((a,b)->a.compareTo(b));
        this.hotelSet = new TreeSet<HotelDTO>(hotelSet);
        this.nameIDMap = new TreeMap<>();
        initializeMap(hotelSet);
    }
    private void initializeMap(Set<HotelDTO> hotels){
        for(HotelDTO hotel : hotels){
            hotelIDMap.put(hotel.getId(), hotel);
            nameIDMap.put(hotel.getName().toLowerCase(), hotel.getId());
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

    public List<HotelDTO> findHotelByName(String name){
        List<HotelDTO> res = new ArrayList<>();
        for(Map.Entry<String,String> entry : nameIDMap.entrySet()){
            if(entry.getKey().contains(name.toLowerCase())){
                res.add(hotelIDMap.get(entry.getValue()));
            }
        }
        return res;
    }
    public List<HotelDTO> getAllHotels(){
        List<HotelDTO> res = new ArrayList<>(hotelIDMap.values());
        return res;
    }
}
