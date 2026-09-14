package com.shofiqul.test_app;

public class TransactionItem {
    private long id;
    private String title;
    private float buyPrice;
    private float sellPrice;
    private int quantity;
    private float extraCost;
    private float vatPercent;
    private float discountPercent;
    private float netProfit;
    private float marginPercent;
    private String date;
    private long timestamp;

    public TransactionItem() {
    }

    public TransactionItem(long id, String title, float buyPrice, float sellPrice, int quantity,
                           float extraCost, float vatPercent, float discountPercent,
                           float netProfit, float marginPercent, String date, long timestamp) {
        this.id = id;
        this.title = title;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.extraCost = extraCost;
        this.vatPercent = vatPercent;
        this.discountPercent = discountPercent;
        this.netProfit = netProfit;
        this.marginPercent = marginPercent;
        this.date = date;
        this.timestamp = timestamp;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title == null || title.isEmpty() ? "সাধারণ হিসাব" : title; }
    public void setTitle(String title) { this.title = title; }

    public float getBuyPrice() { return buyPrice; }
    public void setBuyPrice(float buyPrice) { this.buyPrice = buyPrice; }

    public float getSellPrice() { return sellPrice; }
    public void setSellPrice(float sellPrice) { this.sellPrice = sellPrice; }

    public int getQuantity() { return Math.max(1, quantity); }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public float getExtraCost() { return extraCost; }
    public void setExtraCost(float extraCost) { this.extraCost = extraCost; }

    public float getVatPercent() { return vatPercent; }
    public void setVatPercent(float vatPercent) { this.vatPercent = vatPercent; }

    public float getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(float discountPercent) { this.discountPercent = discountPercent; }

    public float getNetProfit() { return netProfit; }
    public void setNetProfit(float netProfit) { this.netProfit = netProfit; }

    public float getMarginPercent() { return marginPercent; }
    public void setMarginPercent(float marginPercent) { this.marginPercent = marginPercent; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public float getTotalCost() {
        return (buyPrice * getQuantity()) + extraCost;
    }

    public float getTotalRevenue() {
        float subtotal = sellPrice * getQuantity();
        float discount = subtotal * (discountPercent / 100f);
        float afterDiscount = subtotal - discount;
        float vat = afterDiscount * (vatPercent / 100f);
        return afterDiscount + vat;
    }
}
