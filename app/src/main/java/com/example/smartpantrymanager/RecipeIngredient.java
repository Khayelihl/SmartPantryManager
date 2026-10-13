package com.example.smartpantrymanager;

public class RecipeIngredient {
    private String name;
    private double quantity;
    private String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    // Text shown on screen, e.g. "2 pcs egg" or "50 g cheese"
    public String getDisplay() {
        String q = (quantity == Math.floor(quantity))
                ? String.valueOf((long) quantity)
                : String.valueOf(quantity);
        return q + " " + unit + " " + name;
    }
}
