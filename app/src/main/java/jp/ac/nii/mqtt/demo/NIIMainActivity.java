package jp.ac.nii.mqtt.demo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import jp.ac.nii.mqtt.R;


public class NIIMainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,SettingFragment.OnFragmentInteractionListener,ResultFragment.OnFragmentInteractionListener{

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
                    return true;
                case R.id.navigation_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingFragment()).commit();
                    break;
                case R.id.navigation_results:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ResultFragment()).commit();
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

    }
}

