package com.liferyan.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ryan on 16/9/26.
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    // Province 建表语句
    private static final String CREATE_PROVINCE =
            "CREATE TABLE Province ("
                    + "id integer PRIMARY KEY AUTOINCREMENT,"
                    + "province_name text,"
                    + "province_code text)";

    // City 建表语句
    private static final String CREATE_CITY =
            "CREATE TABLE City ("
                    + "id integer PRIMARY KEY AUTOINCREMENT,"
                    + "city_name text,"
                    + "city_code text,"
                    + "province_id integer)";
    // Country 建表语句
    private static final String CREATE_COUNTRY =
            "CREATE TABLE Country ("
                    + "id integer PRIMARY KEY AUTOINCREMENT,"
                    + "country_name text,"
                    + "country_code text,"
                    + "city_id integer)";


    public CoolWeatherOpenHelper(Context context, String name
            , SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
