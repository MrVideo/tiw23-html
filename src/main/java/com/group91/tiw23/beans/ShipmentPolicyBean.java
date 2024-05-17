package com.group91.tiw23.beans;

/**
 * @author Mario Merlo
 */
public class ShipmentPolicyBean {
    private int policyId, supplierId, minItems, maxItems;
    private float shipmentCost;

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getMinItems() {
        return minItems;
    }

    public void setMinItems(int minItems) {
        this.minItems = minItems;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public float getShipmentCost() {
        return shipmentCost;
    }

    public void setShipmentCost(float shipmentCost) {
        this.shipmentCost = shipmentCost;
    }
}
