package jp.ac.nii.mqtt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private static final String TAG = "MainActivity";
    MqttAndroidClient mClient;
    EditText host, port, clientId;
    EditText mess_topic, mess_dt, qos_dt, retained_dt, gps_dt;
    Button connect, publish, subscribe, disconnect, cameraButton;
    Context myContext = MainActivity.this;

    //カメラの設定
    private ImageView imageView;
    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;
    public static final int RESULT_OK = -1;


    private Uri cameraUri;
    private String filePath;


    //   userName,passWord
//      光センサー
    private SensorManager manager;
    private Sensor lightSensor;
    //      GPSセンサー
    private LocationManager locationManager;

//    Image to byte arraylist
//    private String filename = "/Users/sun/AndroidStudioProjects/MQTT/app/imgs/lane.png";
    private Bitmap imageForScale;
    String imgString;

    //    string to Json style
    String script, topic, message;
    PopupMenu popupTopic, popupMessage;

    Button popTopicButton, popSensorButton;
    Intent mainintent;
    List<Double> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        host = findViewById(R.id.host);
        port = findViewById(R.id.port);
        clientId = findViewById(R.id.clientId);
//        userName = findViewById(R.id.userName);
//        passWord = findViewById(R.id.passWord);


        mess_topic = findViewById(R.id.mess_topic);
        mess_dt = findViewById(R.id.mess_dt);
        qos_dt = findViewById(R.id.qos);
        retained_dt = findViewById(R.id.retained);
        gps_dt = findViewById(R.id.gps_sensor);

        connect = findViewById(R.id.connect);
        publish = findViewById(R.id.publish);
        subscribe = findViewById(R.id.subscribe);
        disconnect = findViewById(R.id.disconnect);
        popTopicButton = findViewById(R.id.popupButton);
        popSensorButton = findViewById(R.id.popupSensorButton);

        mainintent = getIntent();

        if (mainintent.getStringExtra("GPS_value") != null) {
            gps_dt.setText(mainintent.getStringExtra("GPS_value"));
        } else {
            gps_dt.setText("GPS Information is not start!");
        }


//        Bundle bundle = mainintent.getExtras();
//        String x = bundle.getString("x");
//        String y = bundle.getString("y");
//        String z = bundle.getString("z");
//
//        gps_dt.setText(x + "," + y + "," + z);

        mess_topic.setText("mqtt-android-light");
        list = new ArrayList<Double>();

        popTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PopupMenuのインスタンスを作成
                popupTopic = new PopupMenu(MainActivity.this, view);

                // popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
                popupTopic.getMenuInflater().inflate(R.menu.topic, popupTopic.getMenu());

                // ポップアップメニューを表示
                popupTopic.show();

                // ポップアップメニューのメニュー項目のクリック処理
                popupTopic.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // 押されたメニュー項目名をToastで表示
                        mess_topic.setText(item.getTitle().toString());
//                        Toast.makeText(MainActivity.this, "Clicked : " + item.getTitle(),  Toast.LENGTH_SHORT).show();

                        return true;
                    }
                });
            }

        });

        popSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PopupMenuのインスタンスを作成
                popupMessage = new PopupMenu(MainActivity.this, view);

                // popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
                popupMessage.getMenuInflater().inflate(R.menu.sensor, popupMessage.getMenu());

                // ポップアップメニューを表示
                popupMessage.show();

                // ポップアップメニューのメニュー項目のクリック処理
                popupMessage.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // 押されたメニュー項目名をToastで表示
//                        message = item.getTitle().toString();
                        Toast.makeText(MainActivity.this, "Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (item.getTitle().equals("sensor-acceleromete")) {
                            Intent intent = new Intent(MainActivity.this, AccelerometerSensorActivity.class);
                            startActivity(intent);

                        } else if (item.getTitle().equals("sensor-temperature")) {
                            Intent intent = new Intent(MainActivity.this, GPSSensorActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
            }
        });

        //センサーマネージャを取得
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //センサマネージャから照度センサーを指定
        lightSensor = manager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mess_dt.setText("Illuminance Sensor ＝ ");

        //    Image to byte arraylist
        imageView = findViewById(R.id.image_view);
        cameraButton = findViewById(R.id.camera_button);

//MQTTに接続する
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    connect();
                }
            }
        });
