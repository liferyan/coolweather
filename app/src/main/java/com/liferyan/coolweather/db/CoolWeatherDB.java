package com.liferyan.coolweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liferyan.coolweather.model.City;
import com.liferyan.coolweather.model.Country;
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
            db.execSQL("INSERT INTO province (province_name,province_code) VALUES (?,?)"
                    , new String[]{province.getProvinceName(), province.getProvinceCode()});
        }
    }

    public List<Province> loadProvinces() {
        List<Province> provinceList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM province", null);
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
            db.execSQL("INSERT INTO city (city_name,city_code,province_id) VALUES (?,?,?)"
                    , new String[]{city.getCityName(), city.getCityCode(), city.getProvinceId() + ""});
        }
    }

    public List<City> loadCities() {
        List<City> cityList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FORM city", null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cityList;
    }

    public void saveCountry(Country country) {
        if (country != null) {
            db.execSQL("INSERT INTO country (country_name,country_code,city_id) VALUES (?,?,?)"
                    , new String[]{country.getCountryName(), country.getCountryCode(), country.getCityId() + ""});
        }
    }

    public List<Country> loadCountries() {
        List<Country> countryList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM country", null);
        if (cursor.moveToFirst()) {
            do {
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
                country.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                countryList.add(country);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return countryList;
    }
}
