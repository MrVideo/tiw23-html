package com.group91.tiw23.beans;

import java.util.List;

/**
 * @author Mario Merlo
 */
public class SupplierBean {
    private int supplierId, totalCartQuantity;
    private String name;
    private List<ShipmentPolicyBean> shippingPolicies;
    private float rating;
    private float freeShippingThreshold, totalCartValue, price;

    public int getSupplierId() {
        return supplierId;
    }

    public int getTotalCartQuantity() {
        return totalCartQuantity;
    }

    public void setTotalCartQuantity(int totalCartQuantity) {
        this.totalCartQuantity = totalCartQuantity;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShipmentPolicyBean> getShippingPolicies() {
        return shippingPolicies;
    }

    public void setShippingPolicies(List<ShipmentPolicyBean> shippingPolicies) {
        this.shippingPolicies = shippingPolicies;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getFreeShippingThreshold() {
        return freeShippingThreshold;
    }

    public void setFreeShippingThreshold(float freeShippingThreshold) {
        this.freeShippingThreshold = freeShippingThreshold;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setTotalCartValue(float totalCartValue) {
        this.totalCartValue = totalCartValue;
    }

    public float getTotalCartValue() {
        return totalCartValue;
    }
}
