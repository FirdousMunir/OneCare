package com.health.onecare;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.entity.StringEntity;

public class BaseApi {

    static String TAG = "BaseClassTag";
    public static String BASE_URL = "http://chat.onecarebhs.com/api/";
//    public static String BASE_URL = "https://chat.integrated-itsolutions.com/api/";
    private static SharedPrefsData sharedPrefsData;
    private static AsyncHttpClient client = new AsyncHttpClient();


    public BaseApi() { }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(getLoginURL(url), params, responseHandler);
    }

    public static void post(Context context , String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.addHeader("Accept", "application/json");
        client.post(context,getLoginURL(url), params, responseHandler);
    }

    public static void getWithHeader(Context context, String url, AsyncHttpResponseHandler responseHandler){
        sharedPrefsData = new SharedPrefsData(context);
        sharedPrefsData.getTokenFromPreference();
        Log.i(TAG, "getToken: "+ sharedPrefsData.loginUserToken);
        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", "Bearer " + sharedPrefsData.loginUserToken);
        client.get(context,getLoginURL(url),responseHandler);
    }

    public static void postWithHeaderWParams(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        sharedPrefsData = new SharedPrefsData(context);
        sharedPrefsData.getTokenFromPreference();
        Log.i(TAG, "postToken: "+ sharedPrefsData.loginUserToken);
        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", "Bearer " + sharedPrefsData.loginUserToken);
        client.post(context,getLoginURL(url),params,responseHandler);
    }
    public static void postWithHeaderNoParams(Context context, String url, AsyncHttpResponseHandler responseHandler){
        sharedPrefsData = new SharedPrefsData(context);
        sharedPrefsData.getTokenFromPreference();
        Log.i(TAG, "postToken: "+ sharedPrefsData.loginUserToken);
        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", "Bearer " + sharedPrefsData.loginUserToken);
        client.post(getLoginURL(url),responseHandler);
    }

    private static String getLoginURL(String relativeURL){
        return BASE_URL+relativeURL;
    }

}
