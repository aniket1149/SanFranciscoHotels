package server;

import hotelapp.HotelReviewService;
import hotelapp.controller.HotelCollection;
import hotelapp.controller.ThreadSafeInvertedIndex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.repositories.*;

import java.util.ArrayList;
import java.util.List;

public class TravelServerDriver {
    private static final Logger logger = LogManager.getLogger(TravelServerDriver.class);
    private static final HotelRepository hotelRepository = new HotelRepositoryImpl();
    private static final ReviewRepository reviewRepository = new ReviewRepositoryImpl();
    private static final UserRepository userRepository = new UserRepositoryImpl();
    private static final HistoryRepository historyRepository = new HistoryRepositoryImpl();

    public static void main(String[] args)  {
        // FILL IN CODE, and add more classes as needed


        HotelReviewService hotelReviewService = new HotelReviewService(hotelRepository, reviewRepository, userRepository);
        hotelReviewService.loadData(args);
        HotelCollection hotelCollection = hotelReviewService.getHotelCollection();
        ThreadSafeInvertedIndex reviewCollection = hotelReviewService.getReviewCollection();
        TravelServer server = new TravelServer(hotelRepository, reviewRepository, userRepository, historyRepository);
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
