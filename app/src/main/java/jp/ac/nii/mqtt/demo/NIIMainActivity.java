package jp.ac.nii.mqtt.demo;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import org.eclipse.paho.android.service.MqttAndroidClient;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import jp.ac.nii.mqtt.R;


public class NIIMainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,SettingFragment.OnFragmentInteractionListener,ResultFragment.OnFragmentInteractionListener{

    private Uri videoUri;
    private String mqttmClient;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
//            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:

//                    Intent intent = getIntent();
//                    Bundle bundle = intent.getExtras();
//
//                    String bird = bundle.getString("mclient");
//                    Log.d("NII",bird);


                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
                    return true;
                case R.id.navigation_settings:
                    SettingFragment setingFragment = new SettingFragment();
                    if (mqttmClient!= null ) {
                        Bundle argset = new Bundle();
                        argset.putString("mqtt-info", mqttmClient);
                        setingFragment.setArguments(argset);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,setingFragment).commit();
                    return true;
                case R.id.navigation_results:
                    ResultFragment resultFragment = new ResultFragment();
                    if (videoUri!= null ) {
                        Bundle args = new Bundle();
                        args.putString("videoCapturedUriPath", videoUri.toString());
                        resultFragment.setArguments(args);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, resultFragment).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niimain);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        videoUri = uri;
    }

    @Override
    public void onFragmentInteractionFromHomeToMain(String mClient) {
        mqttmClient = mClient;
    }
}

