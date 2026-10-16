package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnItemActionListener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private RecyclerView rvPantry;
    private TextView tvEmpty;
    private TextView tvExpiring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);
        rvPantry = findViewById(R.id.rvPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvExpiring = findViewById(R.id.tvExpiring);
        Button btnAdd = findViewById(R.id.btnAdd);

        adapter = new PantryAdapter(new ArrayList<>(), this);
        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        rvPantry.setAdapter(adapter);

        // Opens the Add/Edit screen in "add" mode (no item id passed)
        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditActivity.class)));

        BottomNavHelper.setup(this, R.id.nav_pantry);
    }

    // Runs every time the screen becomes visible, so the list is always up to date
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    // Reads from SQLite and refreshes the list; shows a message if it is empty
    private void loadItems() {
        List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items);
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvPantry.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvPantry.setVisibility(View.VISIBLE);
        }
        updateExpiryAlert(items);
    }

    // Shows a banner for items that are expired or expire within 3 days,
    // but only if the user switched alerts on in Settings
    private void updateExpiryAlert(List<PantryItem> items) {
        if (!SettingsActivity.isExpiryAlertsEnabled(this)) {
            tvExpiring.setVisibility(View.GONE);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);
        long limit = System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000;
        int count = 0;
        for (PantryItem item : items) {
            String expiry = item.getExpiryDate();
            if (expiry == null || expiry.isEmpty()) {
                continue;
            }
            try {
                Date date = sdf.parse(expiry);
                if (date != null && date.getTime() <= limit) {
                    count++;
                }
            } catch (ParseException e) {
                // ignore a badly formatted date
            }
        }
        if (count > 0) {
            tvExpiring.setText("Warning: " + count + " item(s) expired or expiring within 3 days");
            tvExpiring.setVisibility(View.VISIBLE);
        } else {
            tvExpiring.setVisibility(View.GONE);
        }
    }

    // Opens the Add/Edit screen in "edit" mode by passing the item's id in the Intent
    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra(AddEditActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    loadItems();
                    Toast.makeText(this, item.getName() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}