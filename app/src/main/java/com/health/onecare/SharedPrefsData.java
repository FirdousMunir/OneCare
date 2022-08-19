package com.health.onecare;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.onecare.R;

public class SharedPrefsData {


    String tokenPreferenceTag = "LoginToken";
    String userMobileNoTag = "userMobileNumber";
    String userCountryCodeTag = "userCountryCode";
    String userIDTag = "userID";
    String userNameTag = "userName";
    String userChoiceTag = "userChoice";
    String userEmailTag = "userEmail";
    String isUserRegisteredTag= "isUserRegisteredTag";

    public String loginUserToken;
    String countryCode;
    String mobileNumber;
    Context context;
    String userID;
    String userName;
    String userChoice;
    String userEmail;
    Boolean isUserRegister;

    public SharedPrefsData(Context context) {
        this.context = context;
    }

    public void setValueInPreference(String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE).edit();
        editor.putString(tokenPreferenceTag, token);
        editor.apply();
    }

    public void getTokenFromPreference() {
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        loginUserToken = prefs.getString(tokenPreferenceTag, "");
    }

    public void setMobileNCodeInPreference(String countryCode, String mobileNum) {
        SharedPreferences.Editor editor = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE).edit();
        editor.putString(userMobileNoTag, mobileNum);
        editor.putString(userCountryCodeTag, countryCode);
        editor.apply();
    }

    public void getMobileNCodeFromPreference() {
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        loginUserToken = prefs.getString(tokenPreferenceTag, "");
        countryCode = prefs.getString(userCountryCodeTag,"");
        mobileNumber = prefs.getString(userMobileNoTag, "");
    }

    public void setEmailInPrefs(String email){
        SharedPreferences.Editor editor = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE).edit();
        editor.putString(userEmailTag, email);
        editor.apply();
    }

    public void getEmailInPrefs(){
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        userEmail = prefs.getString(userEmailTag, "");
    }

    public void setUserID(String id, String name){
        SharedPreferences.Editor editor = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE).edit();
        editor.putString(userIDTag, id);
        editor.putString(userNameTag, name);
        editor.apply();
    }

    public void getUSerID(){
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        userID = prefs.getString(userIDTag, "");
        userName = prefs.getString(userNameTag,"");
    }

    public void setUserChoice(String choice){
        SharedPreferences.Editor editor = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE).edit();
        editor.putString(userChoiceTag, choice);
        editor.apply();
    }

    public void getUSerChoice(){
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        userChoice = prefs.getString(userChoiceTag, "");

    }

    public void setUserRegisterInPreference( Boolean userRegister) {
        SharedPreferences.Editor editor = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE).edit();
        editor.putBoolean(isUserRegisteredTag, userRegister);
        editor.apply();
    }

    public void getUserRegisterFromPreference() {
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        isUserRegister = prefs.getBoolean(isUserRegisteredTag,false);
    }

}
