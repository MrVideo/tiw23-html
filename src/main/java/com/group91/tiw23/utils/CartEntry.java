package com.group91.tiw23.utils;

import com.group91.tiw23.beans.ProductBean;
import com.group91.tiw23.beans.SupplierBean;

/**
 * @author Mario Merlo
 */
public class CartEntry {
    private ProductBean productBean;
    private SupplierBean supplierBean;
    private int quantity;

    public CartEntry(ProductBean productBean, SupplierBean supplierBean, int quantity) {
        this.productBean = productBean;
        this.supplierBean = supplierBean;
        this.quantity = quantity;
    }

    public CartEntry(String cartEntryString) {
        String[] splits = cartEntryString.split("_");

        productBean = new ProductBean();
        productBean.setProductId(Integer.parseInt(splits[0]));

        supplierBean = new SupplierBean();
        supplierBean.setSupplierId(Integer.parseInt(splits[1]));

        quantity = Integer.parseInt(splits[2]);
    }

    public ProductBean getProductBean() {
        return productBean;
    }

    public SupplierBean getSupplierBean() {
        return supplierBean;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void setProductBean(ProductBean productBean) {
        this.productBean = productBean;
    }

    public void setSupplierBean(SupplierBean supplierBean) {
        this.supplierBean = supplierBean;
    }

    @Override
    public String toString() {
        return productBean.getProductId() + "_" +
                supplierBean.getSupplierId() + "_" +
                quantity + ".";
    }
}
