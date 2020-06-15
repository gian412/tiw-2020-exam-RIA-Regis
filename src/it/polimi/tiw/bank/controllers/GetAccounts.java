package it.polimi.tiw.bank.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.bank.beans.Account;
import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.UserDAO;
import it.polimi.tiw.bank.utils.ClientHandler;
import org.apache.commons.lang.StringEscapeUtils;

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

@WebServlet("/GetAccounts")
public class GetAccounts extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public GetAccounts() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		connection = ClientHandler.getConnection(getServletContext());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// Get the user from the request
		HttpSession httpSession = req.getSession();
		User user = (User) httpSession.getAttribute("user");
		
		List<Account> accounts;
		UserDAO userDAO = new UserDAO(connection, user.getId());
		
		try {
			accounts = userDAO.findAccountsByUserId();
		} catch (SQLException e) {
			e.printStackTrace(); // TODO: remove after test
			
			// Redirect with error
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().println("Unable to retrieve accounts' information");
			return;
		}
		
		// Cast to JSON
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(accounts);
		
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
