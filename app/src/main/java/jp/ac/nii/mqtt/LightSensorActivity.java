package jp.ac.nii.mqtt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager manager;

    // 照度センサー
    private Sensor lightSensor;

    // 表示用
    private TextView textview;

//    private SensorManager sensorManager;
//    private Sensor light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //センサーマネージャを取得
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //センサマネージャから照度センサーを指定
        lightSensor = manager.getDefaultSensor(Sensor.TYPE_LIGHT);

        textview = (TextView)findViewById(R.id.sensor);
        textview.setText("照度センサー　＝　");

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // accuracy に変更があった時の処理
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        float[] values = event.values;
        long timestamp = event.timestamp;

        // 照度センサー
        if(sensor.getType() == Sensor.TYPE_LIGHT){

            // 照度
            Log.d("SENSOR_DATA", "TYPE_LIGHT = " + String.valueOf(values[0]));
            textview.setText("照度センサー　＝　" + String.valueOf(values[0]));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // リスナー設定
        manager.registerListener (this,
                lightSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // リスナー解除
        manager.unregisterListener(this,lightSensor);
    }

}
