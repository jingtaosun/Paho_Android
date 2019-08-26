package jp.ac.nii.mqtt;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AccelerometerSensorActivity extends AppCompatActivity implements SensorEventListener {
    private float mSensorX;
    private float mSensorY;
    private Display mDisplay;
    private SensorManager sm;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    TextView sensor, contens;
    private String line;
    private Button back;
    public static final int RESULT_OK           = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get an instance of the SensorManager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // Get an instance of the WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();


        setContentView(R.layout.activity_accelerometer_sensor);
        sensor = findViewById(R.id.textView222);
        contens = findViewById(R.id.textcon);
        back = findViewById(R.id.buttonback);
        contens.setText("加速度センサー値:");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value[] = line.split(",", 0);
//                Bundle bundle = new Bundle();
//                bundle.putString("x", value[0]);
//                bundle.putString("y", value[1]);
//                bundle.putString("z", value[2]);
                Intent intent = new Intent();
                intent.putExtra("x",value[0]);
                intent.putExtra("y",value[1]);
                intent.putExtra("z",value[2]);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            String str =  "\nX軸:" + event.values[SensorManager.DATA_X] + "\nY軸:" + event.values[SensorManager.DATA_Y] + "\nZ軸:" + event.values[SensorManager.DATA_Z];
            String str = event.values[SensorManager.DATA_X] + "," + event.values[SensorManager.DATA_Y] + "," + event.values[SensorManager.DATA_Z];

            sensor.setText(str);
            line = str;
        }


//        finish();

//        Bundle bundle = new Bundle();
//        bundle.putString("x", value[0]);
//        bundle.putString("y", value[1]);
//        bundle.putString("z", value[2]);
//        Intent intent = new Intent(getApplicationContext(), AccelerometerSensorActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);

    }

}