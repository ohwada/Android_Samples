/**
  * NDK Sample
  * 2019-08-01 K.OHWADA
 * original : https://github.com/android/ndk-samples/tree/master/bitmap-plasma
  */
package jp.ohwada.android.ndk6;


/*
 * Copyright (C) 2010 The Android Open Source Project
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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.Display;
import android.view.WindowManager;


/** 
 *  class MainActivity
 */
//public class Plasma extends Activity
public class MainActivity extends Activity
{

/** 
 *  onCreate
 *  Called when the activity is first created.
 */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        setContentView(new PlasmaView(this, displaySize.x, displaySize.y));
    }

    // load our native library
    static {
        System.loadLibrary("plasma");
    }
}

/** 
 *  class PlasmaView
 *  Custom view for rendering plasma.
 *   Note: suppressing lint wrarning for ViewConstructor since it is
 *  manually set from the activity and not used in any layout.
 */
@SuppressLint("ViewConstructor")
class PlasmaView extends View {
    private Bitmap mBitmap;
    private long mStartTime;

    // implementend by libplasma.so
    private static native void renderPlasma(Bitmap  bitmap, long time_ms);


/** 
 *  constractot
 */
    public PlasmaView(Context context, int width, int height) {
        super(context);
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mStartTime = System.currentTimeMillis();
    }


/** 
 *  onDraw
 */
    @Override 
    protected void onDraw(Canvas canvas) {
        renderPlasma(mBitmap, System.currentTimeMillis() - mStartTime);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        // force a redraw, with a different time-based pattern.
        invalidate();
    }
}
