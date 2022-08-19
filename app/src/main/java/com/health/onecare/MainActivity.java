package com.health.onecare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onecare.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivityTag";
    EditText editTextNumber, etVerificationCode;
    Button buttonContinue, buttonVerify;
    //    CountryCodePicker countryCodePicker;
    String countryCode = "1";
    String mobileNumber;
    String userVerificationCode;
    LinearLayout layoutMobileNumber, layoutContinue;
    LinearLayout layoutVerifyCode, layoutVerifyButton;
    RequestParams params;
    SharedPrefsData sharedPrefsData;
    String currentDeviceToken;
    FusedLocationProviderClient fusedLocationClient;
    double latitude = 0.00000, longitude = 0.00000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        initViews();
        getFCMToken();
        getLatLong();

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userVerificationCode = etVerificationCode.getText().toString();
                if(userVerificationCode.isEmpty()){
                    etVerificationCode.setError("Verification Code is Required");
                }else {
                    verifyOtp();
                }
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileNumber = editTextNumber.getText().toString();
                if (mobileNumber.isEmpty()) {
                    editTextNumber.setError("Enter Mobile Number");
                } else {
                    sendOTPToNumber();
                }

            }
        });

    }

    void initViews() {
        editTextNumber = findViewById(R.id.editTextMobileNumber);
        buttonContinue = findViewById(R.id.buttonContinue);

        layoutMobileNumber = findViewById(R.id.linearLayoutPhoneNumber);
        layoutContinue = findViewById(R.id.linearLayoutContinue);

        layoutVerifyCode = findViewById(R.id.linearLayoutVerification);
        layoutVerifyButton = findViewById(R.id.linearLayoutVerificationDone);

        etVerificationCode = findViewById(R.id.editTextVerificationCode);
        buttonVerify = findViewById(R.id.buttonDoneVerification);

        sharedPrefsData = new SharedPrefsData(MainActivity.this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

    }

    void goToSignUpActivity() {
        finish();
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        finish();

    }

    void goToCallChatChoiceActivity() {
        finish();
        startActivity(new Intent(MainActivity.this, CallChatChoiceActivity.class));
        finish();
    }


    public void sendOTPToNumber() {

        params = new RequestParams();
        params.put("country_code", countryCode);
        params.put("phone_number", mobileNumber);

        Log.i(TAG, "country_code: " + countryCode);
        Log.i(TAG, "phone_number: " + mobileNumber);
//        sendOTP
        BaseApi.post(MainActivity.this, "sendOTP", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    Log.i(TAG, "onSuccess: " + response);
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.has("message")) {
                        String msg = mainObject.getString("message");
                        Log.i(TAG, "success: " + msg);
                        openVerificationCodeDesign();
                        sharedPrefsData.setMobileNCodeInPreference(countryCode, mobileNumber);
                    }

                } catch (Exception e) {
                    Log.i(TAG, "mainSendSuccTryExp: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    String message;
                    Log.i(TAG, "onFailure: " + response);
                    JSONObject failureObject = new JSONObject(response);
                    if (failureObject.has("message")) {
                        String msggg = failureObject.getString("message");
                        Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                    }
                    try {
                        JSONObject mainObject = new JSONObject(response).getJSONObject("error");
                        if (mainObject.has("phone_number")) {
                            message = mainObject.getJSONArray("phone_number").get(0).toString();
                            editTextNumber.setError(message);
                        } else if (mainObject.has("country_code")) {
                            message = mainObject.getJSONArray("country_code").get(0).toString();
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "SendFailTryExp: " + e.getMessage());
                    }

                } catch (Exception e) {
                    Log.i(TAG, "mainSendFailTryExp: " + e.getMessage());
                }
            }
        });

    }

    void openVerificationCodeDesign() {
        if (layoutMobileNumber.getVisibility() == View.VISIBLE && layoutContinue.getVisibility() == View.VISIBLE) {
            layoutMobileNumber.setVisibility(View.GONE);
            layoutContinue.setVisibility(View.GONE);
            layoutVerifyCode.setVisibility(View.VISIBLE);
            layoutVerifyButton.setVisibility(View.VISIBLE);
        }
    }

    void verifyOtp() {

        params = new RequestParams();
        params.put("verification_code", userVerificationCode);
        params.put("phone_number", mobileNumber);
        params.put("country_code", countryCode);
        params.put("lat", latitude + "");
        params.put("lng", longitude + "");
        params.put("device_token", currentDeviceToken);

        Log.i(TAG, "verifyUserCode: " + userVerificationCode);
        Log.i(TAG, "verifyPhone: " + mobileNumber);
        Log.i(TAG, "verifyCountryCode: " + countryCode);
        Log.i(TAG, "latitude: " + latitude + "");
        Log.i(TAG, "longitude: " + longitude + "");
        Log.i(TAG, "currentDeviceToken: " + currentDeviceToken);

        BaseApi.post(MainActivity.this, "verify-phone-number", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    Log.i(TAG, "onSuccessVerify: " + response);
                    JSONObject mainObject = new JSONObject(response);

                    if (mainObject.has("user")) {
                        String userName = mainObject.getJSONObject("user").getString("name").toString();
                        if (userName.equals("null")) {
                            goToSignUpActivity();
                        } else {
                            JSONObject userObject = mainObject.getJSONObject("user");
                            String idd = userObject.getString("id");
                            String name = userObject.getString("name");
                            String token = mainObject.getString("token");
                            sharedPrefsData.setUserID(idd, name);
                            sharedPrefsData.setValueInPreference(token);
                            sharedPrefsData.setUserRegisterInPreference(true);
                            goToCallChatChoiceActivity();
                        }
                    } else if (mainObject.has("error")) {
                        String errorMessage = mainObject.getString("error");
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.i(TAG, "mainVerifySucc: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    Log.i(TAG, "onFailureVerify: " + response);
                    try {
                        JSONObject failureObject = new JSONObject(response);
                        if (failureObject.has("message")) {
                            String msggg = failureObject.getString("message");
//                            Toast.makeText(MainActivity.this, msggg, Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "VerifyFail: " + e.getMessage());
                    }

                    try {
                        JSONObject mainObject = new JSONObject(response).getJSONObject("error");
                        if (mainObject.has("country_code")) {
                            String errorMsg = mainObject.getJSONArray("country_code").get(0).toString();
                            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "SendFailTryExp: " + e.getMessage());
                    }
                } catch (Exception e) {
                    Log.i(TAG, "mainVerifyFail: " + e.getMessage());
                }
            }
        });

    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    currentDeviceToken = task.getResult();
                    Log.d(TAG, "FCMToken: " + currentDeviceToken);
                });
    }

    @SuppressLint("MissingPermission")
    void getLatLong() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.i(TAG, "Location: " + location.getLatitude() + " : " + location.getLongitude());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });
    }

}