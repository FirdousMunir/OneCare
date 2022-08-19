package com.health.onecare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onecare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.health.onecare.model.ChatModel;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ChatActivity extends AppCompatActivity {

    String TAG = "ChatActivityTag";
    DatabaseReference databaseReference;
    ArrayList<ChatModel> msgsList;
    ListView listViewMsgs;
    MessagesAdapterList adapterList;
    EditText etMessage;
    Button btnMessage;
    String message;
    SharedPrefsData sharedPrefsData;
    String savedID, savedName;
    RequestParams params;
    String currentDeviceToken;
    ProgressBar progressBar;
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    //    small h for 12 hours and capital for 24 hours format
    SimpleDateFormat output = new SimpleDateFormat("hh:mm");
    SimpleDateFormat dateOutput = new SimpleDateFormat("dd-MMM-yyyy");
    Date d, date = null;
    String formattedTime, formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().hide();
        initViews();
        getAllMessages();
        getFCMToken();

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = FirebaseDatabase.getInstance().getReference("messages");
                message = etMessage.getText().toString();
                if (message.isEmpty()) {
//                    btnMessage.setEnabled(false);
                } else {
                    callChatApi(message);
                    btnMessage.setEnabled(false);
                }
            }
        });

    }

    void initViews() {

        listViewMsgs = findViewById(R.id.msgsListView);
        etMessage = findViewById(R.id.editTextMessage);
        btnMessage = findViewById(R.id.buttonSendMessage);

        progressBar = findViewById(R.id.chatProgressBar);

        sharedPrefsData = new SharedPrefsData(ChatActivity.this);
        sharedPrefsData.getUSerID();
        savedID = sharedPrefsData.userID;
        savedName = sharedPrefsData.userName;
        Log.i(TAG, "savedID: " + savedID);
        Log.i(TAG, "savedName: " + savedName);

    }

    void getAllMessages() {
        msgsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        Query queryFForID = databaseReference.orderByChild("id");
        queryFForID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "chatSnapShot: " + snapshot);

                if (snapshot.getValue() != null) {
                    msgsList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String msg = dataSnapshot.child("message").getValue().toString();
                        String name = dataSnapshot.child("senderName").getValue().toString();
                        String sentAt = dataSnapshot.child("sentAt").getValue().toString();
                        String sentById = dataSnapshot.child("sentById").getValue().toString();
                        String receiveById = dataSnapshot.child("receiveById").getValue().toString();
                        String senderID = dataSnapshot.child("sender_id").getValue().toString();
                        String id = dataSnapshot.child("id").getValue().toString();
                        String dateee = dataSnapshot.child("sentAt").getValue().toString();

                        try {
                            d = input.parse(sentAt);
                            date = input.parse(dateee);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.i(TAG, "TimeDateExp: " + e.getMessage());
                        }
                        formattedTime = output.format(d);
                        formattedDate = dateOutput.format(date);
                        Log.i(TAG, "Time: " + formattedTime);
                        Log.i(TAG, "Date: " + formattedDate);
                        Log.i(TAG, "savedId: " + savedID);
                        Log.i(TAG, "savedName: " + savedName);

//                        && senderID.equals("1") && savedID.equals(receiveById)
//                        savedID.equals(sentById) // current user messages
                        if ( savedID.equals(sentById)  || savedID.equals(receiveById) ) {
                            Log.i(TAG, "id: " + id);
                            Log.i(TAG, "msg: " + msg);
                            Log.i(TAG, "name: " + name);
                            Log.i(TAG, "sentAt: " + sentAt);
                            Log.i(TAG, "receiveById: " + receiveById);
                            Log.i(TAG, "sentById: " + sentById);
                            Log.i(TAG, "savedId: " + savedID);
                            Log.i(TAG, "savedName: " + savedName);
                            msgsList.add(new ChatModel(id, msg, sentById, receiveById, name, formattedTime, formattedDate));
                        }

//                        Log.i(TAG, "id: " + id);
//                        Log.i(TAG, "msg: " + msg);
//                        Log.i(TAG, "name: " + name);
//                        Log.i(TAG, "sentAt: " + sentAt);
//                        Log.i(TAG, "receiveById: " + receiveById);
//                        Log.i(TAG, "sentById: " + sentById);

                    }
                    Log.i(TAG, "ListSize: " + msgsList.size());
                    adapterList = new MessagesAdapterList(ChatActivity.this, msgsList);
                    listViewMsgs.setAdapter(adapterList);
                    adapterList.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    listViewMsgs.setVisibility(View.VISIBLE);

                }
                progressBar.setVisibility(View.GONE);
                listViewMsgs.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "chatCancel" + error.getDetails());
            }
        });
    }

    void callChatApi(String message) {

        params = new RequestParams();

        params.put("sender_id", savedID);
        params.put("receiver_id", "1");
        params.put("message", message);
        params.put("device_token", currentDeviceToken);


        BaseApi.postWithHeaderWParams(ChatActivity.this, "messages/chat", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Charset charset = StandardCharsets.UTF_8;
                String response = new String(responseBody, charset);
                Log.i(TAG, "onSuccess: " + response);
                etMessage.setText("");
                btnMessage.setEnabled(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Charset charset = StandardCharsets.UTF_8;
                String response = new String(responseBody, charset);
                Log.i(TAG, "onFailure: " + response);
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
        sharedPrefsData = new SharedPrefsData(ChatActivity.this);
        sharedPrefsData.setValueInPreference("");
        sharedPrefsData.setUserID("", "");
        sharedPrefsData.setMobileNCodeInPreference("", "");
        startActivity(new Intent(ChatActivity.this, SplashActivity.class));
        finish();
    }

}