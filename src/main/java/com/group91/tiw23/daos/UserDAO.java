package com.group91.tiw23.daos;

import com.group91.tiw23.beans.UserBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Mario Merlo
 */
public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean login(String email, String password) throws SQLException {
        String query = "SELECT COUNT(*) FROM User WHERE Email = ? AND Password = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        statement.setString(2, password);
        ResultSet result = statement.executeQuery();

        if(result.next())
            return result.getInt(1) > 0;

        return false;
    }

    public UserBean getUserDataFromEmail(String email) throws SQLException {
        String query = "SELECT * FROM User WHERE Email = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet result = statement.executeQuery();

        UserBean bean = new UserBean();

        if(result.next()) {
            bean.setUserId(result.getInt("UserID"));
            bean.setEmail(result.getString("Email"));
            bean.setFirstName(result.getString("FirstName"));
            bean.setLastName(result.getString("LastName"));
            bean.setShippingAddress(result.getString("ShippingAddress"));
        }

        return bean;
    }
}
