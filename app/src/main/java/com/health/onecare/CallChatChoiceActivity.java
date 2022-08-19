package com.health.onecare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.onecare.R;
import com.health.onecare.adapter.CallLogsListAdapter;
import com.health.onecare.model.CallLogsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CallChatChoiceActivity extends AppCompatActivity {

    SharedPrefsData sharedPrefsData;
    String TAG = "CallChatChoiceActivityTag";
    ListView lvCallLogs;
    ArrayList<CallLogsModel> callLogsModelList;
    CallLogsListAdapter callLogsAdapter;
    String adminNumber = "+18886817776";
    Button btnCallLogs, btnDialAdmin;
    //    small h for 12 hours and capital for 24 hours format
    SimpleDateFormat outputDateTime = new SimpleDateFormat("dd-MMM, hh:mm a");
    String formattedDateTime;
    String formattedDuration;
    TextView tvNoCallLogs;
    Boolean isUserRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_chat_choice);

        btnDialAdmin = findViewById(R.id.buttonDialAdmin);
//        btnCallLogs = findViewById(R.id.buttonCallLogs);
        lvCallLogs = findViewById(R.id.listViewCallLogs);
        tvNoCallLogs = findViewById(R.id.textViewNoCallLogs);
        sharedPrefsData = new SharedPrefsData(CallChatChoiceActivity.this);
        sharedPrefsData.getUserRegisterFromPreference();
        isUserRegistered = sharedPrefsData.isUserRegister;

        if (!isUserRegistered) {
            startActivity(new Intent(CallChatChoiceActivity.this, SplashActivity.class));
        }

        /*btnCallLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                } else {
                    if (lvCallLogs.getVisibility() == View.GONE) {
                        lvCallLogs.setVisibility(View.VISIBLE);
                        getCallLogs();
                    } else if (lvCallLogs.getVisibility() == View.VISIBLE) {
                        lvCallLogs.setVisibility(View.GONE);
                    }
                }
            }
        });*/

        btnDialAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lvCallLogs.getVisibility() == View.VISIBLE) {
                    lvCallLogs.setVisibility(View.GONE);
                    goToDialPad();
                } else {
                    goToDialPad();
                }
            }
        });

    }

    public void goToChatActivity(View view) {
        startActivity(new Intent(CallChatChoiceActivity.this, ChatActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_logout:
                callLogout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void callLogout() {
        sharedPrefsData = new SharedPrefsData(CallChatChoiceActivity.this);
        sharedPrefsData.setValueInPreference("");
        sharedPrefsData.setUserID("", "");
        sharedPrefsData.setMobileNCodeInPreference("", "");
        sharedPrefsData.setUserRegisterInPreference(false);
        startActivity(new Intent(CallChatChoiceActivity.this, SplashActivity.class));
        finish();
    }

    public void goToDialPad() {
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                "+1-888-681-7776", null));
        startActivity(phoneIntent);
    }

    void getCallLogs() {

        /*callLogsModelList = new ArrayList<>();

        String numberCol = CallLog.Calls.NUMBER;
        String durationCol = CallLog.Calls.DURATION;
        String typeCol = CallLog.Calls.TYPE;// 1 - Incoming, 2 - Outgoing, 3 - Missed
        String dateTime = CallLog.Calls.DATE;

        String dateSortOrder = dateTime + " DESC";
        String[] projection = {numberCol, durationCol, typeCol, dateTime};

        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection, null, null, dateSortOrder);

        int numberColIdx = cursor.getColumnIndex(numberCol);
        int durationColIdx = cursor.getColumnIndex(durationCol);
        int typeColIdx = cursor.getColumnIndex(typeCol);
        int dateIndex = cursor.getColumnIndex(dateTime);

        Log.i(TAG, "numberIndex: " + numberColIdx);
        Log.i(TAG, "typeIndex: " + typeColIdx);
        Log.i(TAG, "dateIndex: " + dateIndex);

        while (cursor.moveToNext()) {
            String number = cursor.getString(numberColIdx);
            int duration = cursor.getInt(durationColIdx);
            String type = cursor.getString(typeColIdx);
            long timeDate = cursor.getLong(dateIndex);

            Log.i(TAG, "num: " + number);
            Log.i(TAG, "duration: " + duration);
            Log.i(TAG, "type: " + type);
            Log.i(TAG, "date: " + timeDate);

            formattedDateTime = outputDateTime.format(new Date(timeDate));
            formattedDuration = callDuration(duration);

            if (number.equals(adminNumber)) {
                switch (type) {
                    case "1":
                        callLogsModelList.add(new CallLogsModel(formattedDateTime, number, formattedDuration, R.drawable.ic_baseline_phone_incoming_24));
                        break;
                    case "2":
                        callLogsModelList.add(new CallLogsModel(formattedDateTime, number, formattedDuration, R.drawable.ic_outgoing));
                        break;
                    case "3":
                        callLogsModelList.add(new CallLogsModel(formattedDateTime, number, formattedDuration, R.drawable.ic_baseline_phone_missed_24));
                        break;
                }
            }
        }

        cursor.close();

        callLogsAdapter = new CallLogsListAdapter(CallChatChoiceActivity.this, callLogsModelList);
        lvCallLogs.setAdapter(callLogsAdapter);
        callLogsAdapter.notifyDataSetChanged();*/

    }

    @Override
    public void onBackPressed() { }

    public String callDuration(int duration) {
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;

        if (hours <= 0 && minutes <= 0 && seconds <= 0) {
            return seconds + "s";
        } else if (hours <= 0 && minutes <= 0) {
            return seconds + "s";
        } else if (hours <= 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return hours + "h " + minutes + "m " + seconds + "s";
        }

    }

   /* private Boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(CallChatChoiceActivity.this, Manifest.permission.WRITE_CALL_LOG);
        int result1 = ContextCompat.checkSelfPermission(CallChatChoiceActivity.this, Manifest.permission.READ_CALL_LOG);
        return result1 == PackageManager.PERMISSION_GRANTED && result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1);
//        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG}, 1);
    }*/
}