package jp.ac.nii.mqtt.demo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.ac.nii.mqtt.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int RESULT_OK = -1;
    private final static int RESULT_IMAGE = 1002;
    private final static int RESULT_VIDEO = 1003;
    private static final int REQUEST_VIDEO_CAPTURE = 100;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String videoCapturedUriPath;
    private Bitmap imageBitmap;
//    private ImageView test_image;


    private OnFragmentInteractionListener mListener;

    private Button popTopicButton, popSensorButton, popQosButton, popRetainedButton;
    private PopupMenu popupTopic, popupMessage, popQos, popRetained;
    private EditText mess_topic, mess_dt, qos_dt, retained_dt, gps_dt;
    private  Button publish;
    MqttAndroidClient mClient;
    Bundle bundle;

    private Button connect,disconnect;
    private String mqtt_info;
    private String currentPhotoPath;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();





    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_setting, container, false);
        popTopicButton = (Button)rootView.findViewById(R.id.popupButton);
        popSensorButton = (Button)rootView.findViewById(R.id.popupSensorButton);
        popQosButton = (Button)rootView.findViewById(R.id.popupQoS);
        popRetainedButton = (Button)rootView.findViewById(R.id.popupRetained);
//        test_image = (ImageView) rootView.findViewById(R.id.test_image);

        mess_topic = (EditText)rootView.findViewById(R.id.mess_topic);
        mess_dt = (EditText)rootView.findViewById(R.id.mess_dt);
        qos_dt = (EditText)rootView.findViewById(R.id.qos);
        retained_dt = (EditText)rootView.findViewById(R.id.retained);
//        gps_dt = (EditText)rootView.findViewById(R.id.gps_sensor);

        connect = (Button)rootView.findViewById(R.id.connect);
        disconnect = (Button)rootView.findViewById(R.id.disconnect);
        publish = (Button) rootView.findViewById(R.id.publish);


        bundle = this.getArguments();


        if (bundle != null) {
            mqtt_info = bundle.getString("mqtt-info");
            Log.d("infor", mqtt_info);
        }


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect(mqtt_info);
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
                    connect.setText("connecting");
                    disconnect.setText("disconnected");
                    Toast.makeText(getContext(), "Disconnected with Mosquitto MQTT Broker", Toast.LENGTH_LONG).show();

                } catch (MqttException e) {
                    Log.d(TAG, e.toString());
                }

            }
        });


        popTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PopupMenuのインスタンスを作成
                popupTopic = new PopupMenu(rootView.getContext(), view);

                // popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
                popupTopic.getMenuInflater().inflate(R.menu.topic, popupTopic.getMenu());

                // ポップアップメニューを表示
                popupTopic.show();

                // ポップアップメニューのメニュー項目のクリック処理
                popupTopic.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // 押されたメニュー項目名をToastで表示
                        mess_topic.setText(item.getTitle().toString());
//                        Log.d("video", item.getTitle().toString());
                        Toast.makeText(rootView.getContext(), "Clicked : " + item.getTitle(),  Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }

        });

        popSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PopupMenuのインスタンスを作成
                popupMessage = new PopupMenu(rootView.getContext(), view);

                // popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
                popupMessage.getMenuInflater().inflate(R.menu.sensor, popupMessage.getMenu());

                // ポップアップメニューを表示
                popupMessage.show();

                // ポップアップメニューのメニュー項目のクリック処理
                popupMessage.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // 押されたメニュー項目名をToastで表示
                        mess_dt.setText(item.getTitle().toString());
                        Toast.makeText(rootView.getContext(), "Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
//                        if (item.getTitle().equals("sensor-acceleromete")) {
//                            Intent intent = new Intent(rootView.getContext(), AccelerometerSensorActivity.class);
//                            startActivity(intent);
//
//                        } else if (item.getTitle().equals("sensor-temperature")) {
//                            Intent intent = new Intent(rootView.getContext(), GPSSensorActivity.class);
//                            startActivity(intent);
//                        }

//                        if (item.getTitle().equals("sensor-video")) {
//                            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                            if (videoIntent.resolveActivity(rootView.getContext().getPackageManager()) != null) {
//                                startActivityForResult(videoIntent, RESULT_VIDEO);
//                            }
//                        }
                        if (item.getTitle().equals("sensor-video")) {
                            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                            videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            if (videoIntent.resolveActivity(rootView.getContext().getPackageManager()) != null) {
                                startActivityForResult(videoIntent, RESULT_VIDEO);
                            }
                        } else if (item.getTitle().equals("sensor-image")) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            // Create the File where the photo should go
                            if (takePictureIntent.resolveActivity(rootView.getContext().getPackageManager()) != null) {

                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(getContext(),
                                            "jp.ac.nii.mqtt.fileprovider",
                                            photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, RESULT_IMAGE);
                                }
                            }
                        }
                        return true;
                    }
                });
            }
        });

        popQosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PopupMenuのインスタンスを作成
                popQos = new PopupMenu(rootView.getContext(), view);

                // popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
                popQos.getMenuInflater().inflate(R.menu.qos, popQos.getMenu());

                // ポップアップメニューを表示
                popQos.show();

                // ポップアップメニューのメニュー項目のクリック処理
                popQos.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // 押されたメニュー項目名をToastで表示
                        qos_dt.setText(item.getTitle().toString());
//                        Log.d("video", item.getTitle().toString());
                        Toast.makeText(rootView.getContext(), "Clicked : " + item.getTitle(),  Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }

        });

        popRetainedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PopupMenuのインスタンスを作成
                popRetained = new PopupMenu(rootView.getContext(), view);

                // popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
                popRetained.getMenuInflater().inflate(R.menu.retained, popRetained.getMenu());

                // ポップアップメニューを表示
                popRetained.show();

                // ポップアップメニューのメニュー項目のクリック処理
                popRetained.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // 押されたメニュー項目名をToastで表示
                        retained_dt.setText(item.getTitle().toString());
