package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.beans.Transfer;
import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.AccountDAO;
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

@WebServlet("/AccountStatus")
public class GoToAccountStatus extends HttpServlet {

    private static final double serialVersionUID = 1L;
    private Connection connection;

    public GoToAccountStatus() {
        super();
    }

    @Override
    public void init() throws ServletException {
        connection = ClientHandler.getConnection(getServletContext());

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

        if (id==null || id.isEmpty()) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Account id can't be empty");
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        try {
            accountId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            e.printStackTrace(); // TODO: remove after test
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Invalid account ID");
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Get user from the session
        User user;
        HttpSession httpSession = req.getSession();
        user = (User) httpSession.getAttribute("user");

        // Get account from DB
        UserDAO userDAO = new UserDAO(connection, user.getId());
        Account account;
        try {
            account = userDAO.findAccountByAccountId(accountId);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Unable to retrieve accounts from DB");
            String path = "/home.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (account==null) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "No account found with this ID for you");
            String path = "/home.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        AccountDAO accountDAO = new AccountDAO(connection, account.getId());
        List<Transfer> outgoingTransfers, incomingTransfers;
        try {
            outgoingTransfers = accountDAO.findOutgoingTransfer();
            incomingTransfers = accountDAO.findIncomingTransfer();
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Unable to retrieve transfers from DB");
            String path = "/home.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        String path = "/WEB-INF/accountStatus.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        ctx.setVariable("account", account);
        ctx.setVariable("outgoingTransfers", outgoingTransfers);
        ctx.setVariable("incomingTransfers", incomingTransfers);
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

    }
}
