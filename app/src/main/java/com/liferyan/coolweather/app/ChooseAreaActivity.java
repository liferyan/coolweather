package com.liferyan.coolweather.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liferyan.coolweather.R;
import com.liferyan.coolweather.db.CoolWeatherDB;
import com.liferyan.coolweather.model.City;
import com.liferyan.coolweather.model.County;
import com.liferyan.coolweather.model.Province;
import com.liferyan.coolweather.util.Constants;
import com.liferyan.coolweather.util.HttpCallbackListener;
import com.liferyan.coolweather.util.HttpUtil;
import com.liferyan.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 16/9/26.
 */

public class ChooseAreaActivity extends Activity implements Constants {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private ProgressDialog progressDialog;

    private CoolWeatherDB db;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    // 当前选中的省份
    private Province selectedProvince;
    // 当前选中的城市
    private City selectedCity;
    // 当前选中级别
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryWeather(countyList.get(position).getCountyCode());
                }
            }
        });

        // 进入主界面后,查询并显示所有省份
        db = CoolWeatherDB.getInstance(this);
        queryProvinces();
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY)
            queryCities();
        else if (currentLevel == LEVEL_CITY)
            queryProvinces();
        else
            super.onBackPressed();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    // 查询全国所有的省,优先从数据库中查询,如果没有则到服务器上查询
    private void queryProvinces() {
        provinceList = db.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList)
                dataList.add(province.getProvinceName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(R.string.china);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, PROVINCE);
        }
    }

    // 查询选择省的所有的市,优先从数据库中查询,如果没有则到服务器上查询
    private void queryCities() {
        cityList = db.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList)
                dataList.add(city.getCityName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), CITY);
        }
    }

    // 查询选择市的所有的县,优先从数据库中查询,如果没有则到服务器上查询
    private void queryCounties() {
        countyList = db.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList)
                dataList.add(county.getCountyName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), COUNTY);
        }
    }

    private void queryWeather(String countyCode) {
        queryFromServer(countyCode, WEATHER);
    }

    private void queryFromServer(String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://liferyan.com/weather/city" + code + ".xml";
        } else {
            address = "http://liferyan.com/weather/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinished(final String response) {
                boolean result = false;
                if (PROVINCE.equals(type)) {
                    result = Utility.handleProvinceResponse(db, response);
                } else if (CITY.equals(type)) {
                    result = Utility.handleCityResponse(db, response, selectedProvince.getId());
                } else if (COUNTY.equals(type)) {
                    result = Utility.handleCountyResponse(db, response, selectedCity.getId());
                } else if (WEATHER.equals(type)) {
                    result = true;
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (PROVINCE.equals(type))
                                queryProvinces();
                            else if (CITY.equals(type))
                                queryCities();
                            else if (COUNTY.equals(type))
                                queryCounties();
                            else if (WEATHER.equals(type)) {
                                String[] array = response.split("\\|");
                                if (array.length == 2) {
                                    String weatherCode = array[1];
                                    Intent intent = new Intent();
                                    intent.putExtra(WEATHER_CODE, weatherCode);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
