package com.group91.tiw23.daos;

import com.group91.tiw23.beans.OrderBean;
import com.group91.tiw23.beans.ProductBean;
import com.group91.tiw23.utils.CartEntry;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @author Mario Merlo
 */
public class OrderDAO {
    private final Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    public void placeOrder(int userId, int supplierId, ArrayList<CartEntry> products, float totalValue) throws SQLException {
        // Insert order details in table Order
        String insertOrder = "INSERT INTO `Order`(UserID, SupplierID, TotalValue, ShippingDate) VALUES (?, ?, ?, ?)";

        PreparedStatement insertOrderStatement = connection.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
        insertOrderStatement.setInt(1, userId);
        insertOrderStatement.setInt(2, supplierId);
        insertOrderStatement.setFloat(3, totalValue);
        insertOrderStatement.setDate(4, Date.valueOf(LocalDate.now().plusDays(1)));
        insertOrderStatement.executeUpdate();

        // Insert ordered items in table OrderedProducts
        ResultSet result = insertOrderStatement.getGeneratedKeys();
        int insertedId = 0;
        if(result.next())
            insertedId = result.getInt(1);

        connection.setAutoCommit(false);

        String insertProduct = "INSERT INTO OrderedProducts VALUES (?, ?)";
        PreparedStatement insertProductsStatement = connection.prepareStatement(insertProduct);
        insertProductsStatement.setInt(1, insertedId);

        for(CartEntry product : products) {
            insertProductsStatement.setInt(2, product.getProductBean().getProductId());
            insertProductsStatement.executeUpdate();
        }

        connection.commit();
    }

    public ArrayList<OrderBean> getOrderDetailsByUserId(int userId) throws SQLException {
        // Get order details from Order table
        String queryOrderDetails = """
                SELECT O.OrderID, S.Name, O.TotalValue, O.ShippingDate, U.ShippingAddress
                FROM `Order` O, OrderedProducts OP, User U, Supplier S
                WHERE O.OrderID = OP.OrderID
                  AND O.UserID = U.UserID
                  AND O.SupplierID = S.SupplierID
                  AND U.UserID = ?
                GROUP BY O.OrderID, O.ShippingDate
                ORDER BY O.ShippingDate DESC, O.OrderID DESC;
                """;

        ArrayList<OrderBean> orders = new ArrayList<>();
        PreparedStatement orderDetailsStatement = connection.prepareStatement(queryOrderDetails);
        orderDetailsStatement.setInt(1, userId);
        ResultSet result = orderDetailsStatement.executeQuery();

        while(result.next()) {
            OrderBean bean = new OrderBean();
            bean.setOrderId(result.getInt("OrderID"));
            bean.setSupplierName(result.getString("Name"));
            bean.setTotalValue(result.getFloat("TotalValue"));
            bean.setShippingDate(result.getDate("ShippingDate"));
            bean.setShippingAddress(result.getString("ShippingAddress"));
            orders.add(bean);
        }

        result.close();
        orderDetailsStatement.close();

        // Get product lists for orders from OrderedProducts table
        String queryOrderedProducts = """
                SELECT P.ProductID
                FROM OrderedProducts O, Product P
                WHERE O.ProductID = P.ProductID
                  AND O.OrderID = ?
                """;

        PreparedStatement productsStatement = connection.prepareStatement(queryOrderedProducts);
        ResultSet products = null;

        ProductDAO productDAO = new ProductDAO(connection);

        for(OrderBean order : orders) {
            productsStatement.setInt(1, order.getOrderId());
            products = productsStatement.executeQuery();

            ArrayList<ProductBean> orderedProducts = new ArrayList<>();

            while(products.next())
                orderedProducts.add(productDAO.getDetailsFromId(products.getInt("ProductID")));

            order.setProducts(orderedProducts);
        }

        if(products != null)
            products.close();
        productsStatement.close();

        return orders;
    }
}
