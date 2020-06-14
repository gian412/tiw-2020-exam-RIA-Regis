package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.AnonymousUserDAO;
import it.polimi.tiw.bank.utils.ClientHandler;
import it.polimi.tiw.bank.utils.Encryption;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {

    private static final double serialVersionUID = 1L;
    private Connection connection;

    public CheckLogin() {
        super();
    }

    @Override
    public void init() throws ServletException {

        connection = ClientHandler.getConnection(getServletContext());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username, password, usernameToHash = null, passwordToHash = null;

        try { // Get username and password from the request
            usernameToHash = req.getParameter("username");
            passwordToHash = req.getParameter("password");
        } catch (NullPointerException e) {
            e.printStackTrace(); // TODO: remove after test

            // Redirect to login.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Username and password cannot be empty");
            String path = "/login.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (usernameToHash==null || usernameToHash.equals("")) {
            // Redirect to login.html with username error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("usernameErrorMessage", "Username can't be empty");
            String path = "/login.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (passwordToHash==null || passwordToHash.equals("")) {
            // Redirect to login.html with password error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("passwordErrorMessage", "Password can't be empty");
            String path = "/login.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Encrypt username and password
        username = Encryption.hashString(usernameToHash);
        password = Encryption.hashString(passwordToHash);

        User user = null;
        AnonymousUserDAO anonymousUserDAO = new AnonymousUserDAO(connection);
        try {
            user = anonymousUserDAO.findUserByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test

            // Redirect to login.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("errorMessage", "Unable to access DB, try again later");
            String path = "/login.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (user==null) { // No user with this username
            // Redirect to login.html with username error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("usernameErrorMessage", "Wrong username");
            String path = "/login.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        String path;
        if (user.getPassword().equals(password)) { // Password correspond
            req.getSession().setAttribute("user", user); // Save user in the session
            path = getServletContext().getContextPath() + "/Home";
            resp.sendRedirect(path);
        }

        // Redirect to login.html with password error message
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        ctx.setVariable("username", usernameToHash);
        ctx.setVariable("passwordErrorMessage", "Wrong password");
        path = "/login.html";
        templateEngine.process(path, ctx, resp.getWriter());

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
