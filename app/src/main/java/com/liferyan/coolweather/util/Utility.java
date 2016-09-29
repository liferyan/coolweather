package com.liferyan.coolweather.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.liferyan.coolweather.db.CoolWeatherDB;
import com.liferyan.coolweather.model.Basic;
import com.liferyan.coolweather.model.City;
import com.liferyan.coolweather.model.County;
import com.liferyan.coolweather.model.DailyForecast;
import com.liferyan.coolweather.model.Now;
import com.liferyan.coolweather.model.Province;
import com.liferyan.coolweather.model.Suggestion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    public static boolean handleCountyResponse(CoolWeatherDB db, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    db.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    // 解析服务器返回的JSON数据,并将解析出的数据存储到本地
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject data = jsonArray.getJSONObject(0);

            //Basic
            Basic basic = new Basic();
            basic.setCity(data.getJSONObject("basic").getString("city"));
            basic.setTime(data.getJSONObject("basic").getJSONObject("update").getString("loc"));

            //Now
            Now now = new Now();
            now.setTmp(data.getJSONObject("now").getString("tmp"));
            now.setCond(data.getJSONObject("now").getJSONObject("cond").getString("txt"));
            now.setDir(data.getJSONObject("now").getJSONObject("wind").getString("dir"));
            now.setSc(data.getJSONObject("now").getJSONObject("wind").getString("sc"));

            //DailyForecast
            JSONArray daily_forecast = data.getJSONArray("daily_forecast");
            List<DailyForecast> dailyList = new ArrayList<>();
            for (int i = 0; i < daily_forecast.length(); i++) {
                DailyForecast daily = new DailyForecast();
                JSONObject dailyJSONObject = daily_forecast.getJSONObject(i);
                daily.setDate(dailyJSONObject.getString("date"));
                String weekDay = getWeek(dailyJSONObject.getString("date"));
                daily.setWeekDay(weekDay);
                daily.setMax(dailyJSONObject.getJSONObject("tmp").getString("max"));
                daily.setMin(dailyJSONObject.getJSONObject("tmp").getString("min"));
                daily.setCond(dailyJSONObject.getJSONObject("cond").getString("txt_d"));
                dailyList.add(daily);
            }

            //Suggestion
            Suggestion suggestion = new Suggestion();
            suggestion.setComf(data.getJSONObject("suggestion").getJSONObject("comf").getString("brf"));
            suggestion.setDrsg(data.getJSONObject("suggestion").getJSONObject("drsg").getString("brf"));
            suggestion.setFlu(data.getJSONObject("suggestion").getJSONObject("flu").getString("brf"));
            suggestion.setSport(data.getJSONObject("suggestion").getJSONObject("sport").getString("brf"));
            suggestion.setTrav(data.getJSONObject("suggestion").getJSONObject("trav").getString("brf"));
            suggestion.setUv(data.getJSONObject("suggestion").getJSONObject("uv").getString("brf"));

            saveWeatherInfo(context,basic,now,dailyList,suggestion);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将天气信息保存到本地 SharedPreferences
    private static void saveWeatherInfo(Context context, Basic basic, Now now, List<DailyForecast> dailyList, Suggestion suggestion) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("time", basic.getTime());
        editor.putString("city", basic.getCity());
        editor.putString("tmp", now.getTmp());
        editor.putString("cond", now.getCond());
        editor.putString("wind", now.getWind());
        for (int i = 0; i < dailyList.size(); i++) {
            editor.putString("futures" + (i + 1), dailyList.get(i).toString());
        }
        editor.putString("comf", suggestion.getComf());
        editor.putString("drsg", suggestion.getDrsg());
        editor.putString("flu", suggestion.getFlu());
        editor.putString("sport", suggestion.getSport());
        editor.putString("trav", suggestion.getTrav());
        editor.putString("uv", suggestion.getUv());
        editor.apply();
    }

    private static String getWeek(String dateString) {
        String Week = "周";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "六";
        }
        return Week;
    }
}
