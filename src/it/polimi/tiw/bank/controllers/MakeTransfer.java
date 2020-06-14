package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.TransferDAO;
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

@WebServlet("/MakeTransfer")
public class MakeTransfer extends HttpServlet {

    private static final double serialVersionUID = 1L;
    private Connection connection;

    public MakeTransfer() {
        super();
    }

    @Override
    public void init() throws ServletException {

        connection = ClientHandler.getConnection(getServletContext());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get user from the session
        User user = new User();
        HttpSession httpSession = req.getSession();
        user = (User) httpSession.getAttribute("user");

        String originAccountIdString, destinationUserIdString, destinationAccountIdString, causal, amountString;
        int originAccountId, destinationUserId, destinationAccountId;
        double amount;

        // Get transfer's information from the request
        try {
            originAccountIdString = req.getParameter("origin");
            destinationUserIdString = req.getParameter("user");
            destinationAccountIdString = req.getParameter("account");
            causal = req.getParameter("causal");
            amountString = req.getParameter("amount");
        } catch (NullPointerException e) {
            e.printStackTrace(); // TODO: remove after test

            // Redirect to accountStatus.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Parameters can't be empty");
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Check origin account id and parseInt
        if (originAccountIdString!=null && !originAccountIdString.isEmpty()) {
            try {
                originAccountId = Integer.parseInt(originAccountIdString);
            } catch (NumberFormatException e) {
                // Redirect to transferError.html with error message
                ServletContext servletContext = getServletContext();
                final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
                ctx.setVariable("originParseError", "Origin Account ID must be an integer");
                String path = "/WEB-INF/transferError.html";
                templateEngine.process(path, ctx, resp.getWriter());
                return;
            }
        } else {
            // Redirect to transferError.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("originEmptyError", "Origin Account ID can't be empty");
            String path = "/WEB-INF/transferError.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        Account account;
        UserDAO userDAO = new UserDAO(connection, user.getId());
        try {
            account = userDAO.findAccountByAccountId(originAccountId);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test

            // Redirect to transferError.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("originRetrieveError", "Unable to retrieve origin Account");
            String path = "/WEB-INF/transferError.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // check user id and parseInt
        if (destinationUserIdString!=null && !destinationUserIdString.isEmpty()) {
            try {
                destinationUserId = Integer.parseInt(destinationUserIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // TODO: remove after test

                // Redirect to accountStatus.html with error message
                ServletContext servletContext = getServletContext();
                final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
                ctx.setVariable("account", account);
                ctx.setVariable("originAccount", originAccountId);
                ctx.setVariable("userIdErrorMessage", "Destination User ID must be an integer");
                ctx.setVariable("destinationAccount", destinationAccountIdString);
                ctx.setVariable("causal", causal);
                ctx.setVariable("amount", amountString);
                String path = "/accountStatus.html";
                templateEngine.process(path, ctx, resp.getWriter());
                return;
            }
        } else {
            // Redirect to accountStatus.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("account", account);
            ctx.setVariable("originAccount", originAccountId);
            ctx.setVariable("userIdErrorMessage", "Destination User ID can't be empty");
            ctx.setVariable("destinationAccount", destinationAccountIdString);
            ctx.setVariable("causal", causal);
            ctx.setVariable("amount", amountString);
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Check account id and parseInt
        if (destinationAccountIdString!=null && !destinationAccountIdString.isEmpty()) {
            try {
                destinationAccountId = Integer.parseInt(destinationAccountIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // TODO: remove after test

                // Redirect to accountStatus.html with error message
                ServletContext servletContext = getServletContext();
                final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
                ctx.setVariable("account", account);
                ctx.setVariable("originAccount", originAccountId);
                ctx.setVariable("userId", destinationUserId);
                ctx.setVariable("destinationAccountErrorMessage", "Destination Account ID must be an integer");
                ctx.setVariable("causal", causal);
                ctx.setVariable("amount", amountString);
                String path = "/accountStatus.html";
                templateEngine.process(path, ctx, resp.getWriter());
                return;
            }
        } else {
            // Redirect to accountStatus.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("account", account);
            ctx.setVariable("originAccount", originAccountId);
            ctx.setVariable("userId", destinationUserId);
            ctx.setVariable("destinationAccountErrorMessage", "Destination Account ID can't be empty");
            ctx.setVariable("causal", causal);
            ctx.setVariable("amount", amountString);
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Check causal
        if (causal==null || causal.isEmpty()) {
            // Redirect to accountStatus.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("account", account);
            ctx.setVariable("originAccount", originAccountId);
            ctx.setVariable("userId", destinationUserId);
            ctx.setVariable("destinationAccount", destinationAccountId);
            ctx.setVariable("causalErrorMessage", "Causal can't be empty");
            ctx.setVariable("amount", amountString);
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (causal.length()>1024) {
            // Redirect to accountStatus.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("account", account);
            ctx.setVariable("originAccount", originAccountId);
            ctx.setVariable("userId", destinationUserId);
            ctx.setVariable("destinationAccount", destinationAccountId);
            ctx.setVariable("causalErrorMessage", "Causal can't doubleer than 1024 characters");
            ctx.setVariable("amount", amountString);
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Check amount and parseDouble
        if (amountString!=null && !amountString.isEmpty()) {
            try {
                amount = Double.parseDouble(amountString);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // TODO: remove after test

                // Redirect to accountStatus.html with error message
                ServletContext servletContext = getServletContext();
                final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
                ctx.setVariable("account", account);
                ctx.setVariable("originAccount", originAccountId);
                ctx.setVariable("userId", destinationUserId);
                ctx.setVariable("destinationAccount", destinationAccountId);
                ctx.setVariable("causal", causal);
                ctx.setVariable("amountErrorMessage", "Amount must be a double");
                String path = "/WEB-INF/accountStatus.html";
                templateEngine.process(path, ctx, resp.getWriter());
                return;
            }
        } else {
            // Redirect to accountStatus.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("account", account);
            ctx.setVariable("originAccount", originAccountId);
            ctx.setVariable("userId", destinationUserId);
            ctx.setVariable("destinationAccount", destinationAccountId);
            ctx.setVariable("causal", causal);
            ctx.setVariable("amountErrorMessage", "Amount can't be empty");
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (amount<=0) {
            // Redirect to accountStatus.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("account", account);
            ctx.setVariable("originAccount", originAccountId);
            ctx.setVariable("userId", destinationUserId);
            ctx.setVariable("destinationAccount", destinationAccountId);
            ctx.setVariable("causal", causal);
            ctx.setVariable("amountErrorMessage", "Amount must be greater than 0");
            String path = "/accountStatus.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Check that destination account's owner is destination user
        UserDAO destinationUserDAO = new UserDAO(connection, destinationUserId);
        Account destinationAccount;
        try {
            destinationAccount = destinationUserDAO.findAccountByAccountId(destinationAccountId);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test

            // Redirect to transferError.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("accountRetrieveError", "Unable to retrieve account from DB");
            String path = "/WEB-INF/transferError.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (destinationAccount==null) {
            // Redirect to transferError.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("destinationOwnershipError", "Destination user isn't destination account owner");
            String path = "/WEB-INF/transferError.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Check that session user is origin account's owner
        UserDAO originUserDAO = new UserDAO(connection, user.getId());
        Account originAccount;
        try {
            originAccount = originUserDAO.findAccountByAccountId(originAccountId);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test

            // Redirect to transferError.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("accountRetrieveError", "Unable to retrieve account from DB");
            String path = "/WEB-INF/transferError.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (originAccount==null) {
            // Redirect to transferError.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("originOwnershipError", "You are not origin account owner");
            String path = "/WEB-INF/transferError.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Check that origin account has enough funds
        if (originAccount.getBalance() < amount) {
            // Redirect to transferError.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("fundsError", "Origin account hasn't enough founds");
            String path = "/WEB-INF/transferError.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Create transaction and update accounts
        TransferDAO transferDAO = new TransferDAO(connection);
        try {
            transferDAO.createTransfer(amount, originAccount.getId(), destinationAccount.getId(), causal, originAccount.getBalance(), destinationAccount.getBalance());
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test

            // Redirect to transferError.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("transferError", "Unable to make the transfer");
            String path = "/WEB-INF/transferError.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Get the updated accounts
        try {
            originAccount = originUserDAO.findAccountByAccountId(originAccount.getId());
            destinationAccount = destinationUserDAO.findAccountByAccountId(destinationAccount.getId());
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test

            // Redirect to transferSuccessful.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("accountError", "Unable to retrieve accounts");
            String path = "/WEB-INF/transferSuccessful.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        if (originAccount==null || destinationAccount==null) {
            // Redirect to transferSuccessful.html with error message
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("accountError", "Unable to retrieve accounts");
            String path = "/WEB-INF/transferSuccessful.html";
            templateEngine.process(path, ctx, resp.getWriter());
            return;
        }

        // Redirect to transferSuccessful.html with error message
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        ctx.setVariable("originAccount", originAccount);
        ctx.setVariable("destinationAccount", destinationAccount);
        String path = "/WEB-INF/transferSuccessful.html";
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
