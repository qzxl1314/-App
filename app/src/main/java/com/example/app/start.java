package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class start extends AppCompatActivity {
    ImageView start;
    EditText name;
    EditText id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();
        SharedPreferences sharedPre=getSharedPreferences("config", MODE_PRIVATE);
        String username=sharedPre.getString("username", "");
        String userid=sharedPre.getString("userid", "");
        name.setText(username);
        id.setText(userid);
        start.setOnClickListener(this::onClick);
    }
    public void init() {//初始化控件
    start=findViewById(R.id.imageView2);
    name=findViewById(R.id.editTextTextPersonName);
    id=findViewById(R.id.editTextTextPersonName2);
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