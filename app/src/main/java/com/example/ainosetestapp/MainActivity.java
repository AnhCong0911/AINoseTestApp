package com.example.ainosetestapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mTempSensor;
    private Sensor mHumiSensor;
    private Sensor mPressSensor;
    private Sensor mGasSensor;

    private TextView mTVGasLabel;
    private TextView mTVGasPred;
    private TextView mTVProbaLabel;
    private TextView mTVProbaPred;
    private String url = "https://ai-nose-web-app.herokuapp.com/predict";

    private HashMap<String, String> sensorData;

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

        sensorData = new HashMap<>();
        sensorData.put("temp", String.valueOf(0f));
        sensorData.put("humid", String.valueOf(0f));
        sensorData.put("press", String.valueOf(0f));
        sensorData.put("gas_res", String.valueOf(0f));
    }

    private void getRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject prediction = new JSONObject(response);
                int gasType = prediction.getInt("gas_type");
                String gas = (gasType == 1) ? "Caffee" : "Normal Air";
                double probaCf = prediction.getDouble("proba_cf");
                double probaNa = prediction.getDouble("proba_na");

                if (gasType == 1) {
                    mTVGasPred.setText(gas);
                    mTVProbaPred.setText(String.format("%.2f", probaCf * 100));
                    Log.e("GAS_TYPE", "================= Gas: " + gas + ", Probability: " + probaCf);
                } else {
                    mTVGasPred.setText(gas);
                    mTVProbaPred.setText(String.format("%.2f", probaNa * 100));
                    Log.e("GAS_TYPE", "================= Gas: " + gas + ", Probability: " + probaNa);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = sensorData;
                Log.e("Data", "== Temp: " + sensorData.get("temp") +
                        "\t== Humid: " + sensorData.get("humid") +
                        "\t== Press: " + sensorData.get("press") +
                        "\t== Gas_res: " + sensorData.get("gas_res"));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);
    }

    private void initView() {
        mTVGasLabel = findViewById(R.id.tv_label);
        mTVGasPred = findViewById(R.id.tv_gas_pred);
        mTVProbaLabel = findViewById(R.id.tv_proba);
        mTVProbaPred = findViewById(R.id.tv_proba_pred);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            // gotoMenuScreen();
            openDataActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openDataActivity() {
        Intent mIntent = new Intent(MainActivity.this, DataActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float value = sensorEvent.values[0];

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
//                Log.e("SENSOR-GAS", "================= 1110 - TYPE_AMBIENT_TEMPERATURE => " + value);
                // temp / 100 = value / 100
                sensorData.put("temp", String.valueOf(value/100.0));
                handleDataChange(sensorData);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
//                Log.e("SENSOR-GAS", "================= 1110 - TYPE_HUMIDITY => " + value);
                sensorData.put("humid", String.valueOf(value));
                handleDataChange(sensorData);
                break;
            case Sensor.TYPE_PRESSURE:
//                Log.e("SENSOR-GAS", "================= 1110 - TYPE_PRESSURE => " + value);
                sensorData.put("press", String.valueOf(value));
                handleDataChange(sensorData);
                break;
            case 33171005:
//                Log.e("SENSOR-GAS", "================= 1110 - 33171005 - => " + sensorEvent.values[0]);
                sensorData.put("gas_res", String.valueOf(value));
                handleDataChange(sensorData);
                break;
        }

    }

    private void handleDataChange(HashMap<String, String> hashMap) {
        if (!checkZeroValue(hashMap)) {
            getRequest();
        }
    }

    private boolean checkZeroValue(HashMap<String, String> hashMap) {
        boolean temp = false; // Default: non-zero
        for (String value : hashMap.values()) {
            if (value.equalsIgnoreCase(String.valueOf(0f))) {
                temp = true;
                break;
            }
        }
        return temp;
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