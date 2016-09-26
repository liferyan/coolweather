package com.liferyan.coolweather.util;

/**
 * Created by Ryan on 16/9/26.
 */

public interface HttpCallbackListener {

    void onFinished(String response);

    void onError(Exception e);
}
