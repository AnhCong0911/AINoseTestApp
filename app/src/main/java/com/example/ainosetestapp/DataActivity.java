package com.example.ainosetestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DataActivity extends AppCompatActivity {
    private TextView mTvLabel;
    private EditText mEdtLabel;
    private Button mBtnOk;
    private Button mBtnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        initView();
        initFocusState();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void initFocusState() {
        mEdtLabel.requestFocus();
    }

    private void initView() {
        mTvLabel = findViewById(R.id.tv_label);
        mEdtLabel = findViewById(R.id.edt_label);
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnStop = findViewById(R.id.btn_stop);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                changeViewFocus();
                break;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeViewFocus() {
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            switch (focusedView.getId()){
                case R.id.edt_label:
                    mBtnOk.requestFocus();
                    break;
                case R.id.btn_ok:
                    mBtnStop.requestFocus();
                    break;
                case R.id.btn_stop:
                    mEdtLabel.requestFocus();
            }
        } else {
            mEdtLabel.requestFocus();
        }
    }
}