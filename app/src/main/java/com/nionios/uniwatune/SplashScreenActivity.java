package com.nionios.uniwatune;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        /*Add a pulsing animation on head phone graphic*/
        ImageView headPhones = (ImageView) findViewById(R.id.headPhones);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        headPhones.startAnimation(pulse);
        /* Display the splash screen for 1 second, then move on to MainActivity */
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, 1*1000);
    }
}