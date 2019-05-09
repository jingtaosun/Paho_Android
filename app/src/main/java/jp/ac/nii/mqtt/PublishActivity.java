package jp.ac.nii.mqtt;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class PublishActivity extends AppCompatActivity {
    private static final String TAG = "PublishActivity";
    // トピックはPublisherとSubscriberで同一である必要があります。
    String topic = "admin/test_nii";
    // 送信するデータ
    String content = "hello world";
    // QoSレベル(0〜2。2が一番確実な伝送を実現する)
    int qos = 0;

    private MqttAndroidClient mqttAndroidClient;
    EditText topic_dt, mess_dt, qos_dt,retained_dt;
    Button publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        topic_dt = findViewById(R.id.topic);
        mess_dt = findViewById(R.id.mess);
        qos_dt = findViewById(R.id.qos);
        retained_dt = findViewById(R.id.retained);
        publisher = findViewById(R.id.publisher);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        publisher.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mqttAndroidClient == null)
//                    return;
//                try {
//                    if(mqttAndroidClient.isConnected()) {
//                        IMqttDeliveryToken token = mqttAndroidClient.publish(topic_dt.getText().toString(), mess_dt.getText().toString().getBytes(), 0, true);
//                    }
//                } catch (MqttPersistenceException e) {
//                    Log.d(TAG,e.toString());
//                } catch (MqttException e) {
//                    Log.d(TAG,e.toString());
//                }
//            }
//        });
    }

}
