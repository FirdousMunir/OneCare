package com.health.onecare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class EmailMainActivity extends AppCompatActivity {

    String TAG = "EmailMainActivityTag";
    EditText editTextEmail, etVerificationCode;
    Button buttonContinue, buttonVerify;
    //    CountryCodePicker countryCodePicker;
    String emailAddress;
    String userVerificationCode;
    LinearLayout layoutEmail, layoutContinue;
    LinearLayout layoutVerifyCode, layoutVerifyButton;
    RequestParams params;
    SharedPrefsData sharedPrefsData;
    String currentDeviceToken;
    FusedLocationProviderClient fusedLocationClient;
    double latitude = 0.00000, longitude = 0.00000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_main);

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

                emailAddress = editTextEmail.getText().toString().trim();
                if (emailAddress.isEmpty()) {
                    editTextEmail.setError("Enter Email");
                } else if (emailValidator(emailAddress)) {
                    sharedPrefsData.setEmailInPrefs(emailAddress);
                    sendOTPToEmail();
                } else {
                    Toast.makeText(EmailMainActivity.this, "Enter Valid Email.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonContinue = findViewById(R.id.buttonContinueEmail);

        layoutEmail = findViewById(R.id.linearLayoutEmail);
        layoutContinue = findViewById(R.id.linearLayoutContinueEmail);

        layoutVerifyCode = findViewById(R.id.linearLayoutEmailVerification);
        layoutVerifyButton = findViewById(R.id.linearLayoutEmailVerificationDone);

        etVerificationCode = findViewById(R.id.editTextEmailVerificationCode);
        buttonVerify = findViewById(R.id.buttonDoneVerificationEmail);

        sharedPrefsData = new SharedPrefsData(EmailMainActivity.this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(EmailMainActivity.this);

    }


    public void sendOTPToEmail() {

        params = new RequestParams();
        params.put("email", emailAddress);

        Log.i(TAG, "email: " + emailAddress);

        BaseApi.post(EmailMainActivity.this, "send-verification-email", params, new AsyncHttpResponseHandler() {
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
                        sharedPrefsData.setEmailInPrefs(emailAddress);
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
                        Toast.makeText(EmailMainActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                    }
                    try {
                        JSONObject mainObject = new JSONObject(response).getJSONObject("error");
                        String mainString = mainObject.toString();
                        Toast.makeText(EmailMainActivity.this, mainString, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.i(TAG, "SendFailTryExp: " + e.getMessage());
                    }

                } catch (Exception e) {
                    Log.i(TAG, "mainSendFailTryExp: " + e.getMessage());
//                    yahn url ka check fail b hu ksta h us ko dekhna h
                }
            }
        });

    }

    void verifyOtp() {

        params = new RequestParams();
        params.put("verification_code", userVerificationCode);
        params.put("email", emailAddress);
        params.put("lat", latitude + "");
        params.put("lng", longitude + "");
        params.put("device_token", currentDeviceToken);

        Log.i(TAG, "verifyUserCode: " + userVerificationCode);
        Log.i(TAG, "userEmail: " + emailAddress);
        Log.i(TAG, "latitude: " + latitude + "");
        Log.i(TAG, "longitude: " + longitude + "");
        Log.i(TAG, "currentDeviceToken: " + currentDeviceToken);

       /* verification_code:205198
        email:teamTwo@gmail.com
//phone_number:3155332617
//country_code:1
        lat:22.3444
        lng:74.2343
        device_token:Test*/

        BaseApi.post(EmailMainActivity.this, "verify-through-email", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    Log.i(TAG, "onSuccessVerify: " + response);
                    JSONObject mainObject = new JSONObject(response);

                    if(mainObject.has("message")){
                        String messssg= mainObject.getString("message");
                        Toast.makeText(EmailMainActivity.this, messssg, Toast.LENGTH_SHORT).show();
                    }
                    if (mainObject.has("user")) {
                        String userName = mainObject.getJSONObject("user").getString("name");
                        if (userName.equals("null")) {
                            goToSignUpActivity();
                        } else {
                            JSONObject userObject = mainObject.getJSONObject("user");
                            String idd = userObject.getString("id");
                            String name = userObject.getString("name");
                            String token = mainObject.getString("token");
//                            String email = userObject.getString("email");
                            sharedPrefsData.setUserID(idd, name);
                            sharedPrefsData.setValueInPreference(token);
                            sharedPrefsData.setUserRegisterInPreference(true);
                            goToCallChatChoiceActivity();
                        }
                    } else if (mainObject.has("error")) {
                        String errorMessage = mainObject.getString("error");
                        Toast.makeText(EmailMainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.i(TAG, "mainVerifySuccExp: " + e.getMessage());
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
                            Toast.makeText(EmailMainActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "VerifyFail: " + e.getMessage());
                    }

                    /*try {
                        JSONObject mainObject = new JSONObject(response).getJSONObject("error");
                        if (mainObject.has("country_code")) {
                            String errorMsg = mainObject.getJSONArray("country_code").get(0).toString();
                            Toast.makeText(EmailMainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "SendFailTryExp: " + e.getMessage());
                    }*/
                } catch (Exception e) {
                    Log.i(TAG, "mainVerifyFail: " + e.getMessage());
                }
            }
        });

    }

    void openVerificationCodeDesign() {
        if (layoutEmail.getVisibility() == View.VISIBLE && layoutContinue.getVisibility() == View.VISIBLE) {
            layoutEmail.setVisibility(View.GONE);
            layoutContinue.setVisibility(View.GONE);
            layoutVerifyCode.setVisibility(View.VISIBLE);
            layoutVerifyButton.setVisibility(View.VISIBLE);
        }
    }

    public Boolean emailValidator(String mail) {

        if (!mail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            return true;
        } else {
            return false;
        }
    }

    void goToSignUpActivity() {
        finish();
        startActivity(new Intent(EmailMainActivity.this, SignUpActivity.class));
        finish();

    }

    void goToCallChatChoiceActivity() {
        finish();
        startActivity(new Intent(EmailMainActivity.this, CallChatChoiceActivity.class));
        finish();
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