package it.polimi.tiw.bank.dao;

import it.polimi.tiw.bank.beans.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {
	
	private final Connection connection;
	private final int ownerId;
	
	public AddressDAO(Connection connection, int ownerId) {
		this.connection = connection;
		this.ownerId = ownerId;
	}
	
	public List<Address> findAddressesByUserId () throws SQLException {
		
		String query = "SELECT * FROM address WHERE owner = ?";
		List<Address> addresses = new ArrayList<>();
		
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, this.ownerId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Address address = new Address();
					address.setId(resultSet.getInt("id"));
					address.setOwner(resultSet.getInt("owner"));
					address.setUser(resultSet.getInt("user"));
					address.setAccount(resultSet.getInt("account"));
					address.setIdentifier(resultSet.getString("identifier"));
					addresses.add(address);
				}
			}
		}
		return addresses;
		
	}
	
	public void createAddress (int user, int account, String identifier) throws SQLException {
		
		String query = "INSERT INTO address (owner, user, account, identifier) VALUES (?, ?, ?, ?)";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, this.ownerId);
			preparedStatement.setInt(2, user);
			preparedStatement.setInt(3, account);
			preparedStatement.setString(4, identifier);
			preparedStatement.executeUpdate();
		}
		
	}
	
	public Address findAccountByParameters (int user, int account) throws SQLException {
		
		String query = "SELECT * FROM address WHERE owner = ? AND user = ? AND account = ?";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, this.ownerId);
			preparedStatement.setInt(2, user);
			preparedStatement.setInt(3, account);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (!resultSet.isBeforeFirst()) {
					return null;
				}
				resultSet.next();
				Address address = new Address();
				address.setId(resultSet.getInt("id"));
				address.setOwner(resultSet.getInt("owner"));
				address.setUser(resultSet.getInt("user"));
				address.setAccount(resultSet.getInt("account"));
				address.setIdentifier(resultSet.getString("identifier"));
				return address;
			}
		}
	
	}
	
}
