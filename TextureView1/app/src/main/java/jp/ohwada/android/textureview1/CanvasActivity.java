/**
 * TextureView Sample
 * drawPath on canvas
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.textureview1;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
 * CanvasActivity
 * original : https://github.com/dalinaum/TextureViewDemo/tree/master/src/kr/gdg/android/textureview
 */
public class CanvasActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "TextureView";
    	private final static String TAG_SUB = "CanvasActivity";

	private TextureView mTextureView;
	private RenderThread mThread;
	private int mWidth;
	private int mHeight;

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
 * class RenderThread
 */
	private class RenderThread extends Thread {
		private volatile boolean isRunning = false;
		private int sx, sy, ex, ey;
		private boolean sxToRight, syToBottom;
		private boolean exToRight, eyToBottom;

		@Override
		public void run() {
			Paint paint = new Paint();
			paint.setColor(0xff00ff00);
			paint.setColor(Color.RED);
			
			sx = (int) (Math.random() * mWidth);
			sy = (int) (Math.random() * mHeight);
			ex = (int) (Math.random() * mWidth);
			ey = (int) (Math.random() * mHeight);

			while (isRunning && !Thread.interrupted()) {
				final Canvas canvas = mTextureView.lockCanvas(null);
				try {
					canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);

					
					int strokeWidth = 5;
					paint.setStrokeWidth(strokeWidth);
					paint.setStyle(Paint.Style.STROKE);
					
					Path path = new Path();
					path.moveTo(sx, sy);
					path.lineTo(ex, ey);
					
					canvas.drawPath(path, paint);
					
				} finally {
					mTextureView.unlockCanvasAndPost(canvas);
				}
				
				if (sxToRight) {
					sx += 3;
					if (sx >= mWidth) {
						sxToRight = false;
					}
				} else {
					sx -= 3;
					if (sx < 0) {
						sxToRight = true;
					}
				}

				if (syToBottom) {
					sy += 3;
					if (sy >= mHeight) {
						syToBottom = false;
					}
				} else {
					sy -= 3;
					if (sy < 0) {
						syToBottom = true;
					}
				}
				
				if (exToRight) {
					ex += 3;
					if (ex >= mWidth) {
						exToRight = false;
					}
				} else {
					ex -= 3;
					if (ex < 0) {
						exToRight = true;
					}
				}

				if (eyToBottom) {
					ey++;
					if (ey >= mHeight) {
						eyToBottom = false;
					}
				} else {
					ey--;
					if (ey < 0) {
						eyToBottom = true;
					}
				}
				
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

	} // class RenderThread


/**
 * class CanvasListener
 */
	private class CanvasListener implements SurfaceTextureListener {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface,
				int width, int height) {
			Log.d(TAG, "onSurfaceTextureAvailable");
			mThread = new RenderThread();
			mThread.startRendering();
			mWidth = mTextureView.getWidth();
			mHeight = mTextureView.getHeight();
			log_d("width: " + mWidth + " height: " + height);
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
			log_d("onSurfaceTextureSizeChanged");
			mWidth = mTextureView.getWidth();
			mHeight = mTextureView.getHeight();
		} // onSurfaceTextureSizeChanged

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {
			//log_d("onSurfaceTextureUpdated");
		}

	} // class CanvasListener

} // class CanvasActivity