// 光センサーの数値をpublishする
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              //   get the base 64 string


                try {
                    if (mClient.isConnected()) {

//                        String imgString = Base64.encodeToString(getBytesFromBitmap(imageForScale),
//                                Base64.NO_WRAP);


                        File imagefile = new File(filePath);
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(imagefile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        Bitmap bm = BitmapFactory.decodeStream(fis);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
                        byte[] b = baos.toByteArray();
                        imgString = Base64.encodeToString(b, Base64.DEFAULT);

                        mClient.publish("mqtt-android-video",imgString.getBytes(), 1, true);

                        Log.d("Test_Image",imgString);

                    }


//     光传播
//                    {
//
//
//                        MessageJson info = new MessageJson();
//                        info.time = info.timezone("Asia/Tokyo");
//                        info.time = info.timezone("Asia/Tokyo");
//                        info.sensor = mess_topic.getText().toString();
//
//                        if (gps_dt.getText().toString().contains(",")) {
//                            String line = gps_dt.getText().toString().replaceAll(" ", "");
//                            if (line.contains(",")) {
//                                String[] value = line.split(",", 0);
//                                info.longitude = Double.parseDouble(value[0]);
//                                info.latitude = Double.parseDouble(value[1]);
//                            }
//
//                        }
//                        String line = mess_dt.getText().toString().replaceAll(" ", "");
//
//                        Log.d(TAG,line);
//
//                        if (line.contains("＝")) {
//                            String[] value = line.split("＝", 0);
////                            list.add(Double.parseDouble(value[1]));
////                            info.setList(list);
//                            info.value=Double.parseDouble(value[1]);
//                            mess_dt.setText(value[1]);
//
//                        }
//
//
//                        ObjectMapper mapper = new ObjectMapper();
//                        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//                        try {
//                            script = mapper.writeValueAsString(info);
//                            System.out.println(script);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        mClient.publish(mess_topic.getText().toString(), script.getBytes(), 0, true);
//
////                      IMqttDeliveryToken token = mClient.publish(topic_dt.getText().toString(),imgString.getBytes(), 0, true);
////                      System.out.print("llllllllllllllllllllllll"+token.isComplete());
//                        Toast.makeText(MainActivity.this, "Message is published", Toast.LENGTH_LONG).show();
//                    }


                } catch (MqttPersistenceException e) {
                    Log.d(TAG, e.toString());
                } catch (MqttException e) {
                    Log.d(TAG, e.toString());
                }
//センサーリストの確認
//                manager = (SensorManager) MainActivity.this.getSystemService(Context.SENSOR_SERVICE);
//                List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
//                for (Sensor sensor : sensors) {
////                    Toast.makeText(MainActivity.this , "name:" + sensor.getName() + " type:" + String.valueOf(sensor.getType()), Toast.LENGTH_LONG).show();
//
//                    Log.d("Sensor","name:" + sensor.getName() + " type:" + String.valueOf(sensor.getType()));
//                }


// 光センサーのサンプルソースコード
//                Intent intent = new Intent(MainActivity.this, LightSensorActivity.class);
//                startActivity(intent);

//                light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//                Toast.makeText(MainActivity.this , "temperature sensor:"+String.valueOf(temperature), Toast.LENGTH_LONG).show();
//                Log.d("atemperature", String.valueOf(light));


            }
        });

//光センサーの結果をsubscribeする．
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubsribeActivity.class);

                System.out.println("ssssssssssssssssssss" + clientId.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putString("clientid", clientId.getText().toString());
                bundle.putString("topic", mess_topic.getText().toString());
                bundle.putString("host", host.getText().toString());
                bundle.putInt("qos", Integer.parseInt(qos_dt.getText().toString()));
//                bundle.putString("image_path", imgString);

                intent.putExtras(bundle);
                startActivity(intent);

//センサーリストの確認
//                Intent intent = new Intent(MainActivity.this, SensorListActivity.class);
//                startActivity(intent);
//                Intent intent = new Intent(MainActivity.this, GPSSensorActivity.class);
//                startActivity(intent);


            }
        });
//MQTTに切断する．

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (mClient.isConnected()) {
                        mClient.disconnect();
                        Log.d(TAG, "disconnect");
                    }

                    mClient.unregisterResources();
                    connect.setText("disconnected");
                    Toast.makeText(MainActivity.this, "Disconnected with Mosquitto MQTT Broker", Toast.LENGTH_LONG).show();

                } catch (MqttException e) {
                    Log.d(TAG, e.toString());
                }

            }
        });


//        Intent intent2 = getIntent();
//        Bundle bundle = intent2.getExtras();
//        String sensor_id = bundle.getString("sensor");
//        mess_dt.setText(sensor_id);


