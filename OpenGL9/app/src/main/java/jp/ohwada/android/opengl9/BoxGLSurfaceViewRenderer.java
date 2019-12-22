/** 
 *  OpenGL ES2.0 Sample
 *  draw Images continuously like slot machine
 *  2019-10-01 K.OHWADA
 * original : http://junkcode.aakaka.com/archives/500
 */
package jp.ohwada.android.opengl9;


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


/** 
 *  class class BoxGLSurfaceViewRenderer
 */
class BoxGLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    // debug
    private final static String TAG = "BoxGLSurfaceViewRenderer";

    private final static  int NUM_TEXTURE = 8;
    private final static int NUM_BOX = 10;

    // scale appropriately to fill the screen size,
    private final static float BOX_WIDTH = 1.2f;

	private Context mContext;
	private ValueSlot	mValueSlot;
	
	private Box	mBox;

	private float mAspect;	// Aspect Ratio
	
	private int[] mTextureIdList = new int[NUM_TEXTURE];


/** 
 *  constractor
 */
	public BoxGLSurfaceViewRenderer(final Context context) {
		this.mContext = context;
	}


/** 
 *   fling
 */	
	public void fling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
        float slotVelocity = -velocityX  / 16000.0f;
		this.mValueSlot.setVelocity(slotVelocity);
	}


/** 
 *  onSurfaceCreated
 */	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		// enable Depth Buffer
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		
		this.loadAllTextures();
		
		this.mValueSlot = new ValueSlot();
		this.mValueSlot.setMaxValue(NUM_BOX);

		mBox = new Box();

	}


/** 
 *  loadAllTextures
 */	
	private void loadAllTextures() {
        // 8 Textures
        // index : 0 - 7
		this.mTextureIdList[0] = this.loadTexture(R.drawable.try_gles2005_img00);
		this.mTextureIdList[1] = this.loadTexture(R.drawable.try_gles2005_img01);
		this.mTextureIdList[2] = this.loadTexture(R.drawable.try_gles2005_img02);
		this.mTextureIdList[3] = this.loadTexture(R.drawable.try_gles2005_img03);
		this.mTextureIdList[4] = this.loadTexture(R.drawable.try_gles2005_img04);
		this.mTextureIdList[5] = this.loadTexture(R.drawable.try_gles2005_img05);
		this.mTextureIdList[6] = this.loadTexture(R.drawable.try_gles2005_img06);
		this.mTextureIdList[7] = this.loadTexture(R.drawable.try_gles2005_img07);
	}
	

/** 
 *  loadTexture
 */	
	private int loadTexture(int res_id) {
		int[] texIds = {-1};
		GLES20.glGenTextures(1, texIds, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

int texId = texIds[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        // load Bitmap
        Resources res = mContext.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, res_id);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // specifya Texture Filter
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		
		bitmap.recycle();

		return texId;
	}
	

/** 
 *  onSurfaceChanged
 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		// set View Port
		GLES20.glViewport(0, 0, width, height);

        // calc Aspect Ratio
		this.mAspect = (float)width / (float)height;
	}


/** 
 *  onDrawFrame
 */
	public void onDrawFrame(GL10 gl) {
		
		this.mValueSlot.frameMove();
		
		// fill Backgroud(Gray)
		GLES20.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] mvMatrix = createModelViewMatrix();

        float[] projMatrix = createProjectionMatrix();

		mBox.draw(projMatrix);

        // scale appropriately to fill the screen size,
		float center = BOX_WIDTH / 2.0f;

        // get the number of the center Box
		float sliderCursor = this.mValueSlot.getCursor();
        int centerBoxNo = adjustBoxNo( (int)(sliderCursor) ); 
		float slideValue = (sliderCursor - centerBoxNo) * BOX_WIDTH;

        // get the number of the left Box
        int  leftBoxNo = adjustBoxNo( centerBoxNo - 1 ); 

        // get the number of the right Box
        int  rightBoxNo = adjustBoxNo( centerBoxNo + 1 ); 


            // draw Rectangle and Texture
			float leftX = -slideValue - BOX_WIDTH + center;
			drawTexture(mvMatrix, leftX, leftBoxNo);

			float centerX =  -slideValue + center;
			drawTexture(mvMatrix, centerX,  centerBoxNo);

			float rightX =   -slideValue + BOX_WIDTH + center;
			drawTexture(mvMatrix, rightX, rightBoxNo);
		
	}


