package models;

public class Subscription {
    private int id;
    private int customerId;
    private String planName;
    private double price;
    private String startDate;
    private String endDate;

    public Subscription(int id, int customerId, String planName, double price, String startDate, String endDate) {
        this.id = id;
        this.customerId = customerId;
        this.planName = planName;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getPlanName() {
        return planName;
    }

    public double getPrice() {
        return price;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
