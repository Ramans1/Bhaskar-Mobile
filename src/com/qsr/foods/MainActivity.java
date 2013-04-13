/**
 * @author V.Bhaskar Reddy
 * @Desc   This activity to load index.html from assets.
 * 
 */

package com.qsr.foods;

import org.apache.cordova.DroidGap;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends DroidGap {

    
    // below variable for calendar instance
    public Calendar cal;
    
    // below variable for "CustomHttpClient" instance
    public CustomHttpClient httpClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /**
         * Here hiding status bar
         * 
         */
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        super.init();
        /**
         * setting screen orientation to only portrait.
         */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /**
         * Creating instances for required objects.
         */
        cal = new Calendar(MainActivity.this, appView);
        httpClient = new CustomHttpClient(appView);
        
        /**
         * Adding instances to javascript interface
         */
        appView.addJavascriptInterface(cal, "calender");
        appView.addJavascriptInterface(httpClient, "httpclient");
        
        
        
        /**
         * loading index.html
         */
        
       // super.splashscreen = R.drawable.splash_screen;
       super.loadUrlTimeoutValue = 0;
        super.loadUrl("file:///android_asset/www/index.html",10);
        
    }
    

}
