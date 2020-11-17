package com.example.app;

import java.util.ArrayList;
import java.util.Comparator;

import com.example.app.iBeaconClass.iBeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LeDeviceListAdapter extends BaseAdapter {

    // Adapter for holding devices found through scanning.

    private ArrayList<iBeacon> mLeDevices;
    private LayoutInflater mInflator;
    private Activity mContext;

    public LeDeviceListAdapter(Activity c) {
        super();
        mContext = c;
        mLeDevices = new ArrayList<iBeacon>();
        mInflator = mContext.getLayoutInflater();
    }

    public void addDevice(iBeacon device) {
        if(device==null)
            return;
        if(device.rssi>=-80) {//强度墙
            for (int i = 0; i < mLeDevices.size(); i++) {
                String btAddress = mLeDevices.get(i).bluetoothAddress;
                if (btAddress.equals(device.bluetoothAddress)) {

                    mLeDevices.add(i + 1, device);
                    mLeDevices.remove(i);
                    return;
                }
            }
            mLeDevices.add(device);
        }

    }

    public iBeacon getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public ArrayList<iBeacon> getlist(){
        return mLeDevices;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceUUID= (TextView)view.findViewById(R.id.device_beacon_uuid);
            viewHolder.deviceMajor_Minor=(TextView)view.findViewById(R.id.device_major_minor);
            viewHolder.devicetxPower_RSSI=(TextView)view.findViewById(R.id.device_txPower_rssi);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        iBeacon device = mLeDevices.get(i);
        final String deviceName = device.name;
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);

        viewHolder.deviceAddress.setText(device.bluetoothAddress);
        viewHolder.deviceUUID.setText(device.proximityUuid);
        viewHolder.deviceMajor_Minor.setText("major:"+device.major+",minor:"+device.minor);
        viewHolder.devicetxPower_RSSI.setText("txPower:"+device.txPower+",rssi:"+device.rssi);
        return view;
    }

    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceUUID;
        TextView deviceMajor_Minor;
        TextView devicetxPower_RSSI;
    }
    static class sortById implements Comparator {

        public int compare(Object o1, Object o2) {
            if(((iBeacon)o1).rssi>((iBeacon)o2).rssi)
                return 1;
            return -1;
        }

    }
}
