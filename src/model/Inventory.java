package model;

public class Inventory {

    private int upcCode;
    private String weightMeasure;
    private String name;
    private double price;
    private double quantity;

    public Inventory(int upcCode, String weightMeasure, String name, double price, double quantity) {
        this.upcCode = upcCode;
        this.weightMeasure = weightMeasure;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }


    public int getUpcCode() {
        return upcCode;
    }

    public String getWeightMeasure() {
        return weightMeasure;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setUpcCode(int upcCode) {
        this.upcCode = upcCode;
    }

    public void setWeightMeasure(String weightMeasure) {
        this.weightMeasure = weightMeasure;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "model.Inventory{" +
                "upcCode=" + upcCode +
                ", weightMeasure='" + weightMeasure + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