/** 
 *  createProjectionMatrix
 */
private float[] createProjectionMatrix() {

        float PROJ_FOV = 45.0f;
        float PROJ_NEAR = 1.0f;
        float PROJ_FAR = 100.f;

		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
		this.Perspective(matrix, PROJ_FOV, this.mAspect, PROJ_NEAR, PROJ_FAR);
        return matrix;
}


/** 
 *  createModelViewMatrix
 */
private float[] createModelViewMatrix() { 

    int OFFSET = 0;
        float EYE_X = 0.0f;
        float EYE_Y = 0.0f;
        float EYE_Z = 1.0f;
        float CENTER_X = 0.0f;
        float CENTER_Y = 0.0f;
        float CENTER_Z = 0.0f;
        float UP_X = 0.0f;
        float UP_Y = 1.0f;
        float UP_Z = 0.0f;

		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
		Matrix.setLookAtM(matrix, OFFSET,
				EYE_X, EYE_Y, EYE_Z,
				CENTER_X, CENTER_Y, CENTER_Z,
				 UP_X, UP_Y, UP_Z);
        return matrix;
}


/** 
 *  adjustBoxNo
 */
private int adjustBoxNo(int boxNo) {
		if (boxNo < 0) {
            // go round counterclockwise、if outside the Left side
			boxNo += NUM_BOX;
		} else if (NUM_BOX <= boxNo) {
            // go round clockwise、if outside the Right side
			boxNo -= NUM_BOX;
        }
        return boxNo;
}


/** 
 *  drawTexture
 */
private void drawTexture( float[] mvMatrix, float x, int boxNo) {

			float[] matrix = calcTextureMatrix(mvMatrix, x);

            int index = boxNo % NUM_TEXTURE;
			int textureId = this.mTextureIdList[index];

			mBox.drawTexture(matrix, textureId);
}




/** 
 *  calcTextureMatrix
 */
private float[] calcTextureMatrix(float[] mvMatrix, float x) {

        // scale appropriately to fill the screen size
        float SY = 2.0f;
        float SZ = 1.0f;
		float TY = 0.0f;
		float TZ = 0.0f;

		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
        Matrix.scaleM(matrix, 0, BOX_WIDTH, SY, SZ);
        Matrix.translateM(matrix, 0, x, TY, TZ);
		Matrix.multiplyMM(matrix, 0, mvMatrix, 0, matrix, 0);
		return matrix;
}


/** 
 *  Perspective
 *  create Projection matrix
 */
	private void Perspective(float[] out, float fov, float aspect, 
			float near, float far) {

		float top = near * (float)Math.tan(Math.toRadians(fov));
		float bottom = -top;
		float left = bottom * aspect;
		float right = top * aspect;
		Matrix.frustumM(out, 0, left, right, bottom, top, near, far);
	}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    Log.d( TAG, msg );
}



/** 
 *  class ValueSlot
 *  rotate the value like Slot Machine
 */
static class ValueSlot {
	
	public enum SliderState {
		SLIDER_STOP,	// stop state
		SLIDER_MOVE,	// rotating state
		SLIDER_SNAP,	// snap to the center of the nearest value
	}
	
