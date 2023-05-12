package com.example.ainosetestapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mTempSensor;
    private Sensor mHumiSensor;
    private Sensor mPressSensor;
    private Sensor mGasSensor;

    private TextView mTVTemp;
    private TextView mTVHumi;
    private TextView mTVPress;
    private TextView mTVGas;

    public static final int TIME_RECEIVE_DATA_FROM_SENSOR = 2000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumiSensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mPressSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mGasSensor = sensorManager.getDefaultSensor(33171005);
    }

    private void initView() {
        mTVTemp = findViewById(R.id.tv_temp);
        mTVHumi = findViewById(R.id.tv_humi);
        mTVPress = findViewById(R.id.tv_press);
        mTVGas = findViewById(R.id.tv_gas_res);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int value = Math.round(sensorEvent.values[0]);

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                Log.e("SENSOR-GAS", "================= 1110 - TYPE_AMBIENT_TEMPERATURE => " + value);
                // temp / 100 = value / 100
                mTVTemp.setText(String.format("%s °C", value / 100));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                Log.e("SENSOR-GAS", "================= 1110 - TYPE_HUMIDITY => " + value);
                mTVHumi.setText(String.format("%s %%", value));
                break;
            case Sensor.TYPE_PRESSURE:
                Log.e("SENSOR-GAS", "================= 1110 - TYPE_PRESSURE => " + value);
                mTVPress.setText(String.format("%s hPa", value));
                break;
            case 33171005:
                Log.e("SENSOR-GAS", "================= 1110 - 33171005 - => " + sensorEvent.values[0]);
                mTVGas.setText(String.format("%s kΩ", Math.round(value)));
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AI Nose", "--- AIR : RegisterSensor");
        sensorManager.registerListener(this, mTempSensor, TIME_RECEIVE_DATA_FROM_SENSOR);
        sensorManager.registerListener(this, mHumiSensor, TIME_RECEIVE_DATA_FROM_SENSOR);
        sensorManager.registerListener(this, mPressSensor, TIME_RECEIVE_DATA_FROM_SENSOR);
        sensorManager.registerListener(this, mGasSensor, TIME_RECEIVE_DATA_FROM_SENSOR);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("AI Nose", "--- AIR : unRegisterSensor");
        sensorManager.unregisterListener(this, mTempSensor);
        sensorManager.unregisterListener(this, mHumiSensor);
        sensorManager.unregisterListener(this, mPressSensor);
        sensorManager.unregisterListener(this, mGasSensor);
    }
}