package com.example.thweather;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class DetailActivity extends SingleFragmentActivity{

    private static final String EXTRA_WEATHER_ID = "com.example.thweather.weather_id";

    public static Intent newIntent(Context packageContext, Weather weather) {
        Intent intent = new Intent(packageContext, DetailActivity.class);
        intent.putExtra(EXTRA_WEATHER_ID, weather);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new DetailFragment();
    }
}
