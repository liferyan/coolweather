package com.liferyan.coolweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liferyan.coolweather.model.City;
import com.liferyan.coolweather.model.County;
import com.liferyan.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 16/9/26.
 */

public class CoolWeatherDB {

    private static final String DB_NAME = "CoolWeather.db";

    private static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    // 构造方法私有化
    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            db.execSQL("INSERT INTO Province (province_name,province_code) VALUES (?,?)"
                    , new String[]{province.getProvinceName(), province.getProvinceCode()});
        }
    }

    public List<Province> loadProvinces() {
        List<Province> provinceList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Province", null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return provinceList;
    }

    public void saveCity(City city) {
        if (city != null) {
            db.execSQL("INSERT INTO City (city_name,city_code,province_id) VALUES (?,?,?)"
                    , new String[]{city.getCityName(), city.getCityCode(), String.valueOf(city.getProvinceId())});
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> cityList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM City where province_id = ?"
                , new String[]{String.valueOf(provinceId)});
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cityList;
    }

    public void saveCounty(County county) {
        if (county != null) {
            db.execSQL("INSERT INTO County (county_name,county_code,city_id) VALUES (?,?,?)"
                    , new String[]{county.getCountyName(), county.getCountyCode(), String.valueOf(county.getCityId())});
        }
    }

    public List<County> loadCounties(int cityId) {
        List<County> countyList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM County where city_id = ?"
                , new String[]{String.valueOf(cityId)});
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                countyList.add(county);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return countyList;
    }
}
