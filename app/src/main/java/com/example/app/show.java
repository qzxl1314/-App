  package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.tool.MyDatabaseHelper;
import com.example.app.tool.state;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

  public class show extends AppCompatActivity {
      private LeDeviceListAdapter mLeDeviceListAdapter;
      /**
       * 搜索BLE终端
       */
      private int seconds = 0;
      private BluetoothAdapter mBluetoothAdapter;
      private TextView Content;
      private TextView Time;
      private TextView dao;
      private boolean mScanning;
      private Handler mHandler;
      private String nowadress = "";
      private Timer timer;
      private TimerTask timerTask;
      private MyDatabaseHelper dbHelper;
      private state now;
      ImageView start;
      String id;
      // Stops scanning after 10 seconds.
      private static final long SCAN_PERIOD = 14400000;
      private boolean run = false;

      //    private final Handler handler = new Handler();
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_show);
          Intent intent = getIntent();
          id = intent.getStringExtra("id");

          init();
          run = true;
//        handler.postDelayed(task, 1000);
          mHandler = new Handler();
          // Use this check to determine whether BLE is supported on the device.  Then you can
          // selectively disable BLE-related features.
          dbHelper = new MyDatabaseHelper(this, "beacon.db", null, 1);
          if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
              Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
              finish();
          }
          final BluetoothManager bluetoothManager =
                  (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
          mBluetoothAdapter = bluetoothManager.getAdapter();
          // Checks if Bluetooth is supported on the device.
          if (mBluetoothAdapter == null) {
              Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
              finish();
              return;
          }
          //开启蓝牙
          mBluetoothAdapter.enable();
          start.setOnClickListener(this::onClick);
      }

      private void init() {
          countDownTimer.start();
          Content = findViewById(R.id.content);
          Time = findViewById(R.id.time);
          dao = findViewById(R.id.daojishi);
          start=findViewById(R.id.imageView2);
      }

      ////    private final Runnable task = new Runnable() {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            if (run) {
