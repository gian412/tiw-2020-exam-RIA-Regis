package it.polimi.tiw.bank.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.UserDAO;
import it.polimi.tiw.bank.utils.ClientHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/AccountDetails")
public class GetAccountDetails extends HttpServlet {
	
	private static final double serialVersionUID = 1L;
	private Connection connection;
	
	public GetAccountDetails() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		connection = ClientHandler.getConnection(getServletContext());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// Get user from the session
		HttpSession httpSession = req.getSession();
		User user = (User) httpSession.getAttribute("user");
		
		String accountIdString;
		int accountId;
		
		// Get account ID from the request
		try {
			accountIdString = req.getParameter("accountId");
		} catch (NullPointerException e) {
			e.printStackTrace(); // TODO: remove after test
			
			// Reply with error message
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println("Account ID can't be empty");
			return;
		}
		
		// Check received input
		if (accountIdString==null || accountIdString.isEmpty()) {
			// Reply with error message
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println("Account ID can't be empty");
			return;
		}
		
		// Parse received input
		try {
			accountId = Integer.parseInt(accountIdString);
		} catch (NumberFormatException e) {
			e.printStackTrace(); // TODO: remove after test
			
			// Reply with error message
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println("Account ID must be an integer");
			return;
		}
		
		// Get account from the DB
		Account account;
		UserDAO userDAO = new UserDAO(connection, user.getId());
		
		try {
			account = userDAO.findAccountByAccountId(accountId);
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
		
		// Cast to JSON
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(account);
		
		// Send response
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);
		
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
