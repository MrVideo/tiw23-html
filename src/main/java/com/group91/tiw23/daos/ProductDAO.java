package com.group91.tiw23.daos;

import com.group91.tiw23.beans.ProductBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.group91.tiw23.utils.ImageConverter.imageToBase64;

/**
 * @author Mario Merlo
 */
public class ProductDAO {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ProductBean> productSearch(String keyword) throws SQLException {
        String query = """
                SELECT P.ProductID, P.Name, I1.Price
                FROM Product P, Inventory I1
                WHERE P.ProductID = I1.ProductID
                  AND (P.Name LIKE ? OR P.Description LIKE ?)
                  AND I1.Price = (
                    SELECT MIN(I2.Price)
                    FROM Inventory I2
                    WHERE P.ProductID = I2.ProductID
                    )
                ORDER BY I1.Price;
                """;

        ArrayList<ProductBean> products = new ArrayList<>();

        String formattedKeyword = "%" + keyword + "%";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, formattedKeyword);
        statement.setString(2, formattedKeyword);
        ResultSet result = statement.executeQuery();


        while(result.next()) {
            ProductBean bean = new ProductBean();
            bean.setProductId(result.getInt("ProductID"));
            bean.setName(result.getString("Name"));
            bean.setPrice(result.getFloat("Price"));
            products.add(bean);
        }

        return products;
    }

    public ProductBean getDetailsFromId(int productId) throws SQLException {
        String query = """
                SELECT * FROM Product WHERE ProductID = ?;
                """;
        ProductBean bean = new ProductBean();

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, productId);
        ResultSet result = statement.executeQuery();

        if(result.next()) {
            bean.setProductId(result.getInt("ProductID"));
            bean.setName(result.getString("Name"));
            bean.setDescription(result.getString("Description"));
            bean.setCategory(result.getString("Category"));
            bean.setPhoto(imageToBase64(result.getBlob("Photo")));
        }

        return bean;
    }
}
