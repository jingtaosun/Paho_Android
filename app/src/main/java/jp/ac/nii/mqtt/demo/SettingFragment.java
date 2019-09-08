package jp.ac.nii.mqtt.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;

import jp.ac.nii.mqtt.AccelerometerSensorActivity;
import jp.ac.nii.mqtt.GPSSensorActivity;
import jp.ac.nii.mqtt.MainActivity;
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
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button popTopicButton, popSensorButton, popQosButton, popRetainedButton;
    private PopupMenu popupTopic, popupMessage, popQos, popRetained;
    private EditText mess_topic, mess_dt, qos_dt, retained_dt, gps_dt;
    private  Button publish;
    MqttAndroidClient mClient;


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

        mess_topic = (EditText)rootView.findViewById(R.id.mess_topic);
        mess_dt = (EditText)rootView.findViewById(R.id.mess_dt);
        qos_dt = (EditText)rootView.findViewById(R.id.qos);
        retained_dt = (EditText)rootView.findViewById(R.id.retained);
//        gps_dt = (EditText)rootView.findViewById(R.id.gps_sensor);

        publish = (Button) rootView.findViewById(R.id.publish);


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
//              //   get the base 64 string

                byte[] bytes;
                try {
                    if (mClient.isConnected()) {

                        try {
                            bytes = convertFile(new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20190905_150314.mp4"));
                            mClient.publish("mqtt-android-video",bytes, 1, true);
                        }catch (IOException e) {
                            Log.d(TAG, e.toString());
                        }
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
            }
        });


        Bundle bundle = getArguments();
        String hoge = bundle.getString("hoge");
        String fuga = bundle.getString("fuga");

        Log.d("mclient",hoge+fuga);

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_setting, container, false);
        return rootView;
    }

    private byte[] convertFile(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

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
