package com.liferyan.coolweather.util;

import android.text.TextUtils;

import com.liferyan.coolweather.db.CoolWeatherDB;
import com.liferyan.coolweather.model.City;
import com.liferyan.coolweather.model.County;
import com.liferyan.coolweather.model.Province;

/**
 * Created by Ryan on 16/9/26.
 */

public class Utility {

    // 解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(CoolWeatherDB db, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    db.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    // 解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(CoolWeatherDB db, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    db.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    // 解析和处理服务器返回的县级数据
    public static boolean handleCountryResponse(CoolWeatherDB db, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCountries = response.split(",");
            if (allCountries != null && allCountries.length > 0) {
                for (String c : allCountries) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    db.saveCountry(county);
                }
                return true;
            }
        }
        return false;
    }
}