//                onResume();
//                handler.postDelayed(this, 5000);
//            }
//        }
//    };
      @Override
      protected void onResume() {
          super.onResume();

          // Initializes list view adapter.
          mLeDeviceListAdapter = new LeDeviceListAdapter(this);
//        Collections.sort(mLeDeviceListAdapter.getlist(), new LeDeviceListAdapter.sortById());
//        System.out.println("Asd"+mLeDeviceListAdapter.getlist().size());
//        if(mLeDeviceListAdapter.getlist().size()>0) {
//            System.out.println("Asd"+ mLeDeviceListAdapter.getlist().get(0).bluetoothAddress);
//            Content.setText("你目前所在位置:" + mLeDeviceListAdapter.getlist().get(0).bluetoothAddress);
//        }
          scanLeDevice(true);
      }

      private void scanLeDevice(boolean enable) {
          // Stops scanning after a pre-defined scan period.
          if (enable) {
              // Stops scanning after a pre-defined scan period.
              mHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      mScanning = false;
                      mBluetoothAdapter.stopLeScan(mLeScanCallback);
                      invalidateOptionsMenu();
                  }
              }, SCAN_PERIOD);

              mScanning = true;
              mBluetoothAdapter.startLeScan(mLeScanCallback);
          } else {
              mScanning = false;
              mBluetoothAdapter.stopLeScan(mLeScanCallback);
          }
          invalidateOptionsMenu();
      }

      // Device scan callback.
      private final BluetoothAdapter.LeScanCallback mLeScanCallback =
              new BluetoothAdapter.LeScanCallback() {//对设备列表进行刷新的接口
                  @SuppressLint("SetTextI18n")
                  @Override
                  public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {//每扫描到一个设备刷新数据

                      final iBeaconClass.iBeacon ibeacon = iBeaconClass.fromScanData(device, rssi, scanRecord);
                      runOnUiThread(() -> {
                          mLeDeviceListAdapter.addDevice(ibeacon);
                          Collections.sort(mLeDeviceListAdapter.getlist(), new LeDeviceListAdapter.sortById());
                          if (mLeDeviceListAdapter.getlist().size() > 0) {
                              if (!mLeDeviceListAdapter.getlist().get(0).bluetoothAddress.equals(nowadress)) {//顶部蓝牙设备变化
                                  if (now != null) {
                                      now.setEndTime(NowString());
                                      insert(now);//插入数据
                                      now = null;
                                  }
                                  now=new state();
                                  now.setDeviceID(id);
                                  now.setStartTime(NowString());
                                  starting();//记录停留时间
                                  nowadress = mLeDeviceListAdapter.getlist().get(0).bluetoothAddress;
                              }
                              now.setBeaconID(nowadress);
                              Content.setText("你目前所在位置信标ID为:" + nowadress.substring(12, 14) + nowadress.substring(15, 17));
                          }
                          mLeDeviceListAdapter.notifyDataSetChanged();
                      });
                  }
              };

      public void insert(state now){
          SQLiteDatabase db = dbHelper.getWritableDatabase();
          ContentValues values = new ContentValues();
          // 第一条数据
          values.put("iBeaconid", now.getBeaconID());
          values.put("deviceid", now.getDeviceID());
          values.put("starttime", now.getStartTime());
          values.put("endtime", now.getEndTime());
          System.out.println(values.get("starttime"));
          long a=db.insert("info", null, values);
      }//插入数据

      public String NowString() {
          @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
          return df.format(new Date());// new Date()为获取当前系统时间

      }//日期格式整理

      private void starting() {
          seconds = 0;
          if (timer != null) {
              timer.cancel();
              timer = null;
          }
          if (timerTask != null) {
              timerTask = null;
          }
          timerTask = new TimerTask() {
              @Override
              public void run() {
                  Message msg = new Message();
                  msg.what = 0;
                  handler.sendMessage(msg);
              }
          };
          timer = new Timer();
          timer.schedule(timerTask, 0, 1000);
      }//开始倒计时

      public void ExportToCSV(Cursor c, String fileName) {//到处csv文件

          int rowCount = 0;
          int colCount = 0;
          FileWriter fw;
          BufferedWriter bfw;
          File sdCardDir = Environment.getExternalStorageDirectory();
          File saveFile = new File(sdCardDir, fileName);
          try {

              rowCount = c.getCount();
              colCount = c.getColumnCount();
              fw = new FileWriter(saveFile);
              bfw = new BufferedWriter(fw);
              if (rowCount > 0) {
                  c.moveToFirst();
                  // 写入表头
                  for (int i = 0; i < colCount; i++) {
                      if (i != colCount - 1)
                          bfw.write(c.getColumnName(i) + ',');
                      else
                          bfw.write(c.getColumnName(i));
                  }
                  // 写好表头后换行
                  bfw.newLine();
                  // 写入数据
                  for (int i = 0; i < rowCount; i++) {
                      c.moveToPosition(i);
                      // Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
                      // Toast.LENGTH_SHORT).show();
                      for (int j = 0; j < colCount; j++) {
                          if (j != colCount - 1)
                              bfw.write(c.getString(j) + ',');
                          else
                              bfw.write(c.getString(j));
                      }
                      // 写好每条记录后换行
                      bfw.newLine();
                  }
              }
              // 将缓存数据写入文件
              bfw.flush();
              // 释放缓存
              bfw.close();
              // Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
              System.out.println("导出数据导出完毕！");
          } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } finally {
              c.close();
          }
      }
      //这是接收回来处理的消息
      @SuppressLint("HandlerLeak")
      private final Handler handler = new Handler() {
          @SuppressLint("SetTextI18n")
          public void handleMessage(Message msg) {
              seconds++;
              Time.setText("停留时间为:" + formatTime(seconds));
          }

      };

      @SuppressLint("DefaultLocale")
      private String formatTime(long seconds) {
          return String.format(" %02d时%02d分%02d秒", seconds / 3600, seconds % 3600 / 60, seconds % 60);
      }

      /**
       * CountDownTimer 实现倒计时
       */
      private final CountDownTimer countDownTimer = new CountDownTimer(4 * 60 * 60 * 1000, 1000) {
          @Override
          public void onTick(long millisUntilFinished) {
              String value = formatTime(millisUntilFinished/1000);
              dao.setText(value);
          }

          @Override
          public void onFinish() {
              Intent intent=new Intent(show.this, start.class);
              startActivity(intent);
          }
      };
      @SuppressLint("NonConstantResourceId")
      public void onClick(View v) {
          if (v.getId() == R.id.imageView2) {// 此处添加事件
              scanLeDevice(false);

              countDownTimer.cancel();
              SQLiteDatabase helper=dbHelper.getWritableDatabase();
              Cursor c = helper.rawQuery("select * from info", null);
              System.out.println(c.getCount());
              ExportToCSV(c, "test.csv");
              helper.execSQL("delete from info");
              helper.execSQL("DELETE FROM sqlite_sequence");
              Intent intent = new Intent(show.this, start.class);
              startActivity(intent);
          }
      }
  }