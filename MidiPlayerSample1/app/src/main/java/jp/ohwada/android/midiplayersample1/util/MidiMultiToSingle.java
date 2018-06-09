/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * MultiToSingle
 * マルチトラックをシングルトラックに変換する
 */
public class MidiMultiToSingle {

	// debug
	private final static boolean D = true;
	private final static String TAG = "MidiMultiToSingle";

	// シングルトラックのMIDIメッセージのリスト
	private List<MidiMessage> mSingleList = null;

	// 曲のテンポ ( デルタタイムの補正に使用する )		
	private long mTempo = 1;

	// トラック毎の処理をするクラスのリスト
	private List<TrackProcedure> mTrackList = null;
       		
	/*
	 * コンストラクタ
	 */
	public MidiMultiToSingle() {
		// dummy
	}

	/*
	 * 変換する
	 * @oaram List<ParseList> list
	 */    
	public void convert( List<MidiParseMessageList> list ) {
		log_d( "convert " + list.size() );
		mSingleList = new ArrayList<MidiMessage>();
		// トラック毎のMIDIメッセージを TrackProcedure に代入する
		mTrackList = new ArrayList<TrackProcedure>();
		for ( int i=0; i<list.size(); i ++ ) {
			mTrackList.add( new TrackProcedure( list.get( i ) ) );
		}
		// TrackProcedure の処理をする
    	while( true ) {
    		// 全ての MIDIメッセージ が処理されたときは、終了する
    		if ( isAllEnd() ) break;
    		// トラック毎にMIDIメッセージを処理する
			for ( int i=0; i<mTrackList.size(); i++ ) {
    			procTrack( i );
    		}	
    	}
	}

	/*
	 * シングルトラックのMIDIメッセージのリスト を取得する
	 * @oaram List<MidiMessage>
	 */ 
	public List<MidiMessage> getList() { 
		return mSingleList;
	}

	/*
	 * トラック毎にMIDIメッセージを処理する
	 * @oaram int num トラック番号
	 */ 
	private void procTrack( int num ) {
		TrackProcedure track = mTrackList.get( num );
		// object がないときは、終了する
		if ( track == null ) return;
		// 全てのMIDIメッセージを処理したときは、終了する
		if ( track.isEnd() ) return;
		// デルタタイムが残っていれば、いったん終了する		
		if ( track.checkDeltaTime() ) return;
		MidiParseMessage mes = null;
		long time = 0;
		while( true ) {
			// 全てのMIDIメッセージを処理したときは、終了する
			if ( track.isEnd() ) break;
			mes = track.get(); 
			// デルタタイム (null) のときは、いったん終了する
			if ( mes == null ) break;				
			if ( mes.status == MidiParseMessage.STATUS_TEMPO ) {
				// テンポのときは、テンポを変更する
				mTempo = getTempo( mes.bytes );
			} else {
				// MIDIメッセージのときは、演奏時間を計算する
				time = track.getPlayTime( mTempo );
				// シングルトラックに追加する
				mSingleList.add( new MidiMessage( num, mes.status, mes.bytes, time ));
			}
		}	
	}
	
	/*
	 * 全てのMIDIメッセージを処理したかの判定
	 * @return boolean
	 */ 
	private boolean isAllEnd() {
		TrackProcedure track = null;
		// トラック毎に終了を確認する
		for ( int i=0; i<mTrackList.size(); i++ ) {
			track = mTrackList.get( i );
			// １つでも終了していないときは、false を返送する
			if ( !track.isEnd() ) return false;
		}
		// 全てのトラックで終了しているときは、true を返送する
		return true;
    }	

	/*
	 * テンポを取得する
	 * @param byte[] bytes
	 * @return long
	 */
	private long getTempo( byte[] bytes ) {
		// bytes[0] 0xFF
		// bytes[1] 0x51
		int len = 0x00ff & bytes[2];	 
		long tempo = 0;
		for ( int i=0; i<len; i++ ) {
			tempo = ( tempo << 8 ) | ( bytes[ i + 3 ] & 0x00ff );
		}
		return tempo ;	
	}
 		   	
	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, msg );
	}

	/**
	 * トラック単位のMIDI メッセージを処理するクラス
	 */
	private class TrackProcedure {

		// MIDIメッセージのリスト
		private MidiParseMessageList mList = null;

   		// リストの処理中の位置を示すポインタ
		private int mPointer = 0;

		// デルタタイム
		private long mDeltaTime = 0;
		
		// デルタタイムを残りを示すカウンタ
		private long mTimeCount = 0;
		
		// 演奏時間
		private long mPlayTime = 0 ; 

		/*
		 * コンストラクタ
		 * @param ParseList list
		 */			
		public TrackProcedure( MidiParseMessageList list ) {
			mList = list;
			mPointer = 0;
			mPlayTime = 0 ; 
		}

		/*
		 * MIDI メッセージを取り出す
		 * @return ParseMessage	 
		 */
		public MidiParseMessage get() {
			MidiParseMessage r = mList.get( mPointer );
			mPointer ++;
			if ( r.status == MidiParseMessage.STATUS_DELTA_TIME ) {
				// デルタタイムのときは、変数に値を設定する
				mDeltaTime = r.deltatime;
				mTimeCount = r.deltatime;
				// null を返送する
				return null;
			}
			// MIDIメッセージを返送する
			return r;
		}

		/*
		 * MIDIメッセージを最後まで処理したかの判定
		 * return boolean
		 */ 				
		public boolean isEnd() {
			if ( mPointer >= mList.size() ) {
				return true;
			}
			return false;
		}
		
		/*
		 * デルタタイムが残っているかの判定
		 * return boolean
		 */ 
		public boolean checkDeltaTime() {
			// 呼ばれるたびに、カウンタを１つ減らす
			if ( mTimeCount > 0 ) {
				mTimeCount --;
				return true;
			}
			// ゼロになれば、false を返送する
			return false;
		}

		/*
		 * 演奏時間を計算する
		 * @param long tempo
		 * @return long
		 */ 
		public long getPlayTime( long tempo ) {	
			// デルタタイムの次のMIDIメッセージのときは
			if ( mDeltaTime > 0 ) {
				// 演奏時間を計算する
				mPlayTime += mDeltaTime * tempo ; 
				// デルタタイムが累積しないように、ゼロにする
				mDeltaTime = 0;
			}		
			return mPlayTime;
		}	
		
	}

}
