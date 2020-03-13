package com.example.thweather;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private static final String CITY_NAME = "CITY";
    private static final String TEMPERATURE_UNITS = "TEMP";
    private static final String WEATHER_NOTIFICATIONS = "NOTIFY";

    Toolbar toolbar;
    AlertDialog.Builder builder;
    TextView set_unit;
    String city = "auto_ip";
    String unit = "Metric";
    boolean flag = true;
    String[] units = new String[] {"Metric", "British"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        city = getCityName();
        unit = getTemperatureUnits();
        flag = getNotification();

        toolbar = (Toolbar) findViewById(R.id.settings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewt6) {
                finish();
            }
        });

        final TextView setIp = (TextView) findViewById(R.id.set_ip);
        setIp.setText(city);
        final EditText editLocation = (EditText) findViewById(R.id.edit_location);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.set_location);
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.edit_loc);
        Button button_location = (Button) findViewById(R.id.button_location);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });
        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = editLocation.getText().toString();
                relativeLayout.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                if (city.equals("")) {
                    city = "auto_ip";
                }
                setIp.setText(city);
                saveCity(city);
            }
        });
        LinearLayout temp_units = (LinearLayout) findViewById(R.id.temp_units);
        set_unit = (TextView) findViewById(R.id.set_units);
        set_unit.setText(unit);
        temp_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelect();
            }
        });
        final TextView set_notify = (TextView) findViewById(R.id.set_notify);
        CheckBox enable = (CheckBox) findViewById(R.id.enable_checkbox);
        enable.setChecked(flag);
        if (flag) {
            set_notify.setText("Enable");
        } else {
            set_notify.setText("Disable");
        }
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                flag = b;
                if (flag) {
                    set_notify.setText("Enable");
                } else {
                    set_notify.setText("Disable");
                }
                saveNotification(flag);
            }
        });
    }

    private void showSelect() {
        builder = new AlertDialog.Builder(this)
                .setTitle("Temperature Units")
                .setItems(units, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        unit = units[i];
                        set_unit.setText(unit);
                        saveTemperatureUnits(unit);
                    }
                });
        builder.create().show();
    }

    public void saveCity(String city) {
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString(CITY_NAME, city);
        editor.commit();
    }

    public String getCityName() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        return preferences.getString(CITY_NAME, "");
    }

    public void saveTemperatureUnits(String unit) {
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString(TEMPERATURE_UNITS, unit);
        editor.commit();
    }

    public String getTemperatureUnits() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        return preferences.getString(TEMPERATURE_UNITS, "");
    }

    public void saveNotification(boolean flag) {
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putBoolean(WEATHER_NOTIFICATIONS, flag);
        editor.commit();
    }

    public boolean getNotification() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        return preferences.getBoolean(WEATHER_NOTIFICATIONS, true);
    }
}