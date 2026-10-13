package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smartpantry.db";
    private static final int DB_VERSION = 2;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // Recipe tables
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    public static final String TABLE_RECIPE_ING = "recipe_ingredients";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_ING_NAME = "ingredient_name";
    public static final String COL_ING_QTY = "quantity";
    public static final String COL_ING_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL, "
                + COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_NAME + " TEXT NOT NULL, "
                + COL_RECIPE_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_ING + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_ING_NAME + " TEXT NOT NULL, "
                + COL_ING_QTY + " REAL NOT NULL, "
                + COL_ING_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES "
                + TABLE_RECIPES + "(" + COL_ID + ") ON DELETE CASCADE)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_ING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ---------- CREATE ----------
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, values);
    }

    // ---------- READ (all items) ----------
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE ASC");
        while (cursor.moveToNext()) {
            items.add(cursorToItem(cursor));
        }
        cursor.close();
        return items;
    }

    // ---------- READ (one item) ----------
    public PantryItem getPantryItem(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = cursorToItem(cursor);
        }
        cursor.close();
        return item;
    }

    // ---------- UPDATE ----------
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.update(TABLE_PANTRY, values, COL_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    // ---------- DELETE ----------
    public int deletePantryItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // ---------- SEED DATA (runs once, when the database is first created) ----------
    private void seedRecipes(SQLiteDatabase db) {
        insertRecipe(db, "Scrambled Eggs on Toast",
                "1. Whisk the eggs.\n2. Melt butter in a pan and scramble the eggs.\n3. Toast the bread and serve the eggs on top.",
                new Object[][]{{"egg", 2.0, "pcs"}, {"bread", 2.0, "pcs"}, {"butter", 10.0, "g"}});

        insertRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt butter in a pan and pour in the eggs.\n3. Add grated cheese, fold and cook until set.",
                new Object[][]{{"egg", 3.0, "pcs"}, {"cheese", 50.0, "g"}, {"butter", 10.0, "g"}});

        insertRecipe(db, "Tomato Pasta",
                "1. Boil the pasta.\n2. Fry chopped onion and garlic in olive oil.\n3. Add chopped tomatoes and simmer 10 minutes.\n4. Mix with the pasta.",
                new Object[][]{{"pasta", 200.0, "g"}, {"tomato", 4.0, "pcs"}, {"onion", 1.0, "pcs"}, {"garlic", 2.0, "pcs"}, {"olive oil", 15.0, "ml"}});

        insertRecipe(db, "Egg Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry chopped onion and carrot.\n3. Push aside, scramble the eggs.\n4. Add rice and soy sauce, stir-fry 3 minutes.",
                new Object[][]{{"rice", 200.0, "g"}, {"egg", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"carrot", 1.0, "pcs"}, {"soy sauce", 15.0, "ml"}});

        insertRecipe(db, "Creamy Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash.\n3. Stir in butter and warm milk.",
                new Object[][]{{"potato", 500.0, "g"}, {"butter", 30.0, "g"}, {"milk", 50.0, "ml"}});

        insertRecipe(db, "Simple Pancakes",
                "1. Mix flour, sugar, egg and milk into a smooth batter.\n2. Melt butter in a pan.\n3. Fry spoonfuls of batter until golden on both sides.",
                new Object[][]{{"flour", 150.0, "g"}, {"egg", 1.0, "pcs"}, {"milk", 200.0, "ml"}, {"butter", 20.0, "g"}, {"sugar", 20.0, "g"}});

        insertRecipe(db, "Cheese Toastie",
                "1. Butter the outside of the bread.\n2. Place cheese between the slices.\n3. Toast in a pan until golden and melted.",
                new Object[][]{{"bread", 2.0, "pcs"}, {"cheese", 40.0, "g"}, {"butter", 10.0, "g"}});

        insertRecipe(db, "Vegetable Soup",
                "1. Chop all vegetables.\n2. Fry the onion until soft.\n3. Add the rest, a stock cube and enough water to cover.\n4. Simmer 25 minutes.",
                new Object[][]{{"potato", 200.0, "g"}, {"carrot", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"tomato", 2.0, "pcs"}, {"stock cube", 1.0, "pcs"}});

        insertRecipe(db, "Chicken Rice Bowl",
                "1. Cook the rice.\n2. Fry chicken pieces and onion in olive oil until cooked through.\n3. Serve the chicken over the rice.",
                new Object[][]{{"chicken", 300.0, "g"}, {"rice", 200.0, "g"}, {"onion", 1.0, "pcs"}, {"olive oil", 15.0, "ml"}});

        insertRecipe(db, "Tuna Pasta",
                "1. Boil the pasta.\n2. Fry chopped onion in olive oil.\n3. Add the tuna and heat through.\n4. Toss with the pasta.",
                new Object[][]{{"pasta", 200.0, "g"}, {"tuna", 1.0, "pcs"}, {"onion", 1.0, "pcs"}, {"olive oil", 15.0, "ml"}});

        insertRecipe(db, "Banana Smoothie",
                "1. Peel the bananas.\n2. Blend with cold milk and honey until smooth.\n3. Serve immediately.",
                new Object[][]{{"banana", 2.0, "pcs"}, {"milk", 250.0, "ml"}, {"honey", 15.0, "ml"}});

        insertRecipe(db, "French Toast",
                "1. Whisk eggs, milk and sugar.\n2. Dip the bread slices.\n3. Fry in butter until golden on both sides.",
                new Object[][]{{"bread", 4.0, "pcs"}, {"egg", 2.0, "pcs"}, {"milk", 100.0, "ml"}, {"butter", 10.0, "g"}, {"sugar", 10.0, "g"}});

        insertRecipe(db, "Egg Fried Noodles",
                "1. Boil the noodles and drain.\n2. Stir-fry sliced carrot.\n3. Add the eggs and scramble.\n4. Add noodles and soy sauce, toss well.",
                new Object[][]{{"noodle", 200.0, "g"}, {"egg", 2.0, "pcs"}, {"carrot", 1.0, "pcs"}, {"soy sauce", 15.0, "ml"}});

        insertRecipe(db, "Garlic Butter Potatoes",
                "1. Cut the potatoes into chunks and boil for 10 minutes.\n2. Melt butter with crushed garlic in a pan.\n3. Fry the potatoes until crispy.",
                new Object[][]{{"potato", 400.0, "g"}, {"butter", 30.0, "g"}, {"garlic", 3.0, "pcs"}});

        insertRecipe(db, "Tomato and Cheese Salad",
                "1. Slice the tomatoes.\n2. Add cubed cheese.\n3. Drizzle with olive oil and serve.",
                new Object[][]{{"tomato", 3.0, "pcs"}, {"cheese", 80.0, "g"}, {"olive oil", 10.0, "ml"}});

        insertRecipe(db, "Beans on Toast",
                "1. Heat the baked beans in a pot.\n2. Toast and butter the bread.\n3. Pour the beans over the toast.",
                new Object[][]{{"baked bean", 1.0, "pcs"}, {"bread", 2.0, "pcs"}, {"butter", 10.0, "g"}});

        insertRecipe(db, "Oatmeal Porridge",
                "1. Combine oats and milk in a pot.\n2. Cook on low heat for 5 minutes, stirring.\n3. Top with honey.",
                new Object[][]{{"oat", 80.0, "g"}, {"milk", 300.0, "ml"}, {"honey", 15.0, "ml"}});

        insertRecipe(db, "Chicken Stir-Fry",
                "1. Slice the chicken, carrots and onion.\n2. Fry the chicken until browned.\n3. Add vegetables and garlic, cook 5 minutes.\n4. Add soy sauce and toss.",
                new Object[][]{{"chicken", 300.0, "g"}, {"carrot", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"soy sauce", 20.0, "ml"}, {"garlic", 2.0, "pcs"}});

        insertRecipe(db, "Rice and Beans",
                "1. Cook the rice.\n2. Fry chopped onion until soft.\n3. Add the baked beans and heat through.\n4. Serve over the rice.",
                new Object[][]{{"rice", 200.0, "g"}, {"baked bean", 1.0, "pcs"}, {"onion", 1.0, "pcs"}});

        insertRecipe(db, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix in eggs and flour.\n3. Fry spoonfuls in a pan until golden on both sides.",
                new Object[][]{{"banana", 2.0, "pcs"}, {"egg", 2.0, "pcs"}, {"flour", 50.0, "g"}});
    }

    // Inserts one recipe row, then one row per ingredient linked by recipe_id
    private void insertRecipe(SQLiteDatabase db, String name, String steps, Object[][] ingredients) {
        ContentValues rv = new ContentValues();
        rv.put(COL_RECIPE_NAME, name);
        rv.put(COL_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, rv);

        for (Object[] ing : ingredients) {
            ContentValues iv = new ContentValues();
            iv.put(COL_RECIPE_ID, recipeId);
            iv.put(COL_ING_NAME, (String) ing[0]);
            iv.put(COL_ING_QTY, (Double) ing[1]);
            iv.put(COL_ING_UNIT, (String) ing[2]);
            db.insert(TABLE_RECIPE_ING, null, iv);
        }
    }

    // Used to check that seeding worked
    public int getRecipeCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECIPES, null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        return count;
    }

    // ---------- READ all recipes, each with its ingredient list ----------
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null, null, null, null, null,
                COL_RECIPE_NAME + " COLLATE NOCASE ASC");
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
            Recipe recipe = new Recipe(id,
                    c.getString(c.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_RECIPE_STEPS)));
            recipe.setIngredients(getIngredientsForRecipe(db, id));
            recipes.add(recipe);
        }
        c.close();
        return recipes;
    }

    // Reads the ingredient rows that belong to one recipe (linked by recipe_id)
    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor c = db.query(TABLE_RECIPE_ING, null, COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        while (c.moveToNext()) {
            list.add(new RecipeIngredient(
                    c.getString(c.getColumnIndexOrThrow(COL_ING_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_ING_QTY)),
                    c.getString(c.getColumnIndexOrThrow(COL_ING_UNIT))));
        }
        c.close();
        return list;
    }


    // Converts the current row of a Cursor into a PantryItem object
    private PantryItem cursorToItem(Cursor c) {
        return new PantryItem(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(COL_UNIT)),
                c.getString(c.getColumnIndexOrThrow(COL_EXPIRY)));
    }
}
