package com.zjl.checkticket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zjl.checkticket.R;

/** 主界面
 * Created by zzg on 2016/5/1.
 */
public class MainActivity extends Activity {
    Button jianpiao;//检票按钮
    Button tongji;//统计按钮
    Button guanli;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      inti();
    }
    public void inti(){
        jianpiao=(Button) findViewById(R.id.check);
        tongji=(Button) findViewById(R.id.tongji);
        guanli=(Button) findViewById(R.id.guanli);
        setOnclickListner();
    }

    /**
     * 设置监听事件
     */
    public void setOnclickListner(){
        jianpiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);
            }
        });
        tongji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TongJiActivity.class);

                startActivity(intent);

            }
        });
        guanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, GuanLiActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    Intent resultIntent = new Intent();
                    resultIntent.setClass(this,JianPiaoResultActivity.class);
                    resultIntent.putExtras(bundle);
                    startActivity(resultIntent);
                }
                break;
        }
    }
}
