package com.health.onecare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Signer;

import cz.msebera.android.httpclient.Header;

public class SignUpActivity extends AppCompatActivity {

    String TAG = "SignUpActivityTag";
    TextInputLayout textLayoutName, textLayoutEmail, textLayoutCode, textLayoutNumber;
    TextInputLayout textLayoutPassword, textLayoutConPassword;
    EditText etEmail, etPassword, etConPassword, etName;
    EditText etCountryCode, etPhoneNumber;
    Button buttonRegister;
    LinearLayout layoutSignUp, layoutRegister;
    String userEmail, userPassword, userConPassword, userName, userNumber;
    String userCCode="1";
    RequestParams params;
    SharedPrefsData sharedPrefsData;
    String savedCode, savedNumber, savedEmail;
    FusedLocationProviderClient fusedLocationClient;
    double latitude, longitude;
    String choice ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        initViews();

        Log.i(TAG, "UserChoice: " + choice);


        if (choice.equals("phone")) {
            if(textLayoutCode.getVisibility() == View.VISIBLE && textLayoutNumber.getVisibility() == View.VISIBLE) {
                textLayoutCode.setVisibility(View.GONE);
                textLayoutNumber.setVisibility(View.GONE);
                textLayoutEmail.setVisibility(View.VISIBLE);
            }
        } else if (choice.equals("email")) {
            if(textLayoutCode.getVisibility() == View.GONE && textLayoutNumber.getVisibility() == View.GONE) {
                textLayoutCode.setVisibility(View.VISIBLE);
                textLayoutNumber.setVisibility(View.VISIBLE);
                textLayoutEmail.setVisibility(View.GONE);
            }
        }

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = etName.getText().toString().trim();
                userEmail = etEmail.getText().toString().trim();
                userPassword = etPassword.getText().toString().trim();
                userConPassword = etConPassword.getText().toString().trim();
//                userCCode = etCountryCode.getText().toString();
                userNumber = etPhoneNumber.getText().toString().trim();

