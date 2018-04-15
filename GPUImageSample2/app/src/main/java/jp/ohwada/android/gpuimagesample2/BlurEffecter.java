package jp.ohwada.android.gpuimagesample2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageNormalBlendFilter;


/**
 *  class BlurEffecter
 */
public class BlurEffecter {

private Context mContext;


/**
 *  constractor
 */
public BlurEffecter(Context context) {
mContext = context;
} // BlurEffecter


/**
 *  retouchFrostedGlass
 * blur like frosted glass
 *  original https://qiita.com/ralph/items/fc767ecf33a601c5dc55
 */
public  Bitmap retouchFrostedGlass(Bitmap source) {

// filter like frosted glass
GPUImageNormalBlendFilter filter_blend = new GPUImageNormalBlendFilter();
Bitmap bitmap_glass = createFrostedGlass();
filter_blend.setBitmap(bitmap_glass);

// gaussian filter
GPUImageGaussianBlurFilter filter_gaussian = new GPUImageGaussianBlurFilter();

// combine images
GPUImage gpuImage = new GPUImage(mContext);
gpuImage.setImage(source);
gpuImage.setFilter(filter_blend);
gpuImage.setImage(gpuImage.getBitmapWithFilterApplied());
gpuImage.setFilter(filter_gaussian);
filter_gaussian.setBlurSize(10.0f);
gpuImage.setImage(gpuImage.getBitmapWithFilterApplied());
filter_gaussian.setBlurSize(5.0f);
gpuImage.setImage(gpuImage.getBitmapWithFilterApplied());
filter_gaussian.setBlurSize(1.0f);
Bitmap bitmap_filtered = gpuImage.getBitmapWithFilterApplied(); // 

// release memory 
filter_blend.setBitmap(null);
bitmap_glass.recycle();
source.recycle();

return bitmap_filtered;
} // retouchFrostedGlass


/**
 *  createFrostedGlass
 * create a bitmap of frosted glass
 */
private Bitmap createFrostedGlass() {
    Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint();
    paint.setColor(0x30FFFFFF);
    canvas.drawRect(0, 0, 1, 1, paint);
    return bitmap;
} // createFrostedGlass


} // class BlurFilter
