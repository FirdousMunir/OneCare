package com.health.onecare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.onecare.R;

public class SplashActivity extends AppCompatActivity {

    CheckBox checkBox;
    TextView textViewCheckMsg;
    String TAG = "SplashActivityTAG";
    SharedPrefsData sharedPrefsData;
    Boolean isUserRegistered=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        if (!checkPermission()) {
            requestPermission();
        }
        initViews();
    }

    void initViews() {
        checkBox = findViewById(R.id.checkboxCheck);
        textViewCheckMsg = findViewById(R.id.textViewCheckBoxMessage);

        sharedPrefsData = new SharedPrefsData(SplashActivity.this);

        sharedPrefsData.getUserRegisterFromPreference();
        isUserRegistered = sharedPrefsData.isUserRegister;

        if(isUserRegistered){
            startActivity(new Intent(SplashActivity.this,CallChatChoiceActivity.class));
            finish();
        }
    }

    public void goToPhoneNumberActivity(View view) {
        if (checkBox.isChecked()) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            sharedPrefsData.setUserChoice("phone");
        } else {
            textViewCheckMsg.setVisibility(View.VISIBLE);
        }
    }

    public void goToEmailActivity(View view){
        if (checkBox.isChecked()) {
            startActivity(new Intent(SplashActivity.this, EmailMainActivity.class));
            sharedPrefsData.setUserChoice("email");
        } else {
            textViewCheckMsg.setVisibility(View.VISIBLE);
        }
    }

    public void onCheckBoxClick(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkboxCheck:
                if (checked) {
                    textViewCheckMsg.setVisibility(View.GONE);
                }
                break;
        }
    }

    private Boolean checkPermission() {
        int location = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
//        int readCallLog = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CALL_LOG);
//        int writeCallLog = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_CALL_LOG);
//        return location == PackageManager.PERMISSION_GRANTED && readCallLog == PackageManager.PERMISSION_GRANTED && writeCallLog == PackageManager.PERMISSION_GRANTED;
        return location == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1);
//        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}