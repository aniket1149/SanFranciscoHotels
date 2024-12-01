package hotelapp;

import hotelapp.controller.HotelCollection;
import hotelapp.controller.ThreadSafeInvertedIndex;
import hotelapp.fileprocessor.LoadObjectFromFile;
import hotelapp.util.ArgParser;

import java.nio.file.Path;

/** The driver class for project 3.
 * The main function should be able to take the following command line arguments
 * -hotels hotelFile -reviews reviewsDirectory -threads numThreads -output filepath
 * (only -hotels followed by the hotel file is required):
 * and read general information about the hotels from the hotelFile (a JSON file),
 * as well as concurrently read hotel reviews from the json files in reviewsDirectory.
 * The data should be loaded into data structures that allow efficient search.
 * If the -output flag is provided, the results should be output into the given file.
 * See pdf description of the project for the requirements.
 * You are expected to add other classes and methods from project 1 to this project,
 * and take instructor's / TA's feedback from a code review of project 1 into account.
 */
public class HotelReviewService {
    private LoadObjectFromFile hotelLoader ;
    private LoadObjectFromFile reviewLoader ;
    private HotelCollection hotelCollection;
    private ThreadSafeInvertedIndex reviewCollection;

    // FILL IN CODE: add instance data as needed

    /**
     * Parse given arguments that contain paths to the hotel file and the reviews folder,
     * and load hotel and review data into the corresponding data structures.
     * Do not store data structures (maps) in this class and do not do the actual parsing in this class,
     * think of a better design that includes multiple classes / packages, so that this class can
     * delegate work to other classes.
     * @param args  Arguments can be given in the following format where -reviews, -threads, -output are optional:
     *  -hotels pathToHotelFile -reviews pathToReviewsFolder -threads n -output outputFilePath
     *   or in a different order.
     */
    public void loadData(String[] args) {
        // FILL IN CODE:
        // load info into thread safe data structures
        // use a single thread to load hotels
        // use multithreading to load reviews (using ExecutorService, Phaser etc).
        ArgParser argParser = new ArgParser();
        if(!argParser.isEverythingValid(args)){
            throw new IllegalArgumentException("Invalid Flags passed");
        }
        String threads = argParser.getPath("-threads");
        String hotelPath = argParser.getPath("-hotels");
        String reviewPath = argParser.getPath("-reviews");
        int numthreads = (threads == null) ? 1 : Integer.parseInt(threads);
        hotelLoader = new LoadObjectFromFile(1);
        hotelLoader.validPathLoadObject(Path.of(hotelPath),"hotel");
        hotelCollection = hotelLoader.getHotelCollection();
        if(reviewPath != null){
            reviewLoader = new LoadObjectFromFile(numthreads);
            reviewLoader.validPathLoadObject(Path.of(reviewPath),"review");
            reviewCollection = reviewLoader.getReviewsAndInvertedIndex();
        }
    }
    public HotelCollection getHotelCollection() {
        return hotelCollection;
    }

    public ThreadSafeInvertedIndex getReviewCollection() {
        return reviewCollection;
    }

    /**
     * Process a given query and return the result as a string
     * @param query in one of the following formats:
     *              findHotel hotelId
     *              findReviews hotelId
     *              findWord word
     * @return String, the result of the query
     */


}
