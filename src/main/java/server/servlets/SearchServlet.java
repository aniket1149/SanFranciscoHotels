package server.servlets;

import hotelapp.controller.HotelCollection;
import hotelapp.models.HotelDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import server.utils.VelocityTemplateEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/** For searching the hotels details */
public class SearchServlet extends HttpServlet {
    private VelocityTemplateEngine templateEngine = new VelocityTemplateEngine();
    private HotelCollection hotelCollection;

    private SearchServlet() {
    }

    public SearchServlet(HotelCollection hotelCollection) {
        this.hotelCollection = hotelCollection;
    }
    /**
     * GET checks if session is not null else redirects back to login/user
     * @param request should contain query and searchType = id or name
     * */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/user/login");
            return;
        }

        String query = request.getParameter("query");
        String searchType = request.getParameter("searchType");

        Map<String, Object> model = new HashMap<>();
        model.put("param", request.getParameterMap());

        List<HotelDTO> results = new ArrayList<>();

        List<HotelDTO> hotels = hotelCollection.getAllHotels();
        results.addAll(hotels);

        if (query != null && !query.isEmpty() && searchType != null){
            if (searchType.equals("id")) {
                HotelDTO hotel = hotelCollection.findHotelById(query);
                results.clear();
                if (hotel != null) {
                    results.add(hotel);
                }

            }else if (searchType.equals("name")) {
                hotels = hotelCollection.findHotelByName(query);
                results.clear();
                if(null != hotels && !hotels.isEmpty())results.addAll(hotels);
            }
            model.put("results", results);
        }
        model.put("results", results);
        templateEngine.render("templates/search.vm", model, request, response);
    }
}