//GPSセンサー
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
        } else {
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 50, this);

        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Android 6, API 23以上でパーミッシンの確認
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else {
                    cameraIntent();
                }
            }
        });

    }

    private void connect() {
        String serUrl = getServerURI(host.getText().toString(), port.getText().toString());
        connect.setText("connecting");
        //serUrlはクライアント名，clientidはMQTTのクライアントID，（通常クライアントの唯一の識別子です），
        // MemoryPersistenceはclientidの保存形式を設定する（ディフォルトはメモリ保存）
        mClient = new MqttAndroidClient(myContext, serUrl, clientId.getText().toString(), new MemoryPersistence());
        //CallBackの設定
        mClient.setCallback(mqttCallbackExtended);
        // connect テスト
        try {
            mClient.connect(getMqttConnectOptions(), "Connect", iMqttActionListener);
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
//現状MQTTサーバのセキュリティ認証がないため，下記の情報を利用していないです
//        if (TextUtils.isEmpty(userName.getText().toString())) {
//            Toast.makeText(MainActivity.this, "userName can't be empty", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (TextUtils.isEmpty(passWord.getText().toString())) {
//            Toast.makeText(MainActivity.this, "passWord can't be empty", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    private MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            //接続が落ちた際に，再接続する
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
            //subscribeした後に，受け取ったメッセージはここで処理する
            Log.e(TAG, "Incoming message: " + new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            //publishした後に，メッセージはここで処理する
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

    public String getServerURI(String host, String port) {
        return "tcp://" + host + ":" + port;
    }

    //MQTTの他のオプションの情報の設置設定
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        //セッションをクリアするかどうかを設定します．
        // ここでfalseに設定すると、サーバーはクライアントの接続レコードを保持します．
        // ここでtrueに設定すると、サーバーへの接続は新しいIDで接続されます．
        options.setCleanSession(true);
        //タイムアウトを秒単位で設定します
        options.setConnectionTimeout(10);
        //セッションのハートビート時間を秒単位で設定します。
        // サーバーはクライアントがオンラインかどうかを判断するために1.5 * 20秒ごとにメッセージをクライアントに送信しますが、
        // この方法には再接続メカニズムはありません。
        options.setKeepAliveInterval(20);

//現状MQTTサーバにセキュリティ認証を導入していないため，下記の情報を省略する
//        MQTTサーバのユーザ名の設定
//        options.setUserName(userName.getText().toString().trim());
//        options.setUserName("admin");
//        options.setPassword("hivemq".toCharArray());
//         MQTTサーバのパスワードの設定
//        options.setPassword(passWord.getText().toString().trim().toCharArray());

        return options;
    }

    @Override
    protected void onDestroy() {
        try {
            if (mClient != null && mClient.isConnected()) {
                mClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        float[] values = event.values;
        long timestamp = event.timestamp;

        // 照度センサー
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            // 照度
            Log.d("SENSOR_DATA", "TYPE_LIGHT = " + String.valueOf(values[0]));
            mess_dt.setText("Illuminance Sensor＝"+ String.valueOf(values[0]));
        }


//        Intent intent = new Intent(LightSensorActivity.this, MainActivity.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("sensor", String.valueOf(values[0]));
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        // リスナー設定
        manager.registerListener(this,
                lightSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // リスナー解除
        manager.unregisterListener(this, lightSensor);
    }

    @Override
    public void onLocationChanged(Location location) {

        String str1 = Double.toString(location.getLatitude());
        String str2 = Double.toString(location.getLongitude());
        gps_dt.setText(str1 + "," + str2);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        switch (i) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    // 結果の受け取り

    /**
     * Android Quickstart:
     * https://developers.google.com/sheets/api/quickstart/android
     * <p>
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true");

                locationStart();

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
//        カメラの設定
        Log.d("debug", "onRequestPermissionsResult()");

        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        if(requestCode == RESULT_OK){
            Bundle bundle_1 = mainintent.getExtras();
            String x = bundle_1.getString("x");
            String y = bundle_1.getString("y");
            String z = bundle_1.getString("z");
            Toast.makeText(this,
                    x+","+y+","+z, Toast.LENGTH_SHORT).show();
//            gps_dt.setText(x+","+y+","+z);
//            Toast.makeText(this,"name:? -> " + x+","+y+","+z, Toast.LENGTH_SHORT).show();
        }
    }

    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 50, this);

    }

//    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


    public static boolean countDown(int seconds) {
        System.err.println("倒计时" + seconds + "秒,倒计时开始:");
        int i = seconds;
        while (i > 0) {
            System.err.println(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            i--;
        }
        System.err.println(i);
        System.err.println("倒计时结束");
        return true;
    }


    private void cameraIntent() {
        Log.d("debug", "cameraIntent()");

        // 保存先のフォルダーを作成するケース
//        File cameraFolder = new File(
//                Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES),"IMG");
//        cameraFolder.mkdirs();

        // 保存先のフォルダーをカメラに指定した場合
        File cameraFolder = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM), "Camera");


        // 保存ファイル名
        String fileName = new SimpleDateFormat(
                "ddHHmmss", Locale.US).format(new Date());
        filePath = String.format("%s/%s.jpg", cameraFolder.getPath(), fileName);
        Log.d("debug", "filePath:" + filePath);

        // capture画像のファイルパス
        File cameraFile = new File(filePath);
        cameraUri = FileProvider.getUriForFile(
                MainActivity.this,
                getApplicationContext().getPackageName() + ".fileprovider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);

        Log.d("debug", "startActivityForResult()");
    }


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        if (requestCode == RESULT_CAMERA) {

            if (cameraUri != null) {
                imageView.setImageURI(cameraUri);

                registerDatabase(filePath);
            } else {
                Log.d("debug", "cameraUri == null");
            }
        }
    }


    // アンドロイドのデータベースへ登録する
    private void registerDatabase(String file) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", file);
        contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    // Runtime Permission check
    private void checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            cameraIntent();
        }
        // 拒否していた場合
        else {
            requestPermission();
        }
    }

    // 許可を求める
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this,
                    "許可されないとアプリが実行できません",
                    Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    REQUEST_PERMISSION);

        }
    }


}
