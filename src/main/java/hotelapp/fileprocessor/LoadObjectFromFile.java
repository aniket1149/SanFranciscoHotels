package hotelapp.fileprocessor;

import hotelapp.controller.HotelCollection;
import hotelapp.controller.ThreadSafeInvertedIndex;
import hotelapp.models.HotelDTO;
import hotelapp.models.ReviewDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/*
Loads Hotels single thread
Loads reviews multi threads
Validates file paths and creates threads and processes multiple file concurrently.
 */
public class LoadObjectFromFile {
    private JsonParserHelper jsonParserHelper;
   // private Set<ReviewDTO> reviewDTOS;
    private Set<HotelDTO> hotelDTOS;
    private ExecutorService executorService;
    private Phaser phaser;
    private int numThreads;
    private ThreadSafeInvertedIndex invertedIndex;
    private Set<Path> processedPaths = new HashSet<>();
    private Set<Path> pathNotRecognized = new HashSet<>();
    private HotelCollection hotelCollection;
    private static final Logger logger = LogManager.getLogger(LoadObjectFromFile.class);


    public LoadObjectFromFile(int numThreads) {
        jsonParserHelper = new JsonParserHelper();
        // reviewDTOS = new TreeSet<>();
        hotelDTOS = new HashSet<>();
        this.numThreads = numThreads;

    }


    public void validPathLoadObject(Path path, String type){
        if(type.equals("hotel")){
            validatePathsAndProcess(path, type);
            if(!hotelDTOS.isEmpty()){
                hotelCollection = new HotelCollection(hotelDTOS);
                logger.info("Loaded {} hotels to collection", hotelDTOS.size());
            }
        }else {
            invertedIndex = new ThreadSafeInvertedIndex();
            executorService = Executors.newFixedThreadPool(numThreads);
            phaser = new Phaser(1);
            validatePathsAndProcess(path, type);

            phaser.arriveAndAwaitAdvance();
            executorService.shutdown();
            logger.info("Loaded reviews to collection");
        }
    }

    private void validatePathsAndProcess (Path path, String type){
        if(processedPaths.contains(path)){
            return;
        }
        processedPaths.add(path);

        if(!Files.isDirectory(path)){
            Path p = Paths.get(path.toString());
            String fileName = p.getFileName().toString();
            if(fileName.startsWith("review")){
                if(type.equals("review")){
                    executorService.submit(new ReviewProcessorTask(path));
                   // reviewDTOS.addAll(jsonParserHelper.parseReview(List.of(path)));
                }
            }else if (fileName.startsWith("hotel")){
                if(type.equals("hotel")){
                    try{
                    Set<HotelDTO> hotels = jsonParserHelper.parseHotel(List.of(path));
                    hotelDTOS.addAll(hotels);
                    logger.debug("Added {} hotels from {}", hotels.size(), path.getFileName());
                    }
                    catch(Exception e){
                        logger.error("Error parsing hotels from file: {}", path, e);
                    }
                }
            }else {
                pathNotRecognized.add(path);
                logger.warn("Unrecognized file: {}", path);
            }

        }
        if(Files.isDirectory(path)){
            try(DirectoryStream<Path> ds = Files.newDirectoryStream(path)){
                for(Path ref : ds){
                    validatePathsAndProcess(ref, type);
                }
            }catch (IOException e){
                logger.warn("Invalid path entered: {}", path, e);
            }
        }
    }

    private class ReviewProcessorTask implements Runnable{
            private Path path;
            public ReviewProcessorTask(Path path
            ){
                phaser.register();
                this.path = path;
            }

        @Override
        public void run() {
            try{
                logger.debug("Thread {} processing file: {}", Thread.currentThread().getName(), path);
                Set<ReviewDTO> reviewDTOSet =  jsonParserHelper.parseReview(List.of(path));
                logger.debug("Thread {} added {} reviews from {}", Thread.currentThread().getName(), reviewDTOSet.size(), path.getFileName());
                invertedIndex.addReviews(reviewDTOSet);
            }catch(Exception e){
                logger.error("Error processing file {} in thread {}", path, Thread.currentThread().getName(), e);
            }finally {
                phaser.arriveAndDeregister();
                logger.debug("Thread {} finished processing", Thread.currentThread().getName());
            }
        }
    }

    public HotelCollection getHotelCollection() {
        return new HotelCollection(hotelDTOS);
    }
    public ThreadSafeInvertedIndex getReviewsAndInvertedIndex() {
        return invertedIndex;
    }


}
