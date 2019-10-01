/**
 * Camera2 Sample
  * VideoParam
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera221;


import java.util.ArrayList;
import java.util.List;

/**
  *  class VideoParamUtil
  */
public class VideoParamUtil {

 // reference : https://developer.android.com/guide/topics/media/media-formats#video-formats

    private static final int FRAME_RATE = 30; // 30fps
    private static final int IFRAME_INTERVAL = 10;

    // VGA
    private final static int VGA_WIDTH = 640;
    private final static int VGA_HEIGHT = 480;

    // 640*480*30*0.125 = 1152000
    private static final int VGA_BIT_RATE = 1200000; // 1.2Mbps


    // HD (720p)
    private final static int HD_WIDTH = 1280;
    private final static int HD_HEIGHT = 720;
    private static final int  HD_BIT_RATE = 3500000; // 3.5Mbps


    // SD360p
    private final static int SD_WIDTH = 480;
    private final static int SD_HEIGHT = 360;
    private static final int  SD_BIT_RATE = 650000; // 650Kbps


/**
  *  constractor
  */
    public VideoParamUtil() {
        // nop
    }

/**
  *  getList
  */
    public static List<VideoParam> getList() {

        VideoParam vgaParam = new VideoParam(VGA_WIDTH, VGA_HEIGHT, FRAME_RATE, VGA_BIT_RATE, IFRAME_INTERVAL);

        VideoParam hdParam = vgaParam.clone();
        hdParam.setWidth(HD_WIDTH);
        hdParam.setHeight(HD_HEIGHT);
        hdParam.setBitRate(HD_BIT_RATE);

        VideoParam sdParam = vgaParam.clone();
        sdParam.setWidth( SD_WIDTH);
        sdParam.setHeight( SD_HEIGHT);
        sdParam.setBitRate( SD_BIT_RATE);

        List<VideoParam> list = new ArrayList<VideoParam>();
        list.add(hdParam);
        list.add(vgaParam);
        list.add(sdParam);
        return list;
    }

} // class VideoParamUtil

