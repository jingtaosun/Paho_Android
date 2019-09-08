package jp.ac.nii.mqtt.demo;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import jp.ac.nii.mqtt.MainActivity;
import jp.ac.nii.mqtt.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button connect,disconnect;
    private EditText host, port, clientId;
    MqttAndroidClient mClient;

    private static final String TAG = "HomeFragment";


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_home, container, false);

        connect = (Button)rootView.findViewById(R.id.connect);
        disconnect = (Button)rootView.findViewById(R.id.disconnect);

        host = (EditText)rootView.findViewById(R.id.ip_text);
        port = (EditText)rootView.findViewById(R.id.port_text);
        clientId = (EditText)rootView.findViewById(R.id.clientid_text);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    Log.d("mclient", "hello"+check());
                    connect();

                }
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

        return rootView;
//        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private boolean check() {
        if (TextUtils.isEmpty(host.getText().toString())) {
            Toast.makeText(getContext(), "host can't be empty ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(port.getText().toString())) {
            Toast.makeText(getContext(), "port can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(clientId.getText().toString())) {
            Toast.makeText(getContext(), "clientIds can't be empty", Toast.LENGTH_SHORT).show();
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

    private void connect() {
        String serUrl = getServerURI(host.getText().toString(), port.getText().toString());
        connect.setText("connecting");
        //serUrlはクライアント名，clientidはMQTTのクライアントID，（通常クライアントの唯一の識別子です），
        // MemoryPersistenceはclientidの保存形式を設定する（ディフォルトはメモリ保存）
        mClient = new MqttAndroidClient(getContext(), serUrl, clientId.getText().toString(), new MemoryPersistence());
        //CallBackの設定
        mClient.setCallback(mqttCallbackExtended);

        Log.d("mclient", mClient.getClientId());
        // connect テスト
        try {
            mClient.connect(getMqttConnectOptions(), "Context", iMqttActionListener);
            Toast.makeText(getContext(), "Connected to Mosquitto MQTT Broker"+serUrl, Toast.LENGTH_LONG).show();

            Bundle bundle = new Bundle();
            bundle.putString("hoge", "hoge");
            bundle.putString("fuga", "fuga");

            HomeFragment fragment = new HomeFragment();
            fragment.setArguments(bundle);

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







    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
