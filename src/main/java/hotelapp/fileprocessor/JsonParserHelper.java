package hotelapp.fileprocessor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import hotelapp.models.HotelDTO;
import hotelapp.models.ReviewDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
/*
Helps in parsing json files for hotel and review class
returns set of hotels and set of reviews.
 */
public class JsonParserHelper {
    private Gson gson;
    private static final Logger logger = LogManager.getLogger(JsonParserHelper.class);

    public JsonParserHelper() {
        gson = new Gson();
    }


    public Set<ReviewDTO> parseReview(List<Path> paths){
        Set<ReviewDTO> reviews = new TreeSet<>();
        for(Path path : paths){
            try(BufferedReader br = new BufferedReader(new FileReader(path.toString()))){
                JsonObject js = JsonParser.parseReader(br).getAsJsonObject();
                JsonObject reviewCollectionObj = js.getAsJsonObject("reviewDetails").getAsJsonObject("reviewCollection");
                JsonArray jsArr = reviewCollectionObj.getAsJsonArray("review");
                Type reviewSetType = new TypeToken<Set<ReviewDTO>>(){}.getType();
                Set<ReviewDTO> reviewSet = gson.fromJson(jsArr, reviewSetType);
                logger.debug("GSON Parsed the following json file and extracted {} reviews", reviewSet.size());
                if(null != reviewSet) reviews.addAll(reviewSet);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
        return reviews;
    }

    public Set<HotelDTO> parseHotel(List<Path> paths){
        Set<HotelDTO> hotels = new HashSet<>();
        for(Path path : paths){
            try(BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
                JsonObject js = JsonParser.parseReader(br).getAsJsonObject();
                JsonArray jsArr = js.getAsJsonArray("sr");
                Type hotelSetType = new TypeToken<Set<HotelDTO>>(){}.getType();
                Set<HotelDTO> hotelSet = gson.fromJson(jsArr, hotelSetType);
                logger.debug("GSON Parsed the following json file and extracted {} hotels", hotelSet.size());
                if(null != hotelSet) hotels.addAll(hotelSet);
            } catch (IOException e ){
                System.out.println(e.getMessage());
                return null;
            }
        }

        return hotels;
    }
}
