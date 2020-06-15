package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.UserDAO;
import it.polimi.tiw.bank.utils.ClientHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/Home")
public class GoToHome extends HttpServlet {

    /*private static final double serialVersionUID = 1L;
    private Connection connection;

    public GoToHome() {
        super();
    }

    @Override
    public void init() throws ServletException {
        connection = ClientHandler.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get user from the session
        User user = new User();
        HttpSession httpSession = req.getSession();
        user = (User) httpSession.getAttribute("user");

        // Get user's account from DB
        UserDAO userDAO = new UserDAO(connection, user.getId());
        List<Account> accounts;
        try {
            accounts = userDAO.findAccountsByUserId();
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Unable to retrieve accounts from DB");
            String path = "/home.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        String path = "/WEB-INF/home.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        ctx.setVariable("userId", user.getId());
        ctx.setVariable("accounts", accounts);
        templateEngine.process(path, ctx, resp.getWriter());

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
    }*/
}
