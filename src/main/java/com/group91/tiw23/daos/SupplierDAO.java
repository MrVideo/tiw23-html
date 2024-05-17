package com.group91.tiw23.daos;

import com.group91.tiw23.beans.SupplierBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Mario Merlo
 */
public class SupplierDAO {
    private final Connection connection;

    public SupplierDAO(Connection connection) {
        this.connection = connection;
    }

    public List<SupplierBean> getSuppliersByProductId(int productId) throws SQLException {
        String query = """
                SELECT S.SupplierID, S.Name, S.Rating, S.FreeShippingThreshold, I.Price
                FROM Supplier S, Inventory I
                WHERE S.SupplierID = I.SupplierID AND I.ProductID = ?
                ORDER BY I.Price;
                """;
        ArrayList<SupplierBean> suppliers = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, productId);
        ResultSet result = statement.executeQuery();

        while(result.next()) {
            SupplierBean bean = new SupplierBean();
            bean.setSupplierId(result.getInt("SupplierID"));
            bean.setName(result.getString("Name"));
            bean.setRating(result.getFloat("Rating"));
            bean.setFreeShippingThreshold(result.getFloat("FreeShippingThreshold"));
            bean.setPrice(result.getFloat("Price"));
            suppliers.add(bean);
        }

        result.close();
        statement.close();

        return suppliers;
    }

    public SupplierBean getSupplierById(int supplierId) throws SQLException {
        String query = "SELECT * FROM Supplier WHERE SupplierID = ?";
        SupplierBean bean = new SupplierBean();

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, supplierId);
        ResultSet result = statement.executeQuery();

        if(result.next()) {
            bean.setSupplierId(result.getInt("SupplierID"));
            bean.setName(result.getString("Name"));
            bean.setRating(result.getFloat("Rating"));
            bean.setFreeShippingThreshold(result.getFloat("FreeShippingThreshold"));
        }

        return bean;
    }
    
    public HashMap<Integer, String> getIdToNameMapping() throws SQLException {
        String query = "SELECT SupplierID, Name FROM Supplier";
        HashMap<Integer, String> mapping = new HashMap<>();

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery();


        while(result.next())
            mapping.put(result.getInt("SupplierID"), result.getString("Name"));

        result.close();
        statement.close();

        return mapping;
    }
}
