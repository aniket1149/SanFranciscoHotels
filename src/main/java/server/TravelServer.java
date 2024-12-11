package server;

import hotelapp.controller.HotelCollection;
import hotelapp.controller.ThreadSafeInvertedIndex;
import jakarta.servlet.Servlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import server.repositories.*;
import server.servlets.*;

import java.util.List;

/**
 * Travel Server intializes the dependenceis for each servlet.
 * **/
public class TravelServer {
    private static final int PORT = 8080;
    private Server jettyServer;
    private ServletContextHandler handler;
    private static final Logger logger = LogManager.getLogger(TravelServer.class);
    private HotelRepository hotelRepository;
    private ReviewRepository reviewRepository;
    private UserRepository userRepository;
    private HistoryRepository historyRepository;
    public TravelServer(HotelRepository hotelRepository, ReviewRepository reviewRepository, UserRepository userRepository, HistoryRepository historyRepository) {
        jettyServer = new Server(PORT);
        handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        jettyServer.setHandler(handler);
        this.hotelRepository = hotelRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    /**
     * Adds servlets to the travelServer
     * @param objs
     */
    public void addServlets(List<Object> objs){
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new UserServlet(userRepository)), "/user/*");
        handler.addServlet(new ServletHolder(new HistoryServlet(historyRepository, hotelRepository)), "/history/*");
        handler.addServlet(new ServletHolder(new SearchServlet(hotelRepository)), "/search/*");
        handler.addServlet(new ServletHolder(new ReviewServlet(reviewRepository)), "/review/*");
        handler.addServlet(new ServletHolder(new HotelServlet(hotelRepository, reviewRepository)), "/hotel/*");
    }

    public void start() throws Exception {
        jettyServer.start();
        jettyServer.join();
    }

}
