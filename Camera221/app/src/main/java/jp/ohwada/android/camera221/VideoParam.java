/**
 * Camera2 Sample
  * VideoParam
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera221;


/**
  *  class VideoParam
  */
public class VideoParam implements Cloneable {

private int width;
private int height;
private int frameRate;
private int bitRate;
private int interval;

/**
  *  constractor
  */
    public VideoParam(int _width, int _height, int _frame, int _bit, int _interval) {
        width = _width;
        height = _height;
        frameRate = _frame;
        bitRate = _bit;
        interval = _interval;
    }


/**
  *  clone
  */
   public VideoParam clone(){
    VideoParam param = null;
    try {
      param = (VideoParam)super.clone();
    } catch (CloneNotSupportedException ce) {
            // nop
    }
    return param;
  }


/**
  *  setWidth
  */
    public void setWidth(int _width) {
        width = _width;
    }

/**
  *  getWidth
  */
    public int getWidth() {
        return width;
    }


/**
  *  setHeight
  */
    public void setHeight(int _height) {
        height = _height;
    }

/**
  *  getHeight
  */
    public int getHeight() {
        return height;
    }






/**
  *  setFrameRate
  */
    public void setFrameRate(int _rate) {
        frameRate = _rate;
    }

/**
  *  getFrameRate
  */
    public int getFrameRate() {
        return frameRate;
    }

/**
  *  setBitRate
  */
    public void setBitRate(int _rate) {
        bitRate = _rate;
    }

/**
  *  getgBitRate
  */
    public int getBitRate() {
        return bitRate;
    }



/**
  *  setInterval
  */
    public void setIframeInterval(int _interval) {
       interval = _interval;
    }

/**
  *  getInterval
  */
    public int getIframeInterval() {
        return interval;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
          sb.append("VideoParam: ");
          sb.append("width= ");
          sb.append(width);
          sb.append(", height= ");
          sb.append(height);
          sb.append(", FrameRate= ");
          sb.append(frameRate);
          sb.append(", BitRate= ");
          sb.append(bitRate);
          sb.append(", interval= ");
          sb.append(interval);
        return sb.toString();
    }

} // class VideoParam

