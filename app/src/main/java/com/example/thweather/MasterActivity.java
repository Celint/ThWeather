package com.example.thweather;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public class MasterActivity extends SingleFragmentActivity
        implements MasterFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new MasterFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onWeatherSelected(Weather weather) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = DetailActivity.newIntent(this, weather);
            startActivity(intent);
        } else {
            Fragment newDetail = new DetailFragment().newInstance(weather);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .addToBackStack(null)
                    .commit();
        }
    }

}