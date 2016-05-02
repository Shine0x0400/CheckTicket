package com.zjl.checkticket.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zjl.checkticket.R;

/**
 * Created by zzg on 2016/5/1.
 * 统计页面
 */
public class TongJiActivity extends FragmentActivity {
    private Fragment[] mFragments;
    private RadioGroup bottomRg;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RadioButton jianpiao, erci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tongji2);
        mFragments = new Fragment[2];
        fragmentManager = getSupportFragmentManager();
        mFragments[0] = fragmentManager.findFragmentById(R.id.fragement_jianpian);
        mFragments[1] = fragmentManager.findFragmentById(R.id.fragement_erci);
             fragmentTransaction = fragmentManager.beginTransaction()
                .hide(mFragments[0]).hide(mFragments[1]);

        fragmentTransaction.show(mFragments[0]).commit();
        setFragmentIndicator();
    }

    private void setFragmentIndicator() {

        bottomRg = (RadioGroup) findViewById(R.id.content);
        jianpiao = (RadioButton) findViewById(R.id.check);
        erci = (RadioButton) findViewById(R.id.tongji);


        bottomRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                fragmentTransaction = fragmentManager.beginTransaction()
                        .hide(mFragments[0]).hide(mFragments[1]);
                switch (checkedId) {
                    case R.id.check:

                        fragmentTransaction.show(mFragments[0]).commit();
                        break;

                    case R.id.tongji:

                        fragmentTransaction.show(mFragments[1]).commit();

                        break;


                    default:
                        break;
                }
            }
        });
    }

}


