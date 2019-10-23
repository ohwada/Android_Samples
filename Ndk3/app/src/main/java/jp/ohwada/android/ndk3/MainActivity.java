/**
  * NDK Sample
  * 2019-08-01 K.OHWADA
 * original : https://github.com/android/ndk-samples/tree/master/hello-jniCallback
  */
package jp.ohwada.android.ndk3;


/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;


/** 
 *  class MainActivity
 */
public class MainActivity extends Activity {

    int hour = 0;
    int minute = 0;
    int second = 0;
    TextView tickView;


/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tickView = (TextView) findViewById(R.id.tickView);
        tickView.setTextColor(Color.BLUE);
        tickView.setTextSize(20);

    }


/** 
 *  onResume
 */
    @Override
    public void onResume() {
        super.onResume();
        hour = minute = second = 0;
        TextView tv = (TextView)findViewById(R.id.hellojniMsg);
        tv.setText(stringFromJNI());
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(18);
        startTicks();
    }


/** 
 *  onPause
 */
    @Override
    public void onPause () {
        super.onPause();
        StopTicks();
    }


    /*
     * A function calling from JNI to update current timer
     */
    //@Keep
    private void updateTimer() {
        ++second;
        if(second >= 60) {
            ++minute;
            second -= 60;
            if(minute >= 60) {
                ++hour;
                minute -= 60;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String ticks = "" + MainActivity.this.hour + ":" +
                        MainActivity.this.minute + ":" +
                        MainActivity.this.second;
                MainActivity.this.tickView.setText(ticks);
            }
        }); // runOnUiThread
    }


    static {
        System.loadLibrary("hello-jnicallback");
    }
    public native  String stringFromJNI();
    public native void startTicks();
    public native void StopTicks();
}
