package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    // Key used to pass the item id from MainActivity through the Intent
    public static final String EXTRA_ITEM_ID = "item_id";

    private static final String[] UNITS = {"g", "kg", "ml", "l", "pcs"};

    private DatabaseHelper db;
    private EditText etName, etQuantity, etExpiry;
    private Spinner spUnit;
    private int editingId = -1; // -1 means "add mode"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        TextView tvTitle = findViewById(R.id.tvFormTitle);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        spUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, UNITS));

        // If an id was passed in the Intent, we are editing: load and show that item
        editingId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (editingId != -1) {
            tvTitle.setText("Edit Ingredient");
            PantryItem item = db.getPantryItem(editingId);
            if (item != null) {
                etName.setText(item.getName());
                etQuantity.setText(String.valueOf(item.getQuantity()));
                etExpiry.setText(item.getExpiryDate());
                for (int i = 0; i < UNITS.length; i++) {
                    if (UNITS[i].equals(item.getUnit())) {
                        spUnit.setSelection(i);
                    }
                }
            }
        }

        btnSave.setOnClickListener(v -> save());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String unit = (String) spUnit.getSelectedItem();

        // ----- Input validation -----
        if (name.isEmpty()) {
            etName.setError("Please enter an ingredient name");
            return;
        }
        if (qtyText.isEmpty()) {
            etQuantity.setError("Please enter a quantity");
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Quantity must be a number");
            return;
        }
        if (quantity <= 0) {
            etQuantity.setError("Quantity must be greater than 0");
            return;
        }
        if (!expiry.isEmpty() && !isValidDate(expiry)) {
            etExpiry.setError("Use the format yyyy-MM-dd (e.g. 2026-12-31)");
            return;
        }

        String expiryToSave = expiry.isEmpty() ? null : expiry;

        if (editingId == -1) {
            db.addPantryItem(new PantryItem(name, quantity, unit, expiryToSave));
            Toast.makeText(this, name + " added", Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(new PantryItem(editingId, name, quantity, unit, expiryToSave));
            Toast.makeText(this, name + " updated", Toast.LENGTH_SHORT).show();
        }
        finish(); // go back; MainActivity.onResume() reloads the list
    }

    // Strict date check: rejects things like 2026-13-45
    private boolean isValidDate(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);
        try {
            sdf.parse(text);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
