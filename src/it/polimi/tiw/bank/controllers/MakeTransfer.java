package it.polimi.tiw.bank.controllers;

import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.TransferDAO;
import it.polimi.tiw.bank.dao.UserDAO;
import it.polimi.tiw.bank.utils.ClientHandler;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/MakeTransfer")
@MultipartConfig
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
        User user;
        HttpSession httpSession = req.getSession();
        user = (User) httpSession.getAttribute("user");

        String originAccountIdString, destinationUserIdString, destinationAccountIdString, causal, amountString;
        int originAccountId, destinationUserId, destinationAccountId;
        double amount;

        // Get transfer's information from the request
        try {
            originAccountIdString = StringEscapeUtils.escapeJava(req.getParameter("origin"));
            destinationUserIdString = StringEscapeUtils.escapeJava(req.getParameter("user"));
            destinationAccountIdString = StringEscapeUtils.escapeJava(req.getParameter("account"));
            causal = StringEscapeUtils.escapeJava(req.getParameter("causal"));
            amountString = StringEscapeUtils.escapeJava(req.getParameter("amount"));
        } catch (NullPointerException e) {
            e.printStackTrace(); // TODO: remove after test

           // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Username and password cannot be empty");
            return;
        }

        // Check origin account id and parseInt
        if (originAccountIdString == null || originAccountIdString.isEmpty()) {
            // Reply with Origin Account ID error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Origin Account ID can't be empty");
            return;
        }
        
        try {
            originAccountId = Integer.parseInt(originAccountIdString);
        } catch (NumberFormatException e) {
            
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Origin Account ID must be an integer");
            return;
        }
    
        Account account;
        UserDAO userDAO = new UserDAO(connection, user.getId());
        try {
            account = userDAO.findAccountByAccountId(originAccountId);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, try again later");
            return;
        }
        
        if (account==null) {
            // Reply with username error message
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("You haven't an account with this ID");
            return;
        }

        // check user id and parseInt
        if (destinationUserIdString == null || destinationUserIdString.isEmpty()) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Destination User ID can't be empty");
            return;
        }
        
        try {
            destinationUserId = Integer.parseInt(destinationUserIdString);
        } catch (NumberFormatException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Destination User ID must be an integer");
            return;
        }
        
        // Check account id and parseInt
        if (destinationAccountIdString == null || destinationAccountIdString.isEmpty()) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Destination Account ID can't be empty");
            return;
        }
        
        try {
            destinationAccountId = Integer.parseInt(destinationAccountIdString);
        } catch (NumberFormatException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Destination Account ID must be an integer");
            return;
        }
    
        // Check causal
        if (causal==null || causal.isEmpty()) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Causal can't be empty");
            return;
        }

        if (causal.length()>1024) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Causal can't greater than 1024 characters");
            return;
        }

        // Check amount and parseDouble
        if (amountString == null || amountString.isEmpty()) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Amount can't be empty");
            return;
        }
        
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Amount must be a double");
            return;
        }
    
        if (amount<=0) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Amount must be greater than 0");
            return;
        }

        // Check that destination account's owner is destination user
        UserDAO destinationUserDAO = new UserDAO(connection, destinationUserId);
        Account destinationAccount;
        try {
            destinationAccount = destinationUserDAO.findAccountByAccountId(destinationAccountId);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, try again later");
            return;
        }

        if (destinationAccount==null) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Destination user isn't destination account owner");
            return;
        }

        // Check that session user is origin account's owner
        UserDAO originUserDAO = new UserDAO(connection, user.getId());
        Account originAccount;
        try {
            originAccount = originUserDAO.findAccountByAccountId(originAccountId);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, try again later");
            return;
        }

        if (originAccount==null) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("You are not origin account owner");
            return;
        }

        // Check that origin account has enough funds
        if (originAccount.getBalance() < amount) {
            // Reply with error message
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Origin account hasn't enough founds");
            return;
        }

        // Create transaction and update accounts
        TransferDAO transferDAO = new TransferDAO(connection);
        try {
            transferDAO.createTransfer(amount, originAccount.getId(), destinationAccount.getId(), causal, originAccount.getBalance(), destinationAccount.getBalance());
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: remove after test
    
            // Reply with internal error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, try again later");
            return;
        }
    
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println("OK");

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