//                        Log.d("video", item.getTitle().toString());
                        Toast.makeText(rootView.getContext(), "Clicked : " + item.getTitle(),  Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }

        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String test_data="video_test";
//                    String encode = Base64.encodeToString(convertVideoToBytes(Uri.parse(videoCapturedUriPath)), Base64.DEFAULT);
//                    Log.d("video", encode);
//                    Bitmap myLogo = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.niilog);
//                    test_image.setImageBitmap(decodeBase64(imageStr));

//                    String imageStr = encodeTobase64(myLogo);

                    // メディアメタデータにアクセスするクラスをインスタンス化する。
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(getContext(), Uri.parse(videoCapturedUriPath));

                    int count = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
                    bitmapArrayList = new ArrayList<>();
                    for(int i = 0 ; i < count ; i++) {
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
                        Log.d("size", "size:"+bitmap.getWidth()+":"+bitmap.getHeight());

                        Bitmap resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.2), (int)(bitmap.getHeight()*0.2), true);
                        String imageStr = encodeTobase64(resized);
                        Log.d("size", "size:"+resized.getWidth()+":"+resized.getHeight());

//                        bitmapArrayList.add(resized);
//                        String imageStr = encodeTobase64(bitmapArrayList.get(i));
                        Log.d("value", imageStr);
                        mClient.publish(mess_topic.getText().toString(), imageStr.getBytes(), Integer.parseInt(qos_dt.getText().toString()), Boolean.parseBoolean(retained_dt.getText().toString()));
                    }
                    Toast.makeText(rootView.getContext(), "Data:"+test_data+ ",was transferred to MQTT Broker!",  Toast.LENGTH_SHORT).show();
                    Log.d("list",bitmapArrayList.size()+":");

                    mediaMetadataRetriever.release();

//
//                    MqttMessage message = new MqttMessage();
//                    message.setPayload(encode.getBytes());
//                    message.setRetained(Boolean.parseBoolean(retained_dt.getText().toString()));
//                    message.setQos(Integer.parseInt(qos_dt.getText().toString()));
//                    mClient.publish(mess_topic.getText().toString(), message);


                }catch (MqttPersistenceException e) {
                    Log.d(TAG, e.toString());
                } catch (MqttException e) {
                    Log.d(TAG, e.toString());
                }
            }
        });
        return rootView;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    //TODO convertBitmapToBytes
//    private byte[] convertBitmapToBytes(Bitmap bitmap){
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//
//        int size = bitmap.getRowBytes() * bitmap.getHeight();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
//        bitmap.copyPixelsToBuffer(byteBuffer);
//        return  byteBuffer.array();
//    }

    public static String encodeTobase64(Bitmap image) {

        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 8, baos);
        byte[] b = baos.toByteArray();

        StringBuilder buff=null;
        for (int i = 0; i< b.length; i++){
            String str = String.valueOf(Byte.toUnsignedInt(b[i]));
            buff = new StringBuilder();
            buff.append(str);
        }
        String imageEncoded="";
        try {
            imageEncoded= Base64.encodeToString(buff.toString().getBytes("ascii"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Bitmap bmp = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.niilog);

        return imageEncoded;

    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    private byte[] convertVideoToBytes(Uri uri){
        byte[] videoBytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(getRealPathFromURI(getContext(), uri)));

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

            videoBytes = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoBytes;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private void connect(String mqtt_information) {

        String[] info = mqtt_information.split(",", 0);

        String serUrl = getServerURI(info[0], info[1]);
        connect.setText("connecting");
        //serUrlはクライアント名，clientidはMQTTのクライアントID，（通常クライアントの唯一の識別子です），
        // MemoryPersistenceはclientidの保存形式を設定する（ディフォルトはメモリ保存）
        mClient = new MqttAndroidClient(getContext(), serUrl, info[2], new MemoryPersistence());
        //CallBackの設定
        mClient.setCallback(mqttCallbackExtended);
        // connect テスト
        try {
            mClient.connect(getMqttConnectOptions(), "Context", iMqttActionListener);
            Toast.makeText(getContext(), "Connected to Mosquitto MQTT Broker"+serUrl, Toast.LENGTH_LONG).show();

        } catch (MqttException ex) {
            ex.printStackTrace();
        }

    }

    IMqttActionListener iMqttActionListener = new IMqttActionListener() {
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



    public void sendVideoUri(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RESULT_VIDEO) {
            if (resultCode == RESULT_OK) {


//                videoCapturedUriPath = intent.getData().toString();
//                Log.d("debug", "onActivityResult: " + videoCapturedUriPath);
//                Toast.makeText(getActivity(), "Captured video successfully!", Toast.LENGTH_SHORT).show();
//
//                //send video uri to NIIMainActivity
//                sendVideoUri(Uri.parse(videoCapturedUriPath));

                videoCapturedUriPath = intent.getData().toString();
                Log.d("debug", "onActivityResult: " + videoCapturedUriPath);

                sendVideoUri(Uri.parse(videoCapturedUriPath));


            } else {
                Log.d("debug", "videoCapturedUriPath == null");

            }
        } else if(requestCode == RESULT_IMAGE){
            if (resultCode == RESULT_OK) {
                Log.d("Uri", "onActivityResult: " + currentPhotoPath);
                imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                //imageView.setImageBitmap(bitmap); // Test whether bitmap was created
                Toast.makeText(getActivity(), "Captured image successfully!", Toast.LENGTH_SHORT).show();

                //send image uri to NIIMainActivity
            } else {
                Log.d("debug", "imageCapturedUriPath == null");

            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
