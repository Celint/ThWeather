package com.example.thweather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class DetailFragment extends Fragment {

    private static final String ARG_WEATHER_ID = "weather_id";

    AlertDialog.Builder builder;

    private Toolbar toolbar;
    Weather weather;

    public static DetailFragment newInstance(Weather weather) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_WEATHER_ID, weather);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isPad(getContext())) {
            weather = (Weather) getArguments().getParcelable(ARG_WEATHER_ID);
        } else {
            weather = getActivity().getIntent().getParcelableExtra("Weather");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_detail);
        toolbar.inflateMenu(R.menu.menu_detail);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share: {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                "天红天气为您服务\n" +
                                weather.getWeekday() + ", " +
                                weather.getDateName() + "\n" +
                                getCityName() + ", " + weather.getwName() + "\n" +
                                "Highest temperature: " + weather.gethDegree() + "\n" +
                                "Lowest temperature: " + weather.getlDegree() + "\n" +
                                "Humidity: " + weather.getHumidity() + " %\n" +
                                "Pressure: " + weather.getPressure() + " hPa\n" +
                                "Wind: " + weather.getWind() + " km/h SE");
                        startActivity(shareIntent);
                        break;
                    }
                    case R.id.message : {
                        showDialog();
                        break;
                    }
                    case R.id.set_map_location: {
                        openMap();
                        break;
                    }
                    case R.id.settings: {
                        startActivity(new Intent(getActivity(), SettingsActivity.class));
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        TextView weekday = (TextView) view.findViewById(R.id.detail_weekday);
        weekday.setText(weather.getWeekday());
        TextView date = (TextView) view.findViewById(R.id.detail_date);
        date.setText(weather.getDateName());
        TextView highDegree = (TextView) view.findViewById(R.id.detail_high_degree);
        TextView lowDegree = (TextView) view.findViewById(R.id.detail_low_degree);
        highDegree.setText(weather.gethDegree());
        lowDegree.setText(weather.getlDegree());
        ImageView weatherImg = (ImageView) view.findViewById(R.id.detail_weather_image);
        ApplicationInfo appInfo = getActivity().getApplicationInfo();
        int resID = getResources().getIdentifier("w" + weather.getwImageId(), "drawable", appInfo.packageName);
        weatherImg.setImageDrawable(getResources().getDrawable(resID));
        TextView weatherName = (TextView) view.findViewById(R.id.detail_weather_name);
        weatherName.setText(weather.getwName());
        TextView humidity = (TextView) view.findViewById(R.id.detail_humidity);
        humidity.setText("Humidity: " + weather.getHumidity() + " %");
        TextView pressure = (TextView) view.findViewById(R.id.detail_pressure);
        pressure.setText("Pressure: " + weather.getPressure() + " hPa");
        TextView wind = (TextView) view.findViewById(R.id.detail_wind);
        wind.setText("Wind: " + weather.getWind() + " km/h SE");

        return view;
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void showDialog() {
        builder = new AlertDialog.Builder(getActivity())
                .setTitle("我的信息")
                .setMessage("没有信息")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public String getCityName() {
        SharedPreferences preferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        return preferences.getString("CITY", "");
    }

    public void openMap() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/geocoder?src=andr.baidu.openAPIdemo&address=" + getCityName()));
        startActivity(intent);
    }
}
