package jp.ac.nii.mqtt;

import android.app.ListActivity;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class SensorListActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        editText = findViewById(R.id.editText);

        startSensor();

    }

    private void startSensor() {
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager == null) {
            throw new UnsupportedOperationException();
        }

        List<Sensor> sensorsList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorsList) {
            editText.append(sensor.getName());
            System.out.println("sssssssssssssssss"+sensor.getName());
        }
    }
}