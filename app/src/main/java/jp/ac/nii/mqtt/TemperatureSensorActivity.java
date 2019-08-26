package jp.ac.nii.mqtt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TemperatureSensorActivity extends AppCompatActivity implements SensorEventListener{

    private  SensorManager mSensorManager;
    private  Sensor mTempSensor;
    private TextView temperture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_sensor);

        temperture =findViewById(R.id.temperture);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        float[] values = sensorEvent.values;
        long timestamp = sensorEvent.timestamp;
        // 照度センサー
        if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            // 照度
//            Log.d("SENSOR_DATA", "TYPE_LIGHT = " + String.valueOf(values[0]));
            temperture.setText("Illuminance Sensor ＝ " + String.valueOf(values[0]));
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}