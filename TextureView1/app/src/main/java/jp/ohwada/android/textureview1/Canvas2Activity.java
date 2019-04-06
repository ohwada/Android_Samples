/**
 * TextureView Sample
 * draw small Rect on canvas
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.textureview1;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.FrameLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Camera2Activity
 * original : https://github.com/dalinaum/TextureViewDemo/tree/master/src/kr/gdg/android/textureview
 */
public class Canvas2Activity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "TextureView";
    	private final static String TAG_SUB = "Canvas2Activity";

	private TextureView mTextureView;

	private RenderThread mThread;


/**
 * onCreate
 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_texture);

		mTextureView = (TextureView) findViewById(R.id.texture_view);
		mTextureView.setSurfaceTextureListener(new CanvasListener());
		mTextureView.setOpaque(false);
	}

/**
 * onCreateOptionsMenu
 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu, menu);
		return true;
	}

/**
 * onOptionsItemSelected
 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.increase_alpha:
			mTextureView.setAlpha(mTextureView.getAlpha() + 0.1f);
			return true;
		case R.id.decrease_alpha:
			mTextureView.setAlpha(mTextureView.getAlpha() - 0.1f);
			return true;
		case R.id.rotate_left:
			mTextureView.setRotation(mTextureView.getRotation() - 5f);
			return true;
		case R.id.rotate_right:
			mTextureView.setRotation(mTextureView.getRotation() + 5f);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 *  class RenderThread
 */
	private class RenderThread extends Thread {
		private volatile boolean isRunning = false;

		@Override
		public void run() {
			float x = 0.0f;
			float y = 0.0f;
			float speedX = 5.0f;
			float speedY = 3.0f;

			Paint paint = new Paint();
			paint.setColor(0xff00ff00);

			while ( isRunning && !Thread.interrupted()) {
				final Canvas canvas = mTextureView.lockCanvas(null);
				try {
					canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
					canvas.drawRect(x, y, x + 20.0f, y + 20.0f, paint);
				} finally {
					mTextureView.unlockCanvasAndPost(canvas);
				}

				if (x + 20.0f + speedX >= mTextureView.getWidth()
						|| x + speedX <= 0.0f) {
					speedX = -speedX;
				}
				if (y + 20.0f + speedY >= mTextureView.getHeight()
						|| y + speedY <= 0.0f) {
					speedY = -speedY;
				}

				x += speedX;
				y += speedY;

				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					// Interrupted
				}
			}
		}

		public void startRendering() {
			 isRunning = true;
			start();
		}

		public void stopRendering() {
			//interrupt();
			 isRunning = false;
		}

	} //  class RenderThread


/**
 * class CanvasListener
 */ 
	private class CanvasListener implements SurfaceTextureListener {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface,
				int width, int height) {
			log_d("onSurfaceTextureAvailable");
			mThread = new RenderThread();
			mThread.startRendering();
		} // onSurfaceTextureAvailable

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			log_d( "onSurfaceTextureDestroyed");
			if (mThread != null) {
				mThread.stopRendering();
			}
			return true;
		} // onSurfaceTextureDestroyed

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
				int width, int height) {
			log_d( "onSurfaceTextureSizeChanged");
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {
			//log_d("onSurfaceTextureUpdated");
		}

	} // class CanvasListener


} // class Canvas2Activity
