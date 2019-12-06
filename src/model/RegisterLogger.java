package model;

public class RegisterLogger {
    private final int upcCode;
    private final String itemName;
    private final String orderedQuantity;
    private final String itemPrice;
    private final String discount;
    private final String priceAfterDiscount;

    public RegisterLogger(int upcCode, String itemName, String orderedQuantity, String itemPrice, String discount, String priceAfterDiscount) {
        this.upcCode = upcCode;
        this.itemName = itemName;
        this.orderedQuantity = orderedQuantity;
        this.itemPrice = itemPrice;
        this.discount = discount;
        this.priceAfterDiscount = priceAfterDiscount;
    }


    public int getUpcCode() {
        return upcCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getOrderedQuantity() {
        return orderedQuantity;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public String getPriceAfterDiscount() {
        return priceAfterDiscount;
    }

    @Override
    public String toString() {
        return "model.RegisterLogger{" +
                "upcCode=" + upcCode +
                ", itemName='" + itemName + '\'' +
                ", orderedQuantity='" + orderedQuantity + '\'' +
                ", itemPrice='" + itemPrice + '\'' +
                ", discount='" + discount + '\'' +
                ", priceAfterDiscount='" + priceAfterDiscount + '\'' +
                '}';
    }
}
