package com.group91.tiw23.daos;

import com.group91.tiw23.beans.ProductBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.group91.tiw23.utils.ImageConverter.imageToBase64;

/**
 * @author Mario Merlo
 */
public class ViewsDAO {
    private final Connection connection;

    public ViewsDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ProductBean> getRecentlyViewedItems(int UserID) throws SQLException {
        List<ProductBean> beanList = new ArrayList<>();
        String query = """
                SELECT P.ProductID, Name, Description, Category, Photo
                FROM Product P
                JOIN Views V on P.ProductID = V.ProductID
                WHERE UserID = ?
                ORDER BY LastViewedOn DESC
                LIMIT 5;
                """;

        PreparedStatement statement;
        ResultSet result;
        statement = connection.prepareStatement(query);
        statement.setInt(1, UserID);
        result = statement.executeQuery();

        while(result.next()) {
            ProductBean bean = new ProductBean();
            bean.setProductId(result.getInt("ProductID"));
            bean.setName(result.getString("Name"));
            bean.setDescription(result.getString("Description"));
            bean.setCategory(result.getString("Category"));
            bean.setPhoto(imageToBase64(result.getBlob("Photo")));
            beanList.add(bean);
        }

        result.close();
        statement.close();

        if(beanList.size() < 5) {
            int leftoverSlots = 5 - beanList.size();

            String defaultQuery = "SELECT * FROM Product WHERE Category = 'Electronics' LIMIT 5";

            statement = connection.prepareStatement(defaultQuery);
            result = statement.executeQuery();

            while(result.next() && leftoverSlots > 0) {
                ProductBean bean = new ProductBean();
                bean.setProductId(result.getInt("ProductID"));
                bean.setName(result.getString("Name"));
                bean.setDescription(result.getString("Description"));
                bean.setCategory(result.getString("Category"));
                bean.setPhoto(imageToBase64(result.getBlob("Photo")));
                if(!isInProductList(beanList, bean)) {
                    beanList.add(bean);
                    leftoverSlots--;
                }
            }

            result.close();
            statement.close();
        }

        return beanList;
    }

    public void updateLastViewedDate(int userId, int productId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Views WHERE UserID = ? AND ProductID = ?";
        boolean alreadyViewed = false;

        PreparedStatement queryStatement = connection.prepareStatement(query);
        queryStatement.setInt(1, userId);
        queryStatement.setInt(2, productId);
        ResultSet result = queryStatement.executeQuery();

        if(result.next())
            alreadyViewed = result.getInt(1) > 0;

        result.close();
        queryStatement.close();

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        if(alreadyViewed) {
            String update = "UPDATE Views SET LastViewedOn = ? WHERE UserID = ? AND ProductID = ?";

            PreparedStatement updateStatement = connection.prepareStatement(update);
            updateStatement.setTimestamp(1, currentTime);
            updateStatement.setInt(2, userId);
            updateStatement.setInt(3, productId);
            updateStatement.executeUpdate();

            updateStatement.close();
        } else {
            String insert = "INSERT INTO Views(UserID, ProductID, LastViewedOn) VALUES(?, ?, ?)";

            PreparedStatement insertStatement = connection.prepareStatement(insert);
            insertStatement.setInt(1, userId);
            insertStatement.setInt(2, productId);
            insertStatement.setTimestamp(3, currentTime);
            insertStatement.executeUpdate();

            insertStatement.close();
        }
    }

    private boolean isInProductList(List<ProductBean> list, ProductBean product) {
        for(ProductBean bean : list) {
            if(bean.getProductId() == product.getProductId())
                return true;
        }
        return false;
    }
}
