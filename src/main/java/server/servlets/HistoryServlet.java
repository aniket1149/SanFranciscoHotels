package server.servlets;

import hotelapp.models.History;
import hotelapp.models.HotelDTO;
import hotelapp.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.repositories.HistoryRepository;
import server.repositories.HistoryRepositoryImpl;
import server.repositories.HotelRepository;
import server.repositories.HotelRepositoryImpl;
import server.utils.HelperUtil;
import server.utils.VelocityTemplateEngine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HistoryServlet extends HttpServlet {
    private VelocityTemplateEngine templateEngine = new VelocityTemplateEngine();
    private HistoryRepository historyRepository;
    private HotelRepository hotelRepository;
    private static final Logger logger = LogManager.getLogger(HistoryServlet.class);
    private HistoryServlet(){}
    public HistoryServlet(HistoryRepository historyRepository, HotelRepository hotelRepository) {
        this.historyRepository = historyRepository;
        this.hotelRepository = hotelRepository;
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = null;
        if((user = (User) session.getAttribute("user")) == null) {
            response.sendRedirect("/user/login");
            return;
        }
        String action = request.getPathInfo();
        if (action == null) action = "/view";
        switch (action) {
            case "/click":
                handleClick(request, response);
                break;
            case "/view":
                handleViewHistory(request, response);
                break;
            case "/delete":
                handleDeleteLinkHistory(request, response);
            default: response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleDeleteLinkHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = null;
        if((user = (User) session.getAttribute("user")) == null) {
            response.sendRedirect("/user/login");
            return;
        }
        String sender= request.getParameter("username");
        if(sender == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing sender");
            return;
        }

        if(sender.equals(user.getUsername())) {
            if(historyRepository.deleteHistory(sender)) {
                response.sendRedirect("/history/view");
            }
            else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing sender");
                response.sendRedirect("/history/view");
                return;
            }
        }
    }

    private void handleClick(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = request.getParameter("hotelId");
        if (hotelId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing hotelId");
            return;
        }
        HttpSession session = request.getSession();
        User user = null;
        if((user = (User) session.getAttribute("user")) == null) {
            response.sendRedirect("/user/login");
            return;
        }
        HotelDTO hotelDTO = hotelRepository.findHotelById(hotelId);
        if(hotelDTO == null) {
            response.sendRedirect("/search");
            return;
        }
        History history = new History(hotelId, user.getUsername(), HelperUtil.dateFormatter(Optional.empty()));
        if(historyRepository.saveHistory(history)){
            logger.info("History already present, updating last timestamp");
        }else if(historyRepository.updateHistory(history, user.getUsername())){
                logger.info("History updated successfully");
        }else{
            logger.error("History update failed");
        }

        response.sendRedirect(hotelDTO.getLink());
    }

    private void handleViewHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = null;
        if((user = (User) session.getAttribute("user")) == null) {
            response.sendRedirect("/user/login");
            return;
        }
        List<History> histories = historyRepository.findUserClickHistory(user.getUsername());
        Map<String, Object> model = new HashMap<>();
        model.put("historyItems", histories);
        templateEngine.render("/templates/history.vm", model,request, response);
    }


}
