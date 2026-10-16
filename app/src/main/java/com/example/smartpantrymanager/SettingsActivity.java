package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS = "smartpantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    // Other screens call this to find out whether alerts are switched on (default: on)
    public static boolean isExpiryAlertsEnabled(Context context) {
        return context.getSharedPreferences(PREFS, MODE_PRIVATE)
                .getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat switchExpiry = findViewById(R.id.switchExpiry);
        switchExpiry.setChecked(isExpiryAlertsEnabled(this));

        // Saves the choice immediately, so it survives closing the app
        switchExpiry.setOnCheckedChangeListener((button, isChecked) -> {
            SharedPreferences.Editor editor =
                    getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean(KEY_EXPIRY_ALERTS, isChecked);
            editor.apply();
        });

        BottomNavHelper.setup(this, R.id.nav_settings);
    }
}