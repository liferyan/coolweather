package com.liferyan.coolweather.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.liferyan.coolweather.R;
import com.liferyan.coolweather.util.Constants;
import com.liferyan.coolweather.util.HttpCallbackListener;
import com.liferyan.coolweather.util.HttpUtil;
import com.liferyan.coolweather.util.Utility;

public class WeatherActivity extends AppCompatActivity implements Constants {

    private Handler mHandler = new Handler();

    private TextView time_textview;
    private TextView city_textview;
    private TextView tmp_textview;
    private TextView cond_textview;
    private TextView wind_textview;
    private TextView comf_textview;
    private TextView drsg_textview;
    private TextView flu_textview;
    private TextView sport_textview;
    private TextView trav_textview;
    private TextView uv_textview;
    private TextView future1_textview;
    private TextView future2_textview;
    private TextView future3_textview;
    private TextView future4_textview;
    private TextView future5_textview;
    private TextView future6_textview;
    private TextView future7_textview;

    private ProgressDialog progressDialog;

    private ScrollView content;

    private String weatherCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        time_textview = (TextView) findViewById(R.id.time);
        city_textview = (TextView) findViewById(R.id.city);
        tmp_textview = (TextView) findViewById(R.id.tmp);
        cond_textview = (TextView) findViewById(R.id.cond);
        wind_textview = (TextView) findViewById(R.id.wind);

        comf_textview = (TextView) findViewById(R.id.comf);
        drsg_textview = (TextView) findViewById(R.id.drsg);
        flu_textview = (TextView) findViewById(R.id.flu);
        sport_textview = (TextView) findViewById(R.id.sport);
        trav_textview = (TextView) findViewById(R.id.trav);
        uv_textview = (TextView) findViewById(R.id.uv);

        future1_textview = (TextView) findViewById(R.id.future1);
        future2_textview = (TextView) findViewById(R.id.future2);
        future3_textview = (TextView) findViewById(R.id.future3);
        future4_textview = (TextView) findViewById(R.id.future4);
        future5_textview = (TextView) findViewById(R.id.future5);
        future6_textview = (TextView) findViewById(R.id.future6);
        future7_textview = (TextView) findViewById(R.id.future7);

        content = (ScrollView) findViewById(R.id.content);
        content.setVisibility(View.INVISIBLE);

        // 添加导航栏 包含导航按钮和ActionMenu
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        // 设置导航按钮 添加事件跳转至选择城市Activity
        toolbar.setNavigationIcon(R.drawable.ic_home_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        // 添加ActionMenu 包含刷新按钮
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.refresh:
                        content.setVisibility(View.INVISIBLE);
                        // 点击刷新按钮再次请求网络获得天气数据
                        requestWeatherInfo(weatherCode);
                        break;
                }
                return true;
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        weatherCode = prefs.getString(WEATHER_CODE, "");
        if (TextUtils.isEmpty(weatherCode)) {
            //默认显示上海天气
            weatherCode = WEATHER_CODE_SH;
            requestWeatherInfo(weatherCode);
        } else {
            showWeather();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    content.setVisibility(View.INVISIBLE);
                    weatherCode = data.getStringExtra(WEATHER_CODE);
                    requestWeatherInfo(weatherCode);
                }
                break;
        }
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

    private void requestWeatherInfo(final String cityId) {
        String address = "https://api.heweather.com/x3/weather?cityid=" + cityId + "&key=" + HE_WEATHER_KEY;
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinished(final String response) {
                closeProgressDialog();
                // 将返回的JSON天气信息写入本地
                Utility.handleWeatherResponse(WeatherActivity.this, response);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString(WEATHER_CODE, cityId);
                        editor.apply();
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    // 显示本地的天气信息
    private void showWeather() {
        content.setVisibility(View.VISIBLE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        time_textview.setText(prefs.getString("time", "NA"));
        city_textview.setText(prefs.getString("city", "NA"));
        tmp_textview.setText(prefs.getString("tmp", "NA"));
        cond_textview.setText(prefs.getString("cond", "NA"));
        wind_textview.setText(prefs.getString("wind", "NA"));
        comf_textview.setText(prefs.getString("comf", "NA"));
        drsg_textview.setText(prefs.getString("drsg", "NA"));
        flu_textview.setText(prefs.getString("flu", "NA"));
        sport_textview.setText(prefs.getString("sport", "NA"));
        trav_textview.setText(prefs.getString("trav", "NA"));
        uv_textview.setText(prefs.getString("uv", "NA"));
        future1_textview.setText(prefs.getString("futures1", "NA"));
        future2_textview.setText(prefs.getString("futures2", "NA"));
        future3_textview.setText(prefs.getString("futures3", "NA"));
        future4_textview.setText(prefs.getString("futures4", "NA"));
        future5_textview.setText(prefs.getString("futures5", "NA"));
        future6_textview.setText(prefs.getString("futures6", "NA"));
        future7_textview.setText(prefs.getString("futures7", "NA"));
    }
}
