package server;

import hotelapp.HotelReviewService;
import hotelapp.controller.HotelCollection;
import hotelapp.controller.ThreadSafeInvertedIndex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.repositories.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class TravelServerDriver {
    public static final int PORT = 8080;
    private static final Logger logger = LogManager.getLogger(TravelServerDriver.class);


    public static void main(String[] args)  {
        // FILL IN CODE, and add more classes as needed
        HotelReviewService hotelReviewService = new HotelReviewService();
        hotelReviewService.loadData(args);
        HotelCollection hotelCollection = hotelReviewService.getHotelCollection();
        ThreadSafeInvertedIndex reviewCollection = hotelReviewService.getReviewCollection();
        TravelServer server = new TravelServer();
        List<Object> dataObjects = new ArrayList<>();
        dataObjects.add(hotelCollection);
        if (reviewCollection != null) {
            dataObjects.add(reviewCollection);
        }
        server.addServlets(dataObjects);

        try {
            server.start();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
