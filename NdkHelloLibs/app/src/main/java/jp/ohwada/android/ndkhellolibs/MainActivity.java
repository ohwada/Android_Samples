/**
 * NDK Sample
 * 2019-08-01 K.OHWADA
 * original : https://github.com/googlesamples/android-ndk/tree/master/hello-libs
 */
package jp.ohwada.android.ndkhellolibs;


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


/*
 * class MainActivity 
 *  
 * Simple Java UI to trigger jni function. It is exactly same as Java code
 * in hello-jni.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText( stringFromJNI() );
        tv.setTextColor(Color.BLUE);
        tv.setTextSize(20);
        setContentView(tv);
    }

/**
 *  external link to the native code
 *  app/src/main/cpp/hello-libs.cpp
 */
    public native String  stringFromJNI();

/**
  * use Native Library
  */
    static {
        System.loadLibrary("hello-libs");
    }

} // class MainActivity
