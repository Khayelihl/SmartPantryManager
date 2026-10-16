package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private RecyclerView rvRecipes;
    private TextView tvNoMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);

        db = new DatabaseHelper(this);
        rvRecipes = findViewById(R.id.rvRecipes);
        tvNoMatch = findViewById(R.id.tvNoMatch);
        Button btnBack = findViewById(R.id.btnBack);

        adapter = new RecipeAdapter(new ArrayList<>(), this);
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        BottomNavHelper.setup(this, R.id.nav_suggested);
    }

    // Re-runs the matching every time the screen is shown
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<Recipe> allRecipes = db.getAllRecipes();
        List<PantryItem> pantry = db.getAllPantryItems();

        // The strict-matching rule from Step 7 decides what is shown
        List<Recipe> matches = RecipeMatcher.findStrictMatches(allRecipes, pantry);

        adapter.setRecipes(matches);
        if (matches.isEmpty()) {
            tvNoMatch.setVisibility(View.VISIBLE);
            rvRecipes.setVisibility(View.GONE);
        } else {
            tvNoMatch.setVisibility(View.GONE);
            rvRecipes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}