package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.tool.MyDatabaseHelper;

public class start extends AppCompatActivity {
    ImageView start;
    EditText name;
    EditText id;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();
        dbHelper = new MyDatabaseHelper(this, "beacon.db", null, 1);
        SharedPreferences sharedPre=getSharedPreferences("config", MODE_PRIVATE);
        String username=sharedPre.getString("username", "");
        String userid=sharedPre.getString("userid", "");
        name.setText(username);
        id.setText(userid);
        verifyStoragePermissions(this);
        start.setOnClickListener(this::onClick);

    }
    public void init() {//初始化控件
    start=findViewById(R.id.imageView2);
    name=findViewById(R.id.editTextTextPersonName);
    id=findViewById(R.id.editTextTextPersonName2);
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 102;
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
            //验证是否许可权限

            for (String str : permissions) {
                if (activity.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    activity.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView2:
                // 此处添加事件

                String username=name.getText().toString();
                String uid=id.getText().toString();
                saveLoginInfo(this,username,uid);
                Intent intent=new Intent(start.this, show.class);
                intent.putExtra("name",username);
                intent.putExtra("id",uid);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    public static void saveLoginInfo(Context context, String username, String password){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("config", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("username", username);
        editor.putString("userid", password);
        //提交
        editor.commit();
    }

}