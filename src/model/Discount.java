package model;

public class Discount {

    private final Double discountValue;
    private final boolean isPercentage;

    public Discount(Double discountValue, boolean isPercentage) {
        this.discountValue = discountValue;
        this.isPercentage = isPercentage;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public boolean isPercentage() {
        return isPercentage;
    }
}
