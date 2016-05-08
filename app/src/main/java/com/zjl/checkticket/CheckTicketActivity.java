package com.zjl.checkticket;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjl.checkticket.setting.SettingsActivity;

/**
 * 检票界面，显示检票结果；
 * 该界面不可见时，应该将检票工作委托给Service后台任务。
 */
public class CheckTicketActivity extends AppCompatActivity {

    private Button mCheckBtn;
    private EditText mTicketIdTxt;
    private RelativeLayout mResultLayout;
    private TextView mResultText;

    private int mCheckPassBgColor;
    private int mCheckFailBgColor;
    private int mCheckPassTextColor;
    private int mCheckFailTextColor;

    private String mCheckPassSentence;
    private String mCheckFailSentence;

    private String mTicketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_ticket);

        initResources();
        initWidgets();

    }

    private void initResources() {
        Resources resources = getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCheckPassBgColor = resources.getColor(R.color.check_pass_bg, null);
            mCheckFailBgColor = resources.getColor(R.color.check_fail_bg, null);
            mCheckPassTextColor = resources.getColor(R.color.check_pass_text, null);
            mCheckFailTextColor = resources.getColor(R.color.check_fail_text, null);
        } else {
            mCheckPassBgColor = resources.getColor(R.color.check_pass_bg);
            mCheckFailBgColor = resources.getColor(R.color.check_fail_bg);
            mCheckPassTextColor = resources.getColor(R.color.check_pass_text);
            mCheckFailTextColor = resources.getColor(R.color.check_fail_text);
        }

        mCheckPassSentence = resources.getString(R.string.check_pass);
        mCheckFailSentence = resources.getString(R.string.check_fail);
    }

    private void initWidgets() {
        mResultLayout = (RelativeLayout) findViewById(R.id.result_layout);
        mResultText = (TextView) mResultLayout.findViewById(R.id.result_txt);

        mTicketIdTxt = (EditText) findViewById(R.id.ticket_id_edit);
        mCheckBtn = (Button) findViewById(R.id.check_btn);

        mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTicketId = mTicketIdTxt.getText().toString();

                updateResultUI(TicketUtil.getInstance().checkValidity(mTicketId));
            }
        });

        Button settingBtn = (Button) findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckTicketActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateResultUI(boolean checkResult) {
        mResultLayout.setBackgroundColor(checkResult ? mCheckPassBgColor : mCheckFailBgColor);
        mResultText.setTextColor(checkResult ? mCheckPassTextColor : mCheckFailTextColor);

        mResultText.setText(checkResult ? mCheckPassSentence : mCheckFailSentence);
    }
}
