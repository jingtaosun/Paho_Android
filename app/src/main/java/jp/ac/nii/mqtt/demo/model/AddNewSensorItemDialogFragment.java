package jp.ac.nii.mqtt.demo.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import jp.ac.nii.mqtt.R;


public class AddNewSensorItemDialogFragment extends DialogFragment {


    private EditText nameEditText;
    private Spinner topicSpinner;
    private Spinner typeSpinner;
    private Spinner qosSpinner;
    private Switch retainedSwitch;

    private int position;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_new_sensor_item, null);

        nameEditText = view.findViewById(R.id.sensor_name_edit_text);
        topicSpinner = view.findViewById(R.id.sensor_topic_spinner);
        typeSpinner = view.findViewById(R.id.sensor_type_spinner);
        qosSpinner = view.findViewById(R.id.qos_spinner);
        retainedSwitch = view.findViewById(R.id.retained_switch);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            nameEditText.setText(bundle.getString("name"));
            topicSpinner.setSelection(bundle.getInt("topic"));
            typeSpinner.setSelection(bundle.getInt("type"));
            qosSpinner.setSelection(bundle.getInt("qos"));
            retainedSwitch.setChecked(bundle.getBoolean("retained"));
            position = bundle.getInt("position");

            builder.setView(view)
                    .setTitle("Edit this sensor item")
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getDialog().dismiss();
                        }
                    })
                    .setNeutralButton("delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("action", "delete");
                            passData("delete");
                            getDialog().dismiss();
                        }
                    })
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            passData("edit");
                        }
                    });

        } else {
            builder.setView(view)
                    .setTitle("Add a new sensor item")
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getDialog().dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            passData("add");
                        }
                    });
        }

        return builder.create();
    }

    private void passData(String action) {
        Intent intent = new Intent();
        intent.putExtra("action", action);
        if (action.equals("delete")) {
            intent.putExtra("position", position);
        } else {
            intent.putExtra("name", nameEditText.getText().toString());
            intent.putExtra("topic", topicSpinner.getSelectedItem().toString());
            intent.putExtra("type", typeSpinner.getSelectedItem().toString());
            intent.putExtra("qos", Integer.parseInt(qosSpinner.getSelectedItem().toString()));
            intent.putExtra("retained", retainedSwitch.isChecked());
            if (action.equals("edit")) {
                intent.putExtra("position", position);
            }
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        getDialog().dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
//            listener = (AddNewSensorListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement AddNewSensorListener");
        }
    }

}
