package com.health.onecare.model;

public class CallLogsModel {

    String DateTime, number, callDuration;
    int callType;

    public CallLogsModel(String dateTime, String number, String callDuration, int callType) {
        DateTime = dateTime;
        this.number = number;
        this.callDuration = callDuration;
        this.callType = callType;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }
}
