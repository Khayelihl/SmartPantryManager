package com.example.smartpantrymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RecipeMatcherTest {

    // Cheese Omelette needs: 3 eggs, 50 g cheese, 10 g butter
    private List<Recipe> omeletteOnly() {
        Recipe r = new Recipe(1, "Cheese Omelette", "steps");
        r.setIngredients(Arrays.asList(
                new RecipeIngredient("egg", 3, "pcs"),
                new RecipeIngredient("cheese", 50, "g"),
                new RecipeIngredient("butter", 10, "g")));
        return Arrays.asList(r);
    }

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        // Also checks plural names ("Eggs") and unit conversion (0.1 kg = 100 g)
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("Eggs", 6, "pcs", null),
                new PantryItem("Cheese", 0.1, "kg", null),
                new PantryItem("Butter", 250, "g", null));
        assertEquals(1, RecipeMatcher.findStrictMatches(omeletteOnly(), pantry).size());
    }

    @Test
    public void oneIngredientMissing_recipeIsExcluded() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("Eggs", 6, "pcs", null),
                new PantryItem("Cheese", 100, "g", null));
        assertTrue(RecipeMatcher.findStrictMatches(omeletteOnly(), pantry).isEmpty());
    }

    @Test
    public void notEnoughQuantity_recipeIsExcluded() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("Eggs", 2, "pcs", null), // needs 3
                new PantryItem("Cheese", 100, "g", null),
                new PantryItem("Butter", 100, "g", null));
        assertTrue(RecipeMatcher.findStrictMatches(omeletteOnly(), pantry).isEmpty());
    }

    @Test
    public void duplicateRows_areAddedTogether() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("Egg", 2, "pcs", null),
                new PantryItem("Egg", 2, "pcs", null), // 2 + 2 = 4 >= 3
                new PantryItem("Cheese", 100, "g", null),
                new PantryItem("Butter", 100, "g", null));
        assertEquals(1, RecipeMatcher.findStrictMatches(omeletteOnly(), pantry).size());
    }
}
