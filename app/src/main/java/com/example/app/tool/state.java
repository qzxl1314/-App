package com.example.app.tool;

public class state {
    private String BeaconID;
    private String DeviceID;
    private String StartTime;
    private String EndTime;

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getBeaconID() {
        return BeaconID;
    }

    public void setBeaconID(String beaconID) {
        BeaconID = beaconID;
    }
    @Override
    protected void finalize(){}
}
