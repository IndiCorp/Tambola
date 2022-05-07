package com.indicorp.tambola;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.indicorp.tambola.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding activitySplashBinding;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        activitySplashBinding=ActivitySplashBinding.inflate ( getLayoutInflater () );
        setContentView ( activitySplashBinding.getRoot () );

        String path = "android.resource://" + getPackageName() + "/" + R.raw.indicorp_video;
        activitySplashBinding.splashVideoView.setVideoURI(Uri.parse(path));
        activitySplashBinding.splashVideoView.setOnPreparedListener( mp -> {
            mp.setLooping(false);
            activitySplashBinding.splashVideoView.start();
        } );

        new Handler ( Looper.getMainLooper () ).postDelayed ( () -> {
            startActivity ( new Intent ( SplashActivity.this,MainActivity.class ) );
            SplashActivity.this.finish ();
//        } , 10);

        } , 8000);

    }



}