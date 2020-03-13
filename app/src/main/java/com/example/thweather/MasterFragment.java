package com.example.thweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class MasterFragment extends Fragment {

    private static final String WEATHER_NOTIFICATIONS = "NOTIFY";

    private List<Weather> weatherList = new ArrayList<>();
    private Weather todayWeather = new Weather();
    private WeatherNow now = new WeatherNow();

    private Callbacks mCallbacks;
    private WeatherLab weatherLab;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    String city;
    String unit;

    public interface Callbacks {
        void onWeatherSelected(Weather weather);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (isNetworkAvailableAndConnected()) {
            if (!isNotificationEnabled(getContext())) {
                gotoSet();
            }
            city = getCityName();
            unit = getTemperatureUnits();
            if (city.equals("")) {
                city = "auto_ip";
                Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "更新成功，当前城市为：" + city, Toast.LENGTH_SHORT).show();
            }
            if (unit.equals("")) {
                unit = "Metric";
            }
            new FetchItemsTask().execute();
            new FetchItemTask().execute();
        } else {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isNetworkAvailableAndConnected()) {
            int minute = getMinute(new Date());
            if (!city.equals(getCityName()) || !unit.equals(getTemperatureUnits())) {
                city = getCityName();
                unit = getTemperatureUnits();
                if (city.equals("")) {
                    city = "auto_ip";
                    Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "更新成功，当前城市为：" + city, Toast.LENGTH_SHORT).show();
                }
                if (unit.equals("")) {
                    unit = "Metric";
                }
                new FetchItemsTask().execute();
                new FetchItemTask().execute();
            } else if (minute % 15 == 0) {
                new FetchItemTask().execute();
            }
        } else {
            weatherQuery();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
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
        LinearLayout today_layout = (LinearLayout) view.findViewById(R.id.today_layout);
        today_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPad(getContext())) {
                    mCallbacks.onWeatherSelected(todayWeather);
                } else {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("Weather", todayWeather);
                    startActivity(intent);
                }
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.master_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        WeatherAdapter adapter = new WeatherAdapter(weatherList);
        recyclerView.setAdapter(adapter);

        setupAdapter();

        return view;
    }

    public class WeatherHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int id;
        ImageView weatherImage;     //天气图片
        TextView weatherName;       //天气名称
        TextView date;              //日期
        TextView highDegree;        //最高温
        TextView lowDegree;         //最低温

        public WeatherHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            weatherImage =(ImageView) view.findViewById(R.id.weather_image);
            weatherName = (TextView) view.findViewById(R.id.weather_name);
            date = (TextView) view.findViewById(R.id.date);
            highDegree = (TextView) view.findViewById(R.id.high_degree);
            lowDegree = (TextView) view.findViewById(R.id.low_degree);
        }

        @Override
        public void onClick(View view) {
            if (isPad(getContext())) {
                mCallbacks.onWeatherSelected(weatherList.get(id));
            } else {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("Weather", weatherList.get(id));
                startActivity(intent);
            }
        }
    }

    public class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> {

        private List<Weather> mWeatherList;

        public WeatherAdapter (List<Weather> weatherList) {
            mWeatherList = weatherList;
        }

        @Override
        public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.master_item, parent, false);
            WeatherHolder holder = new WeatherHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
            Weather weather = mWeatherList.get(position);
            ApplicationInfo appInfo = getActivity().getApplicationInfo();
            int resID = getResources().getIdentifier("w" + weather.getwImageId(), "drawable", appInfo.packageName);
            holder.id = position;
            holder.weatherImage.setImageDrawable(getResources().getDrawable(resID));
            holder.weatherName.setText(weather.getwName());
            holder.date.setText(weather.getWeekday());
            holder.highDegree.setText(weather.gethDegree());
            holder.lowDegree.setText(weather.getlDegree());
        }

        @Override
        public int getItemCount() {
            return mWeatherList.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<Weather>> {

        @Override
        protected List<Weather> doInBackground(Void... voids) {
            return new FlickrFetchr().fetchItems(city, unit);
        }

        @Override
        protected void onPostExecute(List<Weather> list) {
            todayWeather = list.get(0);
            todayWeather.setWeekday("Today");

            TextView master_weekday = (TextView) getView().findViewById(R.id.master_weekday);
            master_weekday.setText(todayWeather.getWeekday() + ", ");
            TextView master_date = (TextView) getView().findViewById(R.id.master_date);
            master_date.setText(todayWeather.getDateName());
            TextView master_high_degree = (TextView) getView().findViewById(R.id.master_high_degree);
            master_high_degree.setText(todayWeather.gethDegree());
            TextView master_low_degree = (TextView) getView().findViewById(R.id.master_low_degree);
            master_low_degree.setText(todayWeather.getlDegree());
            TextView master_weather = (TextView) getView().findViewById(R.id.master_weather_name);
            master_weather.setText(todayWeather.getwName());
            ImageView weather_image = (ImageView) getView().findViewById(R.id.master_weather_image);
            ApplicationInfo appInfo = getActivity().getApplicationInfo();
            int resID = getResources().getIdentifier("w" + todayWeather.getwImageId(), "drawable", appInfo.packageName);
            weather_image.setImageDrawable(getResources().getDrawable(resID));

            weatherList = list.subList(1, 7);
            weatherList.get(0).setWeekday("Tomorrow");
            setupAdapter();

            storeWeather(list);
        }

        public void storeWeather(List<Weather> list) {

            weatherLab = new WeatherLab(getActivity(), "Weathers.db", null, 1);
            SQLiteDatabase db = weatherLab.getWritableDatabase();
            db.execSQL("delete from weather");
            ContentValues values = new ContentValues();
            for (int i = 0; i < list.size(); i++) {
                values.put("wImageId", list.get(i).getwImageId());
                values.put("wName", list.get(i).getwName());
                values.put("dateName", list.get(i).getDateName());
                values.put("weekday", list.get(i).getWeekday());
                values.put("hDegree", list.get(i).gethDegree());
                values.put("lDegree", list.get(i).getlDegree());
                values.put("humidity", list.get(i).getHumidity());
                values.put("pressure", list.get(i).getPressure());
                values.put("wind", list.get(i).getWind());
                db.insert("weather", null, values);
                values.clear();
            }
        }
    }

    private class FetchItemTask extends AsyncTask<Void, Void, WeatherNow> {

        @Override
        protected WeatherNow doInBackground(Void... voids) {
            return new FlickrFetchr().fetchNow(city, unit);
        }

        protected void onPostExecute(WeatherNow weatherNow) {
            now = weatherNow;
            onSendNotification();
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            recyclerView.setAdapter(new WeatherAdapter(weatherList));
        }
    }

    public String getCityName() {
        SharedPreferences preferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        return preferences.getString("CITY", "");
    }

    public String getTemperatureUnits() {
        SharedPreferences preferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        return preferences.getString("TEMP", "");
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void openMap() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/geocoder?src=andr.baidu.openAPIdemo&address=" + getCityName()));
        startActivity(intent);
    }

    private boolean isNotificationEnabled(Context context) {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(context).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;
    }

    private void gotoSet() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT > 26) {
            // 8.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getActivity().getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getActivity().getPackageName());
            intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
        } else {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onSendNotification() {
        boolean flag = getNotification();
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getActivity(), "channel_id")
                .setContentTitle("天红天气为您播报今日实时天气")
                .setContentText(city + " 现在天气状况 " + now.getCond() + ", 气温 " + now.getTmp())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.logo)
                .build();
        if (flag) {
            manager.notify(1, notification);
        }
    }

    public boolean getNotification() {
        SharedPreferences preferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        return preferences.getBoolean(WEATHER_NOTIFICATIONS, true);
    }

    public int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    public void weatherQuery() {
        Toast.makeText(getActivity(), "没有网络连接", Toast.LENGTH_SHORT).show();
        List<Weather> list = new ArrayList<>();
        weatherLab = new WeatherLab(getActivity(), "Weathers.db", null, 1);
        SQLiteDatabase db = weatherLab.getWritableDatabase();
        Cursor cursor = db.query("weather", null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Weather weather = new Weather();
            weather.setwImageId(cursor.getInt(cursor.getColumnIndex("wImageId")));
            weather.setwName(cursor.getString(cursor.getColumnIndex("wName")));
            weather.setDateName(cursor.getString(cursor.getColumnIndex("dateName")));
            weather.sethDegree(cursor.getString(cursor.getColumnIndex("hDegree")));
            weather.setlDegree(cursor.getString(cursor.getColumnIndex("lDegree")));
            weather.setHumidity(cursor.getString(cursor.getColumnIndex("humidity")));
            weather.setPressure(cursor.getString(cursor.getColumnIndex("pressure")));
            weather.setWind(cursor.getString(cursor.getColumnIndex("wind")));
            list.add(weather);
            cursor.moveToNext();
        }
        cursor.close();
        String date = changeYMDtoEn(new Date());
        int i;
        for (i = 0; i < list.size(); i++) {
            if (date.equals(list.get(i).getDateName())) {
                todayWeather = list.get(i);
                todayWeather.setWeekday("Today");

                TextView master_weekday = (TextView) getView().findViewById(R.id.master_weekday);
                master_weekday.setText(todayWeather.getWeekday() + ", ");
                TextView master_date = (TextView) getView().findViewById(R.id.master_date);
                master_date.setText(todayWeather.getDateName());
                TextView master_high_degree = (TextView) getView().findViewById(R.id.master_high_degree);
                master_high_degree.setText(todayWeather.gethDegree());
                TextView master_low_degree = (TextView) getView().findViewById(R.id.master_low_degree);
                master_low_degree.setText(todayWeather.getlDegree());
                TextView master_weather = (TextView) getView().findViewById(R.id.master_weather_name);
                master_weather.setText(todayWeather.getwName());
                ImageView weather_image = (ImageView) getView().findViewById(R.id.master_weather_image);
                ApplicationInfo appInfo = getActivity().getApplicationInfo();
                int resID = getResources().getIdentifier("w" + todayWeather.getwImageId(), "drawable", appInfo.packageName);
                weather_image.setImageDrawable(getResources().getDrawable(resID));

                i++;
                break;
            }
        }

        weatherList = list.subList(i, list.size());
        weatherList.get(0).setWeekday("Tomorrow");
        setupAdapter();
    }

    public String changeYMDtoEn(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.UK);      // 月日年
        return format.format(date);
    }

}
