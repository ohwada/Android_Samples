/**
 * Camera2 Sample
 * Shutter Sound
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera29.util;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


/**
 * SoundUtil
 */
public class SoundUtil {

    private static final  int MAX_STREAMS = 1;

    private static final  int DEFAULT_SRC_QUALITY = 0;

    private static final  int LOAD_PRIORITY = 0;

    private static final float PLAY_LEFT_VOLUME = 1.0f;

    private static final float PLAY_RIGHT_VOLUME = 1.0f;

    private static final int PLAY_PRIORITY = 0;

    private static final int PLAY_NO_LOOP = 0;

    private static final float PLAY_NORMAL_PLAYBACK = 1.0f;


    private Context mContext;

    private SoundPool mSoundPool;

    private int mSoundId;    


/**
 * constractor
 */ 
public  SoundUtil(Context context) {
    mContext = context;

    mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, DEFAULT_SRC_QUALITY);
}


/**
 * load
 */ 
public int load(int res_id) {
    int sound_id = mSoundPool.load(mContext, res_id, LOAD_PRIORITY);
    mSoundId = sound_id;
    return sound_id;
}


/**
 * release
 */ 
public void release() {
    mSoundPool.release();
}


/**
 * play
 */ 
public int play() {
    return playSound(mSoundId);
}


/**
 * play with sound_id
 */ 
public int play(int sound_id) {
    return playSound(sound_id);
}


/**
 * playSound
 */ 
private int playSound(int sound_id) {
    int stream_id = mSoundPool.play(sound_id, PLAY_LEFT_VOLUME, PLAY_RIGHT_VOLUME, PLAY_PRIORITY, PLAY_NO_LOOP, PLAY_NORMAL_PLAYBACK);
    return stream_id;
}


} // class SoundUtil
