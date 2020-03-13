package com.example.thweather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WeatherLab extends SQLiteOpenHelper {

    public static final String CRETE_WEATHER = "create table weather (" +
            "wImageId int," +
            "wName varchar(20)," +
            "dateName varchar(20)," +
            "weekday varchar(20)," +
            "hDegree varchar(20)," +
            "lDegree varchar(20)," +
            "humidity varchar(20)," +
            "pressure varchar(20)," +
            "wind varchar(20)" +
            ")";

    public WeatherLab(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CRETE_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}