package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    // Key used to pass the recipe id from the Suggested Recipes screen
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvSteps = findViewById(R.id.tvDetailSteps);
        Button btnBack = findViewById(R.id.btnDetailBack);

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(recipeId);

        if (recipe != null) {
            tvName.setText(recipe.getName());

            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient ing : recipe.getIngredients()) {
                sb.append("\u2022 ").append(ing.getDisplay()).append("\n");
            }
            tvIngredients.setText(sb.toString().trim());
            tvSteps.setText(recipe.getSteps());
        } else {
            tvName.setText("Recipe not found");
        }

        btnBack.setOnClickListener(v -> finish());
    }
}