package com.example.smartpantrymanager;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnItemActionListener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private RecyclerView rvPantry;
    private TextView tvEmpty;

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
        Button btnAdd = findViewById(R.id.btnAdd);

        adapter = new PantryAdapter(new ArrayList<>(), this);
        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        rvPantry.setAdapter(adapter);

        // TEMPORARY: adds a sample item so you can see the list working.
        // We replace this with the real Add screen in Step 5.
        btnAdd.setOnClickListener(v -> {
            db.addPantryItem(new PantryItem("Tomato", 2, "pcs", null));
            loadItems();
        });
    }

    // Runs every time the screen becomes visible, so the list is always up to date
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    // Reads from SQLite and refreshes the list; shows a message if it is empty
    private void loadItems() {
        java.util.List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items);
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvPantry.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvPantry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEdit(PantryItem item) {
        // TEMPORARY: the Edit screen is built in Step 5
        Toast.makeText(this, "Edit screen coming in Step 5", Toast.LENGTH_SHORT).show();
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
