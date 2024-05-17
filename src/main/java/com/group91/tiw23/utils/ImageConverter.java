package com.group91.tiw23.utils;

import jakarta.xml.bind.DatatypeConverter;

import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author Mario Merlo
 */
public class ImageConverter {
    public static String imageToBase64(Blob image) throws SQLException {
        byte[] bytes = image.getBytes(1, (int) image.length());
        return DatatypeConverter.printBase64Binary(bytes);
    }
}
