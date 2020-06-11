package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.utils.ClientHandler;
import it.polimi.tiw.bank.utils.MultiPathMessageResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPath;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/AccountStatus")
public class GoToAccountStatus extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public GoToAccountStatus() {
        super();
    }

    @Override
    public void init() throws ServletException {
        connection = ClientHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver( servletContext );
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        this.templateEngine.setMessageResolver(new MultiPathMessageResolver(servletContext, "i18n"));
        templateResolver.setSuffix(".html");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String id;
        int accountId;

        // Get account ID from the request
        try {
            id = req.getParameter("id");
        } catch (NullPointerException e) {
            e.printStackTrace(); // TODO: remove after test
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Missing account ID parameter");
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (id==null || id.equals("")) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Account id can't be empty");
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    public void destroy() {

        try {
            ClientHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
        }

    }
}