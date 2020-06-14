package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.AnonymousUserDAO;
import it.polimi.tiw.bank.utils.ClientHandler;
import it.polimi.tiw.bank.utils.Email;
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

@WebServlet("/CreateUser")
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
        boolean fn = false, ln = false, un = false, em = false, pw = false, spw = false;
        try {
            firstName = req.getParameter("firstname");
            lastName = req.getParameter("lastname");
            usernameToHash = req.getParameter("username");
            email = req.getParameter("email");
            passwordToHash = req.getParameter("password");
            secondPassword = req.getParameter("password-2");
        } catch (NullPointerException e) {
            e.printStackTrace();// TODO: remove after test
            // Redirect to signUp.html with generic error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "All fields must be filled");
            String path = "/signUp.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Set error indicators
        if ( firstName==null || firstName.equals("") ) {
            fn = true;
        }

        if ( lastName==null || lastName.equals("") ) {
            ln = true;
        }

        if ( usernameToHash==null || usernameToHash.equals("") ) {
            un = true;
        }

        if ( email==null || email.equals("") ) {
            em = true;
        }

        if ( passwordToHash==null || passwordToHash.equals("") ) {
            pw = true;
        }

        if ( secondPassword==null || secondPassword.equals("") ) {
            spw = true;
        }

        if (fn || ln || un || em || pw || spw) {
            // Redirect to signUp.html with relative error messages
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());

            // Set error messages
            if (fn) {
                ctx.setVariable("fnErrorMessage", "First name can't be empty");
            } else {
                ctx.setVariable("firstName", firstName);
            }

            if (ln) {
                ctx.setVariable("lnErrorMessage", "Last name can't be empty");
            } else {
                ctx.setVariable("lastName", lastName);
            }

            if (un) {
                ctx.setVariable("unErrorMessage", "Username can't be empty");
            } else {
                ctx.setVariable("username", usernameToHash);
            }

            if (em) {
                ctx.setVariable("emErrorMessage", "Email can't be empty");
            } else if (Email.isValid(email)) {
                ctx.setVariable("emRegexError", "Email not valid");
            } else {
                ctx.setVariable("email", email);
            }

            if (pw) {
                ctx.setVariable("pwErrorMessage", "Password can't be empty");
            } else {
                ctx.setVariable("password", passwordToHash);
            }

            if (spw) {
                ctx.setVariable("spwErrorMessage", "Password can't be empty");
            } else {
                ctx.setVariable("secondPassword", secondPassword);
            }

            String path = "/signUp.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (!passwordToHash.equals(secondPassword)) {
            // Redirect to signUp.html with second password error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("firstName", firstName);
            ctx.setVariable("lastName", lastName);
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("email", email);
            ctx.setVariable("password", passwordToHash);
            ctx.setVariable("secondPassword", secondPassword);
            ctx.setVariable("spwErrorMessage", "Password must be equals");
            String path = "/signUp.html";
            templateEngine.process(path, ctx, resp.getWriter());
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
            // Redirect to signUp.html with generic error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Unable to access DB, try again later");
            ctx.setVariable("firstName", firstName);
            ctx.setVariable("lastName", lastName);
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("email", email);
            ctx.setVariable("password", passwordToHash);
            ctx.setVariable("secondPassword", secondPassword);
            String path = "/signUp.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (user!=null) {
            // Redirect to signUp.html with username error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("firstName", firstName);
            ctx.setVariable("lastName", lastName);
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("unErrorMessage", "Username already in use, choose another one");
            ctx.setVariable("email", email);
            ctx.setVariable("password", passwordToHash);
            ctx.setVariable("secondPassword", secondPassword);
            String path = "/signUp.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Check email
        try {
            user = anonymousUserDAO.findUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
            // Redirect to signUp.html with generic error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Unable to access DB, try again later");
            ctx.setVariable("firstName", firstName);
            ctx.setVariable("lastName", lastName);
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("email", email);
            ctx.setVariable("password", passwordToHash);
            ctx.setVariable("secondPassword", secondPassword);
            String path = "/signUp.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (user!=null) {
            // Redirect to signUp.html with username error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("firstName", firstName);
            ctx.setVariable("lastName", lastName);
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("email", email);
            ctx.setVariable("emErrorMessage", "Email already associated to another account, try another email");
            ctx.setVariable("password", passwordToHash);
            ctx.setVariable("secondPassword", secondPassword);
            String path = "/signUp.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        password = Encryption.hashString(passwordToHash);

        try {
            anonymousUserDAO.createUser(firstName, lastName, username, email, password);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
            // Redirect to signUp.html with generic error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Unable to create profile, try again later");
            ctx.setVariable("firstName", firstName);
            ctx.setVariable("lastName", lastName);
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("email", email);
            ctx.setVariable("password", passwordToHash);
            ctx.setVariable("secondPassword", secondPassword);
            String path = "/signUp.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        String path;
        try {
            user = anonymousUserDAO.findUserByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
            // Redirect to login.html
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("username", usernameToHash);
            ctx.setVariable("password", passwordToHash);
            path = "/login.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (user!=null) {
            req.getSession().setAttribute("user", user); // Save user in the session
            path = getServletContext().getContextPath() + "/Home";
            resp.sendRedirect(path);
            return;
        }

        // Redirect to signUp.html with generic error message
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        ctx.setVariable("errorMessage", "Something went wrong, try again later");
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("lastName", lastName);
        ctx.setVariable("username", usernameToHash);
        ctx.setVariable("email", email);
        ctx.setVariable("password", passwordToHash);
        ctx.setVariable("secondPassword", secondPassword);
        path = "/signUp.html";
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
