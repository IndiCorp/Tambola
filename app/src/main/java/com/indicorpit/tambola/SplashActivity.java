package com.indicorpit.tambola;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.indicorpit.tambola.databinding.ActivitySplashBinding;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding activitySplashBinding;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        activitySplashBinding=ActivitySplashBinding.inflate ( getLayoutInflater () );
        setContentView ( activitySplashBinding.getRoot () );



        new Timer (  ).scheduleAtFixedRate (
                new TimerTask ( ) {
            @Override
            public void run () {
                runOnUiThread( () -> {
                    if(activitySplashBinding.logo1ImageView.getVisibility ()== View.VISIBLE)
                    {
                        activitySplashBinding.logo1ImageView.setVisibility ( View.INVISIBLE );
                        activitySplashBinding.logo2ImageView.setVisibility ( View.VISIBLE );
                    }else
                    {
                        activitySplashBinding.logo1ImageView.setVisibility ( View.VISIBLE );
                        activitySplashBinding.logo2ImageView.setVisibility ( View.INVISIBLE );
                    }
                } );
            }
        } , 2000 , 100 );

        new Handler ( Looper.getMainLooper () ).postDelayed ( () -> {
            startActivity ( new Intent ( SplashActivity.this,MainActivity.class ) );
            SplashActivity.this.finish ();
//        } , 10);

        } , 5000);

    }



}