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
 * Created by zzg on 2016/5/2.
 */
public class GuanLiActivity extends FragmentActivity {
    private Fragment[] mFragments;
    private RadioGroup bottomRg;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RadioButton upload, download,shezhi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guanli);
        mFragments = new Fragment[3];
        fragmentManager = getSupportFragmentManager();
        mFragments[0] = fragmentManager.findFragmentById(R.id.fragement_shangchuan);
        mFragments[1] = fragmentManager.findFragmentById(R.id.fragement_dowload);
        mFragments[1] = fragmentManager.findFragmentById(R.id.fragement_shezhi);
        fragmentTransaction = fragmentManager.beginTransaction()
                .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);

        fragmentTransaction.show(mFragments[0]).commit();
        setFragmentIndicator();
    }

    private void setFragmentIndicator() {

        bottomRg = (RadioGroup) findViewById(R.id.content);
        upload = (RadioButton) findViewById(R.id.upload);
        download = (RadioButton) findViewById(R.id.download);
        shezhi=(RadioButton) findViewById(R.id.shezhi);

        bottomRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                fragmentTransaction = fragmentManager.beginTransaction()
                        .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);
                switch (checkedId) {
                    case R.id.upload:

                        fragmentTransaction.show(mFragments[0]).commit();
                        break;

                    case R.id.download:

                        fragmentTransaction.show(mFragments[1]).commit();

                        break;
                    case R.id.shezhi:

                        fragmentTransaction.show(mFragments[2]).commit();

                        break;

                    default:
                        break;
                }
            }
        });
    }

}
