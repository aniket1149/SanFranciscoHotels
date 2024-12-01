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
import server.servlets.*;
import server.utils.LoginFilter;

import java.util.List;

public class TravelServer {
    private static final int PORT = 8080;
    private Server jettyServer;  // Jetty server
    private ServletContextHandler handler;
    private static final Logger logger = LogManager.getLogger(TravelServer.class);
    public TravelServer() {
        jettyServer = new Server(PORT);
        handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        jettyServer.setHandler(handler);
    }

    public void addServlets(List<Object> objs){
        handler.setContextPath("/");
        handler.addServlet(UserServlet.class, "/user/*");
        HotelCollection hotelCollection = null;
        ThreadSafeInvertedIndex reviewCollection=null;
        // handler.addFilter(LoginFilter.class, "/*", null);
        for(Object obj : objs){
            if(obj instanceof HotelCollection){
                hotelCollection = (HotelCollection) obj;
                handler.addServlet(new ServletHolder(new SearchServlet((HotelCollection) obj)), "/search/*");
            }else if(obj instanceof ThreadSafeInvertedIndex){
                reviewCollection = (ThreadSafeInvertedIndex) obj;
                ReviewServlet reviewServlet = new ReviewServlet(reviewCollection);
                ServletHolder reviewServletHolder = new ServletHolder(reviewServlet);
                handler.addServlet(reviewServletHolder, "/review/*");
            }else{
                logger.error("Error intializing the servlets");
                return;
            }

        }
        if(hotelCollection != null && reviewCollection != null){
            handler.addServlet(new ServletHolder(new HotelServlet(hotelCollection, reviewCollection)), "/hotel/*");
        }
    }

    public void start() throws Exception {
        // FILL IN CODE: run the jetty server (call start and join)
        jettyServer.start();
        jettyServer.join();
    }

}
