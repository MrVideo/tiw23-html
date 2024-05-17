package com.group91.tiw23.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Mario Merlo
 */
public class InventoryDAO {
    private final Connection connection;

    public InventoryDAO(Connection connection) {
        this.connection = connection;
    }

    public float getPrice(int productId, int supplierId) throws SQLException {
        String query = "SELECT Price FROM Inventory WHERE ProductID = ? AND SupplierID = ?";
        float price = 0F;

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, productId);
        statement.setInt(2, supplierId);
        ResultSet result = statement.executeQuery();

        if(result.next())
            price = result.getFloat("Price");

        result.close();
        statement.close();

        return price;
    }
}
