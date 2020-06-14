package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.AnonymousUserDAO;
import it.polimi.tiw.bank.utils.ClientHandler;
import it.polimi.tiw.bank.utils.Email;
import it.polimi.tiw.bank.utils.Encryption;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/CreateUser")
@MultipartConfig
public class CreateUser extends HttpServlet {

    private static final double serialVersionUID = 1L;
    private Connection connection;

    public CreateUser() {
        super();
    }

    @Override
    public void init() throws ServletException {

        connection = ClientHandler.getConnection(getServletContext());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String firstName, lastName, usernameToHash, username, email, passwordToHash, password, secondPassword;
        
        try { // Get sign up parameters from the request
            firstName = StringEscapeUtils.escapeJava(req.getParameter("firstname"));
            lastName = StringEscapeUtils.escapeJava(req.getParameter("lastname"));
            usernameToHash = StringEscapeUtils.escapeJava(req.getParameter("username"));
            email = StringEscapeUtils.escapeJava(req.getParameter("email"));
            passwordToHash = StringEscapeUtils.escapeJava(req.getParameter("password"));
            secondPassword = StringEscapeUtils.escapeJava(req.getParameter("password-2"));
        } catch (NullPointerException e) {
            e.printStackTrace();// TODO: remove after test
    
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("All fields must be filled");
            return;
        }

        // Set error indicators
        if ( firstName==null || firstName.isEmpty() ) {
            // Reply with fnErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("First name can't be empty");
            return;
        }

        if ( lastName==null || lastName.isEmpty() ) {
            // Reply with lnErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Last name can't be empty");
            return;
        }

        if ( usernameToHash==null || usernameToHash.isEmpty() ) {
            // Reply with unErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Username can't be empty");
            return;
        }

        if ( email==null || email.isEmpty() ) {
            // Reply with emErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Email can't be empty");
            return;
        } else if (!Email.isValid(email)) {
            // Reply with emRegexError
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Email not valid");
            return;
        }

        if ( passwordToHash==null || passwordToHash.isEmpty() ) {
            // Reply with pwErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Password can't be empty");
            return;
        }

        if ( secondPassword==null || secondPassword.isEmpty() ) {
            // Reply with spwErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Second password can't be empty");
            return;
        }
        

        if (!passwordToHash.equals(secondPassword)) {
            // Reply with spwErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Passwords must be equals");
            return;
        }

        username = Encryption.hashString(usernameToHash);

        User user;
        AnonymousUserDAO anonymousUserDAO = new AnonymousUserDAO(connection);

        // Check username
        try {
            user = anonymousUserDAO.findUserByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, try again later");
            return;
        }

        if (user!=null) {
            // Reply with unErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Username already in use, choose another one");
            return;
        }

        // Check email
        try {
            user = anonymousUserDAO.findUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, try again later");
            return;
        }

        if (user!=null) {
            // Reply with emErrorMessage
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Email already associated to another account, try another email");
            return;
        }

        password = Encryption.hashString(passwordToHash);

        try {
            anonymousUserDAO.createUser(firstName, lastName, username, email, password);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, try again later");
            return;
        }

        String path;
        try {
            user = anonymousUserDAO.findUserByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Your profile was created successfully, but an internal server error occurred. Try to login");
            return;
        }

        if (user!=null) {
            req.getSession().setAttribute("user", user); // Save user in the session
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(user.getId());
            return;
        }
    
        // Reply with internal error message
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().println("Your profile was created successfully, but an internal server error occurred. Try to login");

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
