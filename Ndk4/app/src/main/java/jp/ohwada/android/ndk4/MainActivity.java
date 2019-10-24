/**
  * NDK Sample
  * 2019-08-01 K.OHWADA
 * original : https://github.com/android/ndk-samples/tree/master/hello-gl2
  */
package jp.ohwada.android.ndk4;


/*
 * Copyright (C) 2007 The Android Open Source Project
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
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;


/** 
 *  class MainActivity
 */
//public class GL2JNIActivity extends Activity {
public class MainActivity extends Activity {

    GL2JNIView mView;


/** 
 *  onCreate
 */
    @Override protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mView = new GL2JNIView(getApplication());
	setContentView(mView);
    }


/** 
 *  onResume
 */
    @Override protected void onResume() {
        super.onResume();
        mView.onResume();
    }


/** 
 * onPause
 */
    @Override protected void onPause() {
        super.onPause();
        mView.onPause();
    }


}
