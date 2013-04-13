/*package com.qsr.foods;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class SplashScreen extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.splash_screeen);
        
        Log.d("Splash Activity", "Splash Started");

        *//** set time to splash out *//*
        final int welcomeScreenDisplay = 5000;
        *//** create a thread to show splash up to splash time *//*
        Thread welcomeThread = new Thread() {
            
            int wait = 0;
            
            @Override
            public void run() {
                try {
                    super.run();
                    *//**
                     * use while to get the splash time. Use sleep() to increase
                     * the wait variable for every 100L.
                     *//*
                    while (wait < welcomeScreenDisplay) {
                        sleep(100);
                        wait += 100;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    *//**
                     * Called after splash times up. Do some action after splash
                     * times up. Here we moved to another main activity class
                     *//*
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                    finish();
                }
            }
        };
        welcomeThread.start();
        
    }
}
*/