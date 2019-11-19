/**
 * OpenCV Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/opencv/opencv/tree/master/samples/android/15-puzzle
 */
package jp.ohwada.android.opencv44;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import android.util.Log;


/**
 * class Puzzle15Processor
 * This class is a controller for puzzle game.
 * It converts the image from Camera into the shuffled image
 */
public class Puzzle15Processor {

    // debug
	private final static boolean D = true;
    private final static String TAG = "OpenCV";
    private final static String TAG_SUB = "MainActivity";


    private static final int GRID_SIZE = 4;
    private static final int GRID_AREA = GRID_SIZE * GRID_SIZE;
    private static final int GRID_EMPTY_INDEX = GRID_AREA - 1;

    // gray
    private static final Scalar GRID_EMPTY_COLOR = new Scalar(0x33, 0x33, 0x33, 0xFF);


    // CV_FONT_HERSHEY_COMPLEX 
    // normal size serif font
    private static final int TILE_FONT_FACE = 3;

    private static final int TILE_FONT_SCALE = 1;

    // red
    private static final Scalar TILE_FONT_COLOR = new Scalar(255, 0, 0, 255);

    private static final int TILE_FONT_THICKNESS = 2;


    // green
    private static final Scalar GRID_LINE_COLOR = new Scalar(0, 255, 0, 255);

    private static final int GRID_LINE_THICKNESS = 3;


    private int[]   mIndexes;
    private int[]   mTextWidths;
    private int[]   mTextHeights;

    private Mat mRgba15;
    private Mat[] mCells15;
    private boolean mShowTileNumbers = true;


/** 
 *  constractor
 */
    public Puzzle15Processor() {
        mTextWidths = new int[GRID_AREA];
        mTextHeights = new int[GRID_AREA];

        mIndexes = new int [GRID_AREA];

        for (int i = 0; i < GRID_AREA; i++)
            mIndexes[i] = i;
    }


    /* this method is intended to make processor prepared for a new game */
    public synchronized void prepareNewGame() {
        do {
            shuffle(mIndexes);
        } while (!isPuzzleSolvable());
    }


    /* This method is to make the processor know the size of the frames that
     * will be delivered via puzzleFrame.
     * If the frames will be different size - then the result is unpredictable
     */
    public synchronized void prepareGameSize(int width, int height) {
        mRgba15 = new Mat(height, width, CvType.CV_8UC4);
        mCells15 = new Mat[GRID_AREA];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int k = i * GRID_SIZE + j;
                mCells15[k] = mRgba15.submat(i * height / GRID_SIZE, (i + 1) * height / GRID_SIZE, j * width / GRID_SIZE, (j + 1) * width / GRID_SIZE);
            }
        }

        for (int i = 0; i < GRID_AREA; i++) {
            String text = Integer.toString(i + 1);
            Size s = Imgproc.getTextSize(text, TILE_FONT_FACE, TILE_FONT_SCALE, TILE_FONT_THICKNESS, null);
            mTextHeights[i] = (int) s.height;
            mTextWidths[i] = (int) s.width;
        }
    }



    /* this method to be called from the outside. it processes the frame and shuffles
     * the tiles as specified by mIndexes array
     */
    public synchronized Mat puzzleFrame(Mat inputPicture) {
        Mat[] cells = new Mat[GRID_AREA];
        int rows = inputPicture.rows();
        int cols = inputPicture.cols();

        rows = rows - rows%4;
        cols = cols - cols%4;

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int k = i * GRID_SIZE + j;
                cells[k] = inputPicture.submat(i * inputPicture.rows() / GRID_SIZE, (i + 1) * inputPicture.rows() / GRID_SIZE, j * inputPicture.cols()/ GRID_SIZE, (j + 1) * inputPicture.cols() / GRID_SIZE);
            }
        }

        rows = rows - rows%4;
        cols = cols - cols%4;

        // copy shuffled tiles
        for (int i = 0; i < GRID_AREA; i++) {
            int idx = mIndexes[i];
            if (idx == GRID_EMPTY_INDEX)
                mCells15[i].setTo(GRID_EMPTY_COLOR);
            else {
                cells[idx].copyTo(mCells15[i]);
                if (mShowTileNumbers) {
                    String text = Integer.toString(1 + idx);
                    // point of the bottom-left corner
                    double x = (cols / GRID_SIZE - mTextWidths[idx]) / 2;
                    double y = (rows / GRID_SIZE + mTextHeights[idx]) / 2;
                    Point point = new Point(x, y);
                    Imgproc.putText(mCells15[i], text, point, TILE_FONT_FACE, TILE_FONT_SCALE, TILE_FONT_COLOR, TILE_FONT_THICKNESS);
                }
            }
        }

        for (int i = 0; i < GRID_AREA; i++)
            cells[i].release();

        drawGrid(cols, rows, mRgba15);

        return mRgba15;
    }


