package jp.ac.nii.mqtt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GPSSensorActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private  TextView textView1, textView2;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpssensor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
        }
        else{
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 50, this);

        }
        backButton=findViewById(R.id.back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GPSSensorActivity.this, MainActivity.class);
                intent.putExtra("GPS_value",textView1.getText()+":"+textView2.getText());
                startActivity(intent);
            }
        });


    }

    @Override
    public void onLocationChanged(Location location) {
        // 緯度の表示
        textView1 = (TextView) findViewById(R.id.textView1);
        String str1 = Double.toString(location.getLatitude());
        textView1.setText(str1);

        // 経度の表示
        textView2 = (TextView) findViewById(R.id.textView2);
        String str2 = Double.toString(location.getLongitude());
        textView2.setText(str2);

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
     *
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,  @NonNull String[]permissions,  @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                locationStart();

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void locationStart(){
        Log.d("debug","locationStart()");

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


}
