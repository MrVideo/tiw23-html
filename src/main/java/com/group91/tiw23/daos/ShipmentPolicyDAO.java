package com.group91.tiw23.daos;

import com.group91.tiw23.beans.ShipmentPolicyBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mario Merlo
 */
public class ShipmentPolicyDAO {
    private final Connection connection;

    public ShipmentPolicyDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ShipmentPolicyBean> getPoliciesBySupplier(int supplierId) throws SQLException {
        String query = "SELECT * FROM ShipmentPolicies WHERE SupplierID = ?";
        ArrayList<ShipmentPolicyBean> shipmentPolicies = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, supplierId);
        ResultSet result = statement.executeQuery();

        while(result.next()) {
            ShipmentPolicyBean bean = new ShipmentPolicyBean();
            bean.setPolicyId(result.getInt("PolicyID"));
            bean.setSupplierId(result.getInt("SupplierID"));
            bean.setMinItems(result.getInt("MinimumItems"));
            bean.setMaxItems(result.getInt("MaximumItems"));
            bean.setShipmentCost(result.getFloat("ShipmentCost"));
            shipmentPolicies.add(bean);
        }

        result.close();
        statement.close();

        return shipmentPolicies;
    }
}
