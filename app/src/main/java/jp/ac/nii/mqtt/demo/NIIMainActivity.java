package jp.ac.nii.mqtt.demo;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Toolbar;

import jp.ac.nii.mqtt.R;


public class NIIMainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,SettingFragment.OnFragmentInteractionListener,ResultFragment.OnFragmentInteractionListener{

    private Uri videoUri;
    private String mqttmClient;

    private Toolbar toolbar;

    private HomeFragment homeFragment;
    private SettingFragment settingFragment;
    private ResultFragment resultFragment;


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

                    toolbar.setSubtitle("Home");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment,"HomeFragment").commit();
                    return true;
                case R.id.navigation_settings:

                    if (mqttmClient!= null ) {
                        Bundle argset = new Bundle();
                        argset.putString("mqtt-info", mqttmClient);
                        settingFragment.setArguments(argset);
                    }
                    toolbar.setSubtitle("Setting");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, settingFragment,"SettingFragment").commit();
                    return true;
                case R.id.navigation_results:
                    if (videoUri!= null ) {
                        Bundle args = new Bundle();
                        args.putString("videoCapturedUriPath", videoUri.toString());
                        resultFragment.setArguments(args);
                    }
                    toolbar.setSubtitle("Result");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, resultFragment,"ResultFragment").commit();
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niimain);
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        toolbar.setSubtitle("Home");
        homeFragment = new HomeFragment();
        settingFragment = new SettingFragment();
        resultFragment = new ResultFragment();

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