                if (choice.equals("phone")) {
                    phoneDesignVerification(userName, userEmail, userPassword, userConPassword);
                } else if (choice.equals("email")) {
                    emailDesignVerification(userName, userCCode, userNumber, userPassword, userConPassword);
                }
            }
        });

    }

    void initViews() {
        etName = findViewById(R.id.editTextName);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        etConPassword = findViewById(R.id.editTextConfirmPassword);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        etConPassword.setTransformationMethod(new PasswordTransformationMethod());

        etPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        etCountryCode = findViewById(R.id.editTextCountryCode);
        etCountryCode.setInputType(InputType.TYPE_NULL);
        etCountryCode.setFocusable(false);

        buttonRegister = findViewById(R.id.buttonRegister);

        layoutSignUp = findViewById(R.id.linearLayoutSignUp);
        layoutRegister = findViewById(R.id.linearLayoutRegister);

        textLayoutName = findViewById(R.id.textInputName);
        textLayoutEmail = findViewById(R.id.textInputEmail);
        textLayoutCode = findViewById(R.id.textInputCCode);
        textLayoutNumber = findViewById(R.id.textInputPhoneNum);
        textLayoutPassword = findViewById(R.id.textInputPassword);
        textLayoutConPassword = findViewById(R.id.textInputConPassword);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(SignUpActivity.this);

        sharedPrefsData = new SharedPrefsData(SignUpActivity.this);
        sharedPrefsData.getMobileNCodeFromPreference();
        savedCode = sharedPrefsData.countryCode;
        savedNumber = sharedPrefsData.mobileNumber;
        sharedPrefsData.getUSerChoice();
        choice = sharedPrefsData.userChoice;
        sharedPrefsData.getEmailInPrefs();
        savedEmail=sharedPrefsData.userEmail;

    }

    void phoneDesignVerification(String name, String email, String pass, String conPass){
         if (name.isEmpty() && pass.isEmpty() && conPass.isEmpty() && email.isEmpty() ) {
                    etName.setError("Name");
                    etEmail.setError("Email");
                    etPassword.setError("Password");
                    etConPassword.setError("Confirm Password");
                } else if (name.isEmpty()) {
                    etName.setError("Name");
                } else if (pass.isEmpty() && conPass.isEmpty()) {
                    etPassword.setError("password");
                    etConPassword.setError("Confirm Password");
                } else if (pass.length() < 8 && conPass.length() < 8) {
                    displayDialog();
                } else if (emailValidator(email)) {
                    if (pass.equals(conPass)) {
                        callRegisterApiPhone();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Password doesn't match.", Toast.LENGTH_SHORT).show();
                        etPassword.setText("");
                        etConPassword.setText("");
                        etPassword.setFocusable(true);
                    }

                } else {
                    etEmail.setError("Enter Valid Email.");
                }
    }

    void emailDesignVerification(String name, String cCode, String number, String pass, String conPass){
         if (name.isEmpty() && pass.isEmpty() && conPass.isEmpty() && cCode.isEmpty() && number.isEmpty()) {
                    etName.setError("Name");
                    etPassword.setError("Password");
                    etConPassword.setError("Confirm Password");
                    etPhoneNumber.setError("Phone Number");
                } else if (name.isEmpty()) {
                    etName.setError("Name");
                } else if(number.isEmpty()) {
                    etPhoneNumber.setError("Phone Number");
                }else if (pass.isEmpty() && conPass.isEmpty()) {
                    etPassword.setError("password");
                    etConPassword.setError("Confirm Password");
                } else if (pass.length() < 8 && conPass.length() < 8) {
                    displayDialog();
                } else {
                    if (pass.equals(conPass)) {
                        callRegisterApiEmail();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Password doesn't match.", Toast.LENGTH_SHORT).show();
                        etPassword.setText("");
                        etConPassword.setText("");
                        etPassword.setFocusable(true);
                    }

                }
    }

    void callRegisterApiPhone() {
        params = new RequestParams();

        params.put("phone_number", savedNumber);
        params.put("name", userName);
        params.put("email", userEmail);
        params.put("country_code", savedCode);
        params.put("password", userPassword);
        params.put("password_confirmation", userConPassword);

        Log.i(TAG, "phone_number: " + savedNumber);
        Log.i(TAG, "name: " + userName);
        Log.i(TAG, "email: " + userEmail);
        Log.i(TAG, "country_code: " + savedCode);
        Log.i(TAG, "password: " + userPassword);
        Log.i(TAG, "password_confirmation: " + userConPassword);

        BaseApi.post(SignUpActivity.this, "register", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    Log.i(TAG, "onSuccessPhone: " + response);
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.has("user")) {
//                    String respToken = mainObject.getString("access_token");
//                    sharedPrefsData.setValueInPreference(respToken);
                        JSONObject userObject = mainObject.getJSONObject("user");
                        String idd = userObject.getString("id");
                        String name = userObject.getString("name");
                        String token = mainObject.getString("access_token");
                        Log.i(TAG, "onSuccessIDPhone: " + idd);
                        Log.i(TAG, "onSuccessNamePhone: " + name);
                        Log.i(TAG, "onSuccessTokenPhone: " + token);
                        sharedPrefsData.setUserID(idd, name);
                        sharedPrefsData.setValueInPreference(token);
                        sharedPrefsData.setUserRegisterInPreference(true);
                        goToCallChatActivity();
                    } else {
//                        Toast.makeText(SignUpActivity.this, "else called", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onSuccessPhone: " + "else called");
                    }

                } catch (Exception e) {
                    Log.i(TAG, "registerSuccExpPhone: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    Log.i(TAG, "onFailurePhone: " + response);
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.has("error")) {
                        JSONObject objectError = mainObject.getJSONObject("error");
                        if (objectError.has("email")) {
                            String message = objectError.getJSONArray("email").getString(0);
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else if (objectError.has("country_code")) {
                            String messageCountry = objectError.getJSONArray("country_code").getString(0);
                            Toast.makeText(SignUpActivity.this, messageCountry, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    Log.i(TAG, "registerFailExpPhone: " + e.getMessage());
                }
            }
        });


    }

    void callRegisterApiEmail() {
        params = new RequestParams();

        params.put("phone_number", userNumber);
        params.put("name", userName);
        params.put("email", savedEmail);
        params.put("country_code", userCCode);
        params.put("password", userPassword);
        params.put("password_confirmation", userConPassword);

        Log.i(TAG, "phone_number: " + userNumber);
        Log.i(TAG, "name: " + userName);
        Log.i(TAG, "email: " + savedEmail);
        Log.i(TAG, "country_code: " + userCCode);
        Log.i(TAG, "password: " + userPassword);
        Log.i(TAG, "password_confirmation: " + userConPassword);

        BaseApi.post(SignUpActivity.this, "register", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    Log.i(TAG, "onSuccessEmail: " + response);
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.has("user")) {
//                    String respToken = mainObject.getString("access_token");
//                    sharedPrefsData.setValueInPreference(respToken);
                        JSONObject userObject = mainObject.getJSONObject("user");
                        String idd = userObject.getString("id");
                        String name = userObject.getString("name");
                        String token = mainObject.getString("access_token");
                        Log.i(TAG, "EmailonSuccessID: " + idd);
                        Log.i(TAG, "EmailonSuccessName: " + name);
                        Log.i(TAG, "EmailonSuccessToken: " + token);
                        sharedPrefsData.setUserID(idd, name);
                        sharedPrefsData.setValueInPreference(token);
                        sharedPrefsData.setUserRegisterInPreference(true);
                        goToCallChatActivity();
                    } else {
//                        Toast.makeText(SignUpActivity.this, "else called", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onSuccessEmail: " + "else called");
                    }

                } catch (Exception e) {
                    Log.i(TAG, "registerSuccExpEmail: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    String response = new String(responseBody, charset);
                    Log.i(TAG, "onFailureEmail: " + response);
                    JSONObject mainObject = new JSONObject(response);
                    if(mainObject.has("message")){
                        Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                    if (mainObject.has("error")) {
                        JSONObject objectError = mainObject.getJSONObject("error");
                        if (objectError.has("email")) {
                            String message = objectError.getJSONArray("email").getString(0);
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else if (objectError.has("country_code")) {
                            String messageCountry = objectError.getJSONArray("country_code").getString(0);
                            Toast.makeText(SignUpActivity.this, messageCountry, Toast.LENGTH_SHORT).show();
                        }else if (objectError.has("phone_error")){
                            String adminPhoneError = objectError.getJSONArray("phone_error").getString(0);
                            Toast.makeText(SignUpActivity.this, adminPhoneError, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    Log.i(TAG, "registerFailExpEmail: " + e.getMessage());
                }
            }
        });


    }

    public Boolean emailValidator(String mail) {

        if (!mail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            return true;
        } else {
            return false;
        }
    }

    void goToCallChatActivity() {
        finish();
        startActivity(new Intent(SignUpActivity.this, CallChatChoiceActivity.class));
    }

    void displayDialog() {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(SignUpActivity.this);
        builder.setMessage("The password must be at least 8 characters.");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("MissingPermission")
    void getLatLong() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.i("sdsds3", "" + location.getLatitude() + " : " + location.getLongitude());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });
    }

}