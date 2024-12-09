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
            String bootstrap = """
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
                    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js" integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js" integrity="sha384-0pUGZvbkm6XF6gxjEnlmuGrJXVbNuzT9qBBavbLwCsOGabYfZo0T0to5eqruptLy" crossorigin="anonymous"></script>
                    """;
            context.put("bootstrap", bootstrap);
            PrintWriter writer = response.getWriter();
            velocityEngine.getTemplate(templatePath).merge(context, writer);
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
