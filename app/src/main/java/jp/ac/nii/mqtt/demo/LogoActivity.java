package jp.ac.nii.mqtt.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import jp.ac.nii.mqtt.R;

public class LogoActivity extends Activity {

    private ImageView logoImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        logoImage = findViewById(R.id.logo_image);
        logoImage.setImageResource(R.drawable.niilog);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LogoActivity.this,NIIMainActivity.class);
                startActivity(intent);
            }
        },2000);
    }
}