/** 
 *  toggleTileNumbers
 */
    public void toggleTileNumbers() {
        mShowTileNumbers = !mShowTileNumbers;
    }


/** 
 *  deliverTouchEvent
 */
    public void deliverTouchEvent(int x, int y) {
        int rows = mRgba15.rows();
        int cols = mRgba15.cols();

        int row = (int) Math.floor(y * GRID_SIZE / rows);
        int col = (int) Math.floor(x * GRID_SIZE / cols);

        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            Log.e(TAG, "It is not expected to get touch event outside of picture");
            return ;
        }

        int idx = row * GRID_SIZE + col;
        int idxtoswap = -1;

        // left
        if (idxtoswap < 0 && col > 0)
            if (mIndexes[idx - 1] == GRID_EMPTY_INDEX)
                idxtoswap = idx - 1;
        // right
        if (idxtoswap < 0 && col < GRID_SIZE - 1)
            if (mIndexes[idx + 1] == GRID_EMPTY_INDEX)
                idxtoswap = idx + 1;
        // top
        if (idxtoswap < 0 && row > 0)
            if (mIndexes[idx - GRID_SIZE] == GRID_EMPTY_INDEX)
                idxtoswap = idx - GRID_SIZE;
        // bottom
        if (idxtoswap < 0 && row < GRID_SIZE - 1)
            if (mIndexes[idx + GRID_SIZE] == GRID_EMPTY_INDEX)
                idxtoswap = idx + GRID_SIZE;

        // swap
        if (idxtoswap >= 0) {
            synchronized (this) {
                int touched = mIndexes[idx];
                mIndexes[idx] = mIndexes[idxtoswap];
                mIndexes[idxtoswap] = touched;
            }
        }
    }


/** 
 *  drawGrid
 */
    private void drawGrid(int cols, int rows, Mat drawMat) {
        for (int i = 1; i < GRID_SIZE; i++) {
            // horizontal line
            Point pth1 = new Point(0, i * rows / GRID_SIZE);
            Point pth2 = new Point(cols, i * rows / GRID_SIZE);
            Imgproc.line(drawMat, pth1, pth2, GRID_LINE_COLOR, GRID_LINE_THICKNESS);

            // vertical line
            Point ptv1 =new Point(i * cols / GRID_SIZE, 0);
            Point ptv2 =new Point(i * cols / GRID_SIZE, rows);
            Imgproc.line(drawMat, ptv1, ptv2, GRID_LINE_COLOR, GRID_LINE_THICKNESS);
        }
    }


/** 
 *  shuffle
 */
    private static void shuffle(int[] array) {
        for (int i = array.length; i > 1; i--) {
            int temp = array[i - 1];
            int randIx = (int) (Math.random() * i);
            array[i - 1] = array[randIx];
            array[randIx] = temp;
        }
    }


/** 
 *  isPuzzleSolvable
 */
    private boolean isPuzzleSolvable() {

        int sum = 0;
        for (int i = 0; i < GRID_AREA; i++) {
            if (mIndexes[i] == GRID_EMPTY_INDEX)
                sum += (i / GRID_SIZE) + 1;
            else {
                int smaller = 0;
                for (int j = i + 1; j < GRID_AREA; j++) {
                    if (mIndexes[j] < mIndexes[i])
                        smaller++;
                }
                sum += smaller;
            }
        }
        return sum % 2 == 0;
    }


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} 


} // class Puzzle15Processor
