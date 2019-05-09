package jp.ac.nii.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MqttAndroidClient mClient;
    EditText host,port,clientId,userName,passWord;
    EditText topic_dt, mess_dt, qos_dt,retained_dt;
    Button connect,publish, subscribe, disconnect;
    Context myContext = MainActivity.this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        host = findViewById(R.id.host);
        port = findViewById(R.id.port);
        clientId = findViewById(R.id.clientId);
        userName = findViewById(R.id.userName);
        passWord = findViewById(R.id.passWord);


        topic_dt = findViewById(R.id.topic);
        mess_dt = findViewById(R.id.mess);
        qos_dt = findViewById(R.id.qos);
        retained_dt = findViewById(R.id.retained);

        connect = findViewById(R.id.connect);
        publish = findViewById(R.id.publish);
        subscribe = findViewById(R.id.subscribe);
        disconnect = findViewById(R.id.disconnect);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    connect();
                }
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(mClient.isConnected()) {
                       IMqttDeliveryToken token = mClient.publish(topic_dt.getText().toString(),mess_dt.getText().toString().getBytes(), 0, true);
//                       System.out.print("llllllllllllllllllllllll"+token.isComplete());
                       Toast.makeText(MainActivity.this , "Message is published", Toast.LENGTH_LONG).show();
                    }
                } catch (MqttPersistenceException e) {
                    Log.d(TAG,e.toString());
                } catch (MqttException e) {
                    Log.d(TAG,e.toString());
                }


            }
        });
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubsribeActivity.class);

                System.out.println("ssssssssssssssssssss"+clientId.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putString("clientid", clientId.getText().toString());
                bundle.putString("topic", topic_dt.getText().toString());
                bundle.putString("host", host.getText().toString());
                bundle.putInt("qos", Integer.parseInt(qos_dt.getText().toString()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if(mClient.isConnected()) {
                        mClient.disconnect();
                        Log.d(TAG,"disconnect");
                    }

                    mClient.unregisterResources();
                    connect.setText("disconnected");
                    Toast.makeText(MainActivity.this , "Disconnected with Hivemq", Toast.LENGTH_LONG).show();

                } catch (MqttException e) {
                    Log.d(TAG,e.toString());
                }

            }
        });
    }

    private void connect() {
        String serUrl =getServerURI(host.getText().toString(), port.getText().toString());
        connect.setText("connecting");
        //serUrl爲主機名，clientid即連接MQTT的客戶端ID，一般以客戶端唯一標識符表示，MemoryPersistence設置clientid的保存形式，默認爲以內存保存
        mClient = new MqttAndroidClient(myContext, serUrl, clientId.getText().toString(),new MemoryPersistence());
        //設置回調
        mClient.setCallback(mqttCallbackExtended);
        // connect テスト
        try {
            mClient.connect(getMqttConnectOptions(), "Connect", iMqttActionListener);

//            // publish テスト
//            MqttMessage message = new MqttMessage(content.getBytes());
//            message.setQos(qos);
//            message.setRetained(true);
//            mClient.publish(topic, message);
//            mClient.disconnect();
        } catch (MqttException ex) {
            ex.printStackTrace();
        }



    }


    private boolean check() {
        if (TextUtils.isEmpty(host.getText().toString())) {
            Toast.makeText(MainActivity.this, "host can't be empty ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(port.getText().toString())) {
            Toast.makeText(MainActivity.this, "port can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(clientId.getText().toString())) {
            Toast.makeText(MainActivity.this, "clientIds can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(userName.getText().toString())) {
            Toast.makeText(MainActivity.this, "userName can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(passWord.getText().toString())) {
            Toast.makeText(MainActivity.this, "passWord can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            //連接丟失後，一般在這裏面進行重連
            if (reconnect) {
                Log.e(TAG, "Reconnected to : " + serverURI);
                connect.setText("connect success");
            } else {
                Log.e(TAG, "Connected to: " + serverURI);
                connect.setText("connect success");
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.e(TAG, "The Connection was lost.");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            //subscribe後得到的消息會執行到這裏面
            Log.e(TAG, "Incoming message: " + new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            //publish後會執行到這裏
        }
    };

    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.e(TAG, "onSuccess!");
            connect.setText("connect success");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.e(TAG, "exception: " + exception.getMessage());
            connect.setText("connect failure");
        }
    };

    public String getServerURI(String host,String port) {
        return "tcp://" + host + ":" + port;
    }

    //MQTT的連接設置
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        //設置是否清空session,這裏如果設置爲false表示服務器會保留客戶端的連接記錄，這裏設置爲true表示每次連接到服務器都以新的身份連接
        options.setCleanSession(true);
        // 設置超時時間 單位爲秒
        options.setConnectionTimeout(10);
        // 設置會話心跳時間 單位爲秒 服務器會每隔1.5*20秒的時間向客戶端發送個消息判斷客戶端是否在線，但這個方法並沒有重連的機制
        options.setKeepAliveInterval(20);
        //設置連接的用戶名
        options.setUserName(userName.getText().toString().trim());
        //設置連接的密碼
        options.setPassword(passWord.getText().toString().trim().toCharArray());
        return options;
    }

    @Override
    protected void onDestroy() {
        try {
            if (mClient!=null&&mClient.isConnected()){
                mClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
