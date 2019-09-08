package jp.ac.nii.mqtt.demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import jp.ac.nii.mqtt.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String PLAYING_URI_KEY = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private VideoView ori_videoView, pro_videoView;
    private TextView ori_text, pro_text;

    private OnFragmentInteractionListener mListener;

    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ResultFragment newInstance(String uri) {

        Bundle args = new Bundle();

        ResultFragment fragment = new ResultFragment();
        args.putString(PLAYING_URI_KEY,uri);
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
//        return inflater.inflate(R.layout.fragment_result, container, false);
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_result, container, false);

        ori_text = (TextView)rootView.findViewById(R.id.ori_textview);
        pro_text = (TextView)rootView.findViewById(R.id.pro_textview);


        ori_videoView = (VideoView) rootView.findViewById(R.id.ori_videoView);

        MediaController mediaController = new MediaController(ori_videoView.getContext());
        mediaController.setAnchorView(ori_videoView);

        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/VID_20190905_150314.mp4");

        //Setting MediaController and URI, then starting the videoView
        ori_videoView.setMediaController(mediaController);
        ori_videoView.setVideoURI(uri);
        ori_videoView.requestFocus();
        ori_videoView.start();


        pro_videoView = (VideoView) rootView.findViewById(R.id.pro_videoView);

        MediaController mediaController_pro = new MediaController(pro_videoView.getContext());
        mediaController.setAnchorView(pro_videoView);

        Uri pro_uri=Uri.parse(
//                PLAYING_URI_KEY);
                Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/" +"VID_20190905_150314.mp4");
//                        "VID_20190906_183239.mp4");

        //Setting MediaController and URI, then starting the videoView
        pro_videoView.setMediaController(mediaController_pro);
        pro_videoView.setVideoURI(pro_uri);
        pro_videoView.requestFocus();
        pro_videoView.start();

        return rootView;

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
