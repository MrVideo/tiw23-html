package com.group91.tiw23.beans;

import java.sql.Date;
import java.util.ArrayList;

/**
 * @author Mario Merlo
 */
public class OrderBean {
    private int orderId;
    private String supplierName;
    private float totalValue;
    private Date shippingDate;
    private String shippingAddress;

    private ArrayList<ProductBean> products;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
    }

    public Date getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public ArrayList<ProductBean> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductBean> products) {
        this.products = products;
    }
}