	private float mCursor;	// current cursor position
	private float mVelocity;	// velocity
	private float mBoxW;	// Box width
	private int mMaxValue;	// maximum number of Boxes
	private SliderState mSliderState;	// current slider state
	private float mSnapPos;	// snap position
	
	
/** 
 *  constractor
 */
	ValueSlot() {
		this.mBoxW = 1.0f;
		this.mMaxValue = 1;
		this.mCursor = this.mBoxW / 2.0f;
		this.mVelocity = 0.0f;
		this.mSliderState = SliderState.SLIDER_STOP;
	}
	

/** 
 *  setMaxValue
 *  set the maximum number of Boxes
 */
	public void setMaxValue(int value) {
		this.mMaxValue = value;
	}
	

/** 
 *  setVelocity
 */
	public void setVelocity(float speed) {
		this.mVelocity = speed;
		this.mSliderState = SliderState.SLIDER_MOVE;
	}
	

/** 
 *  getCursor
 *  get cursor position
 */
	public float getCursor() {
		// rotate value within range
		return this.roundValue(this.mCursor);
	}
	

/** 
 *  getBoxCount
 *  returns the number of Box
 */
	public int getBoxCount() {
		return this.mMaxValue;
	}
	
	
/** 
 *  roundValue
 *  rotate the value within the range
 * convert to maxValue if it is 0 or less
 * convert to 0 if maxValue or more
 */
	private float roundValue(float currValue) {


		if (currValue < 0) {
            // move the cursor to the right end
            // when all are in the minus range, 
			return currValue + this.mMaxValue;

		} else if (this.mMaxValue < this.mCursor) {
            // return to the left edge
            // when the size of all images is larger than the combined size, 
			return currValue - this.mMaxValue;
		}

		return currValue;
	}
	

/** 
 *  frameMove
 *  update Cursor position
 */
	void frameMove() {

		// branch processing depending on the state of the slider
		switch (this.mSliderState) {
			case SLIDER_STOP: {
				// rotate value within range
				this.mCursor = this.roundValue(this.mCursor);
			}
			break;
			case SLIDER_MOVE: {
				// move Cursor
				this.mCursor += this.mVelocity;
				this.mVelocity *= 0.99f;

                // the speed has slowed down until the snap starts
				if (Math.abs(this.mVelocity) < 0.01f) {

					float center = this.mBoxW / 2.0f;
					float nearSnapLeft = 0.0f;
					float nearSnapRight = 0.0f;


					if (this.mCursor < 0.f) {
                        // snapping between -1.0-0.0
                        // when gone negative 
						nearSnapLeft = -this.mBoxW + center;
						nearSnapRight = center;

					} else {
						nearSnapLeft = ((int) (this.mCursor / this.mBoxW)) * this.mBoxW + center;
						nearSnapRight = (int) ((this.mCursor / this.mBoxW) + 1) * this.mBoxW + center;
					}

                    // snap closer
					if (Math.abs(nearSnapLeft - this.mCursor) < Math.abs(nearSnapRight - this.mCursor)) {
						this.mSnapPos = nearSnapLeft;
					} else {
						this.mSnapPos = nearSnapRight;
					}

                    // set snap velocity
					this.mVelocity += (this.mSnapPos - this.mCursor) / 40;

                    // move to snap position
					this.mSliderState = SliderState.SLIDER_SNAP;
				} else {
                    // rotate value
					this.mCursor = this.roundValue(this.mCursor);
				}
			}
			break;
			case SLIDER_SNAP: {
				// move Cursor
				this.mCursor += this.mVelocity;
				this.mVelocity *= 0.94f;

				// set  snap velocity
				this.mVelocity += (this.mSnapPos - this.mCursor) / 40;

                // just attach it and finish
                // when almost doesnt move, 
				if (Math.abs(this.mVelocity) < 0.002f && Math.abs(this.mSnapPos - this.mCursor) < 0.002f) {
					this.mCursor = this.mSnapPos;
					this.mVelocity = 0.0f;
					this.mSliderState = SliderState.SLIDER_STOP;
				}
			}
			break;
		} // switch
	}

	} // class ValueSlot

} // class BoxGLSurfaceViewRenderer
