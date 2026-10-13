package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    // CORE RULE: returns only recipes where EVERY ingredient is in the pantry
    // in at least the required quantity. Partial matches are excluded.
    public static List<Recipe> findStrictMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> available = buildAvailableMap(pantry);
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, available)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    // A recipe can be made only if no single ingredient is missing or short
    private static boolean canMake(Recipe recipe, Map<String, Double> available) {
        if (recipe.getIngredients().isEmpty()) {
            return false;
        }
        for (RecipeIngredient ing : recipe.getIngredients()) {
            String key = makeKey(ing.getName(), ing.getUnit());
            double needed = toBaseQuantity(ing.getQuantity(), ing.getUnit());
            Double have = available.get(key);
            if (have == null || have + 0.0001 < needed) {
                return false; // missing or not enough -> reject the whole recipe
            }
        }
        return true;
    }

    // Totals the pantry per ingredient, e.g. two "tomato 2 pcs" rows become 4
    private static Map<String, Double> buildAvailableMap(List<PantryItem> pantry) {
        Map<String, Double> map = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = makeKey(item.getName(), item.getUnit());
            double qty = toBaseQuantity(item.getQuantity(), item.getUnit());
            map.put(key, map.getOrDefault(key, 0.0) + qty);
        }
        return map;
    }

    // Key = cleaned name + base unit, so "Tomatoes|pcs" and "tomato|pcs" are the same
    private static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + baseUnit(unit);
    }

    // Lowercase, trim, collapse spaces, and make the last word singular
    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (s.isEmpty()) {
            return s;
        }
        int lastSpace = s.lastIndexOf(' ');
        String prefix = s.substring(0, lastSpace + 1); // empty if one word
        String lastWord = s.substring(lastSpace + 1);
        return prefix + singular(lastWord);
    }

    // Simple plural -> singular rules (not full NLP, but handles common cases)
    private static String singular(String w) {
        if (w.length() <= 3) {
            return w;
        }
        if (w.endsWith("ies")) {
            return w.substring(0, w.length() - 3) + "y";   // berries -> berry
        }
        if (w.endsWith("oes") || w.endsWith("ches") || w.endsWith("shes")
                || w.endsWith("xes") || w.endsWith("sses")) {
            return w.substring(0, w.length() - 2);          // tomatoes -> tomato
        }
        if (w.endsWith("ss")) {
            return w;                                       // glass stays glass
        }
        if (w.endsWith("s")) {
            return w.substring(0, w.length() - 1);          // eggs -> egg
        }
        return w;
    }

    // Maps every unit to a base unit: g, ml or pcs
    private static String baseUnit(String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "g":
            case "kg":
                return "g";
            case "ml":
            case "l":
                return "ml";
            default:
                return u; // "pcs"
        }
    }

    // Converts a quantity into the base unit (1 kg = 1000 g, 1 l = 1000 ml)
    private static double toBaseQuantity(double qty, String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        if (u.equals("kg") || u.equals("l")) {
            return qty * 1000.0;
        }
        return qty;
    }
}
