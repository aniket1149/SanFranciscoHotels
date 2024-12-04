package server.servlets;

import hotelapp.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import server.TravelServer;
import server.repositories.UserRepository;
import server.repositories.UserRepositoryImpl;
import server.utils.PasswordUtil;
import server.utils.VelocityTemplateEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User servlet handles the login, register endpoints for the server.
 * **/

public class UserServlet extends HttpServlet {
    private VelocityTemplateEngine templateEngine = new VelocityTemplateEngine();
    private UserRepository userRepository = new UserRepositoryImpl();
    private static final Logger logger = LogManager.getLogger(UserServlet.class);
    /**
     * GET handles the login functionality and directs based on the action like user/login, user/register, user/logout.
     * @param request should contain query and searchType = id or name
     * */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        logger.info(" GET action: " + action);
        if (action == null) action = "/login";
        switch (action) {
            case "/login":
                templateEngine.render("templates/login.vm", new HashMap<>(), request, response);
                break;
            case "/register":
                templateEngine.render("templates/register.vm", new HashMap<>(), request, response);
                break;
            case "/logout":
                HttpSession session = request.getSession();
                if(session != null) {
                    session.invalidate();
                }
                response.sendRedirect("/user/login");
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    /**
     * POST
     * @param request should contain query and searchType = id or name
     * */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/login";
        logger.info(" POST action: " + action);
        switch (action) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request,response);
                break;
            case "/logout":
                HttpSession session = request.getSession();
                if(session != null) {
                    session.invalidate();
                }
                response.sendRedirect("/user/login");
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        logger.info("Login handler hit");
        User user = userRepository.findByUsername(username);
        if(user != null) {
            if (PasswordUtil.verifyPassword(password, user.getHashedPassword(), user.getSalt())) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect("/search/");
                return;
            }
        }
        Map<String, Object> model = new HashMap<>();
        model.put("error", "Invalid Username or Password");
        templateEngine.render("templates/login.vm", model,request, response);
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map<String, Object> model = new HashMap<>();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            model.put("error", "Username and password cannot be empty.");
            templateEngine.render("templates/register.vm", model,request, response);
            return;
        }
        if(userRepository.findByUsername(username) != null) {
            model.put("error", "Username is already in use.");
            templateEngine.render("templates/register.vm", model,request, response);
            return;
        }
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(password, salt);
        User user = new User(username, hashedPassword, salt);
        if(Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", password)){
        if(userRepository.save(user)) {
            response.sendRedirect("/user/login");
        }else{
            model.put("error", "Something went wrong, try again");
            templateEngine.render("templates/register.vm", model, request, response);
        }
        }else{
            model.put("error", "Password should be atleast 8 char long. Contain 1 uppercase , 1  lowercase letter, 1 digit and 1 Special Character.");
            templateEngine.render("templates/register.vm", model, request, response);
        }
    }


}
