package com.example.thweather;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "8a867c4afb6747cc9b5531b599543f85";

    String unit;

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Weather> fetchItems(String location, String unit) {

        List<Weather> weathers = new ArrayList<>();

        this.unit = unit;

        try {
            String url = Uri.parse("https://free-api.heweather.net/s6/weather/forecast?location="
                    + location + "&key=" + API_KEY + "&lang=en").toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(weathers, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (Exception e) {
            Log.e(TAG, "Failed", e);
        }

        return weathers;
    }

    private void parseItems(List<Weather> items, JSONObject jsonBody) throws Exception{
        JSONArray weatherJsonArray = jsonBody.getJSONArray("HeWeather6");
        JSONObject weathersJsonObject = weatherJsonArray.getJSONObject(0);
        JSONArray weatherForecast = weathersJsonObject.getJSONArray("daily_forecast");

        for (int i = 0; i < weatherForecast.length(); i++) {
            JSONObject weatherJsonObject = weatherForecast.getJSONObject(i);

            Weather weather = new Weather();
            weather.setwName(weatherJsonObject.getString("cond_txt_d"));
            weather.setDateName(changeYMDtoEn(weatherJsonObject.getString("date")));
            weather.setHumidity(weatherJsonObject.getString("hum"));
            weather.sethDegree(weatherJsonObject.getString("tmp_max"));
            weather.setlDegree(weatherJsonObject.getString("tmp_min"));
            weather.setPressure(weatherJsonObject.getString("pres"));
            weather.setWind(weatherJsonObject.getString("wind_spd"));
            weather.setWeekday(getWeekday(weatherJsonObject.getString("date")));
            weather.setwImageId(Integer.valueOf(weatherJsonObject.getString("cond_code_d")));

            if (unit.equals("")) {
                unit = "Metric";
            }
            if (unit.equals("British")) {
                weather.setlDegree(String.format("%.1f", Integer.valueOf(weather.getlDegree()) * 1.8 + 32) + " °F");
                weather.sethDegree(String.format("%.1f", Integer.valueOf(weather.gethDegree()) * 1.8 + 32) + " °F");
            } else {
                weather.setlDegree(weather.getlDegree() + "°");
                weather.sethDegree(weather.gethDegree() + "°");
            }

            items.add(weather);
        }
    }

    public String changeYMDtoEn(String dateYMD) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateYMD);
        SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.UK);      // 月日年
        return format.format(date);
    }

    public String getWeekday(String dateYMD) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateYMD);
        calendar.setTime(date);
        int t = calendar.get(Calendar.DAY_OF_WEEK);
        String weekday = "";
        switch (t) {
            case 1: {
                weekday = "Monday";
                break;
            }
            case 2: {
                weekday = "Tuesday";
                break;
            }
            case 3: {
                weekday = "Wednesday";
                break;
            }
            case 4: {
                weekday = "Thursday";
                break;
            }
            case 5: {
                weekday = "Friday";
                break;
            }
            case 6: {
                weekday = "Saturday";
                break;
            }
            case 7: {
                weekday = "Sunday";
                break;
            }
            default:
                break;
        }
        return weekday;
    }

    public WeatherNow fetchNow(String location, String unit) {
        WeatherNow weather = new WeatherNow();
        this.unit = unit;
        try {
            String jsonString;
            String url = Uri.parse("https://free-api.heweather.net/s6/weather/now?location="
                    + location + "&key=" + API_KEY).toString();
            jsonString = getUrlString(url);
            Log.i(TAG, "fetchNow: " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            parseNow(jsonObject, weather);
        } catch (JSONException joe) {
            Log.e(TAG, "fetchNow: ", joe);
        } catch (Exception e) {
            Log.e(TAG, "fetchNow: ", e);
        }
        return weather;
    }

    public void parseNow(JSONObject jsonObject, WeatherNow weather) throws IOException, JSONException {
        JSONArray array = jsonObject.getJSONArray("HeWeather6");
        JSONObject info = array.getJSONObject(0);
        JSONObject object = info.getJSONObject("now");
        weather.setTmp(object.getString("tmp"));
        weather.setCond(object.getString("cond_txt"));
        if (unit.equals("British")) {
            weather.setTmp(String.format("%.1f", Integer.valueOf(weather.getTmp()) * 1.8 + 32) + " °F");
        } else {
            weather.setTmp(weather.getTmp() + "°");
        }
    }
}