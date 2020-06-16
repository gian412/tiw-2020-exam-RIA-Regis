package it.polimi.tiw.bank.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.bank.beans.Address;
import it.polimi.tiw.bank.beans.User;
import it.polimi.tiw.bank.dao.AddressDAO;
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
import java.util.List;

@WebServlet("/GetAddresses")
public class GetAddresses extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public GetAddresses() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		connection = ClientHandler.getConnection(getServletContext());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// Get user form the session
		HttpSession httpSession = req.getSession();
		User user = (User) httpSession.getAttribute("user");
		
		List<Address> addresses;
		AddressDAO addressDAO = new AddressDAO(connection, user.getId());
		
		try {
			addresses = addressDAO.findAddressesByUserId();
		} catch (SQLException e) {
			e.printStackTrace(); // TODO: remove after test
			
			// Redirect with error
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().println("Unable to retrieve addresses");
			return;
		}
		
		// Cast to JSON
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(addresses);
		
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
