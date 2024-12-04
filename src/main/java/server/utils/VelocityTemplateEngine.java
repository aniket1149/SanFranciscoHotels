package server.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.TravelServerDriver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

public class VelocityTemplateEngine {
    private VelocityEngine velocityEngine;
    private static final Logger logger = LogManager.getLogger(VelocityTemplateEngine.class);

    /**
     * Intializes the velocity engine and sets the properties.
     * **/
    public VelocityTemplateEngine() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine = new VelocityEngine(props);
        velocityEngine.init();
    }
    /**
     * Pushes our variable mapping to the templates and renders the templates
     * **/
    public void render(String templatePath, Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        try{
            VelocityContext context = new VelocityContext(model);
            response.setContentType("text/html");

            HttpSession session = request.getSession(false);
            if (session != null) {
                context.put("session", session);
            }
            PrintWriter writer = response.getWriter();
            velocityEngine.getTemplate(templatePath).merge(context, writer);
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
