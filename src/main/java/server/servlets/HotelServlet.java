package server.servlets;

import hotelapp.controller.HotelCollection;
import jakarta.servlet.http.HttpServlet;

public class HotelServlet extends HttpServlet {
    private HotelCollection hotelCollection;

    private HotelServlet() {
    }

    public HotelServlet(HotelCollection hotelCollection) {
        this.hotelCollection = hotelCollection;
    }
}
