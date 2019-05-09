package jp.ac.nii.mqtt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class SubsribeActivity extends AppCompatActivity {
    private final String TAG = "SubsribeActivity";
    private MqttAndroidClient mqttAndroidClient;
    private String ID = "admin";
    private String PASS = "livemq";
    EditText subscribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsribe);
        subscribe = findViewById(R.id.subscribe);
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

//        Intent intent = getIntent();
//        clinetid = intent.getStringExtra("client_id");
//        topic = intent.getStringExtra("topic");
//        qos = intent.getIntExtra("QoS", 0);
//        System.out.println("llllllllllllllllllll"+clinetid);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String client_id = bundle.getString("clientid");
        String host = bundle.getString("host");
       final String topic = bundle.getString("topic");
       final int qos = bundle.getInt("qos");


                mqttAndroidClient = new MqttAndroidClient(this,"tcp://"+host+":1883",client_id){
//        mqttAndroidClient = new MqttAndroidClient(this,"tcp://136.187.163.103:1883","client_test001"){
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);

                Bundle data = intent.getExtras();

                String action = data.getString("MqttService.callbackAction");
                Object parcel = data.get("MqttService.PARCEL");
                String destinationName = data.getString("MqttService.destinationName");

                if(action.equals("messageArrived"))
                {
                    subscribe.setText(parcel.toString());
                    Log.d(TAG,destinationName + " aaaaaaaaaaaaaaaaaaaaaa: " + parcel.toString());
                }

            }

        };

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(ID);
            options.setPassword(PASS.toCharArray());

            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.d(TAG, "onSuccess");

                    try {
//                       mqttAndroidClient.subscribe("sun/test001", 0);
                        mqttAndroidClient.subscribe(topic, qos);

//                        subscribe.setText();

                        Log.d(TAG, "subscribe");
                    } catch (MqttException e) {
                        Log.d(TAG, e.toString());
                    }
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.d(TAG, "onFailure");
                }
            });

        } catch (MqttException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {

            if(mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnect();
                Log.d(TAG,"disconnect");
            }

            mqttAndroidClient.unregisterResources();

        } catch (MqttException e) {
            Log.d(TAG,e.toString());
        }

    }

}
