package jp.ac.nii.mqtt.demo.model;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import jp.ac.nii.mqtt.R;
import jp.ac.nii.mqtt.demo.NIIMainActivity;
import jp.ac.nii.mqtt.demo.SettingFragment;

public class SensorItemAdapter extends ArrayAdapter<SensorItem> {

    private NIIMainActivity activity;
    private List<SensorItem> sensorItems;
    private LayoutInflater mInflater;

    public SensorItemAdapter(@NonNull Activity activity, List<SensorItem> sensorItems) {
        super(activity, R.layout.sensor_list_item, sensorItems);
        // TODO Auto-generated constructor stub

        this.activity = (NIIMainActivity) activity;
        this.sensorItems = sensorItems;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View mView;

        if (convertView != null) {
            mView = convertView;
        }
        else {
            mView = mInflater.inflate(R.layout.sensor_list_item, null);
        }

        // リストビューに表示する要素を取得
        final SensorItem item = sensorItems.get(position);

        TextView nameText = mView.findViewById(R.id.sensor_item_name);
        TextView topicText = mView.findViewById(R.id.sensor_item_topic);
        TextView typeText = mView.findViewById(R.id.sensor_item_type);
        ImageButton detailButton = mView.findViewById(R.id.sensor_item_detail_button);

        nameText.setText(item.getSensorName());
        topicText.setText(item.getSensorTopic());
        typeText.setText(item.getSensorType());

        Log.d("detail", "getView: "+ activity.getLocalClassName());

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("detail", "onClick: clicked at No."+ position);
                AddNewSensorItemDialogFragment newListenerDialogFragment = new AddNewSensorItemDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position",position);
                bundle.putString("name", item.getSensorName());
                bundle.putString("topic", item.getSensorTopic());
                bundle.putString("type", item.getSensorType());
                bundle.putInt("qos", item.getQos());
                bundle.putBoolean("retained", item.isRetained());
                newListenerDialogFragment.setArguments(bundle);
                Log.d("sensor", "onClick: "+ item.toString());
                //Log.d("fragment", "onClick: " + activity.getSupportFragmentManager().getFragments().get(0).getTag());
                newListenerDialogFragment.setTargetFragment(activity.getSupportFragmentManager().findFragmentByTag("SettingFragment"), SettingFragment.DIALOG_FRAGMENT);
                newListenerDialogFragment.show(activity.getSupportFragmentManager().beginTransaction(), "add new listener dialog");
            }
        });

        return mView;
    }
}
