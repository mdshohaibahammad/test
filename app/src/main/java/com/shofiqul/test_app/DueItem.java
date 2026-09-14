package com.shofiqul.test_app;

public class DueItem {
    private long id;
    private String customerName;
    private String customerPhone;
    private float dueAmount;
    private String notes;
    private String date;
    private String status; // "DUE", "PAID"

    public DueItem() {}

    public DueItem(long id, String customerName, String customerPhone, float dueAmount, String notes, String date, String status) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.dueAmount = dueAmount;
        this.notes = notes;
        this.date = date;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCustomerName() { return customerName == null ? "" : customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone == null ? "" : customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public float getDueAmount() { return dueAmount; }
    public void setDueAmount(float dueAmount) { this.dueAmount = dueAmount; }

    public String getNotes() { return notes == null ? "" : notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDate() { return date == null ? "" : date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status == null || status.isEmpty() ? "DUE" : status; }
    public void setStatus(String status) { this.status = status; }
}
