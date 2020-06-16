package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.AnonymousUserDAO;
import it.polimi.tiw.bank.utils.ClientHandler;
import it.polimi.tiw.bank.utils.Encryption;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/CheckLogin")
@MultipartConfig
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
        
        String username, password, usernameToHash, passwordToHash;
        
        try { // Get username and password from the request
            usernameToHash = StringEscapeUtils.escapeJava(req.getParameter("username"));
            passwordToHash = StringEscapeUtils.escapeJava(req.getParameter("password"));
        } catch (NullPointerException e) {
            e.printStackTrace(); // TODO: remove after test

            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Username and password cannot be empty");
            return;
        }

        if (usernameToHash==null || usernameToHash.isEmpty()) {
            // Reply with username error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Username can't be empty");
            return;
        }

        if (passwordToHash==null || passwordToHash.isEmpty()) {
            // Reply with password error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Password can't be empty");
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
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, try again later");
            return;
        }

        if (user==null) { // No user with this username
            // Reply with username error message
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Wrong username");
            return;
        }
        
        if (user.getPassword().equals(password)) { // Password correspond
            req.getSession().setAttribute("user", user); // Save user in the session
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(user.getId());
            return;
        }
    
        // Reply with password error message
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().println("Wrong password");

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
