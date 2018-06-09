/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

/*
 * class MidiParser
 * MIDIファイルを解析する
 */ 
public class MidiParser {

	// debug
	private final static boolean D = true;
	private final static int DEBUG_LEVEL = 0;
	private final static String TAG = "MidiPaser";


    // リターンコード
	public final static int RET_SUCCESS = 0;	
	public final static int RET_ERR_HEADER = 1;	
	public final static int RET_ERR_HEADER_LENGTH = 2;
	public final static int RET_ERR_STATUS_BYTE = 3;
	public final static int RET_ERR_F0 = 4;
	public final static int RET_ERR_TEMPO = 5;
	public final static int RET_ERR_END_OF_TRACK = 6;

	      										    
    // MIDIファイルの解析の状態
	private final static int STATE_INIT = 0;
	private final static int STATE_HEAD_CHUNK = 1;
	private final static int STATE_HEADER = 2;
	private final static int STATE_BODY = 3;
	private final static int STATE_END = 3;
		
    // MIDI メッセージの種別
	private final static int MSG_NORMAL = 0;
	private final static int MSG_END = 1;
	private final static int MSG_ERROR = 2;

	// 空白
	private final static String CHAR_SPACE = " ";
	
	// 上位から引き継いだ object
	private Context mContext;

	// 解析対象のMIDIのバイト列
	private byte[] mMidiBytes = null;

	// バイト列の処理中の位置を示すポインタ		
	private int mReadPointer = 0;

	// チャンク単位のバイト列の最後を示すポインタ
	private int mReadEnd = 0;

	// バイト列の最後になったことを示すフラグ
	private boolean isEndOfBytes = false;
	
	// デルタタイムのベースとなる時間
	private long mTimebase = 0;
			
	// 解析されたMIDIのリスト
	private MidiParseMessageList mMidiParseMessageList = null;
	
	// トラック毎のリスト
	private List<MidiParseMessageList> mTrackList = null;

	// return code
	private int mRetCode = RET_SUCCESS;
	
	// MIDI バイトの解析の状態
	private int mParseState = STATE_INIT;

	// ログに残すために、読み込んだデータの一時保管
    private String mLogText = "";

	/*
	 * コンストラクタ
	 * @param Context context
	 */ 		
	public MidiParser( Context context ) {
		mContext = context;
	}

	/*
	 * バイト列を解析する
	 * @param byte[] bytes
	 * @return int
	 */ 	
	public int parse( byte[] bytes ) {
		log_d( "parse" );
		mMidiBytes = bytes;
		isEndOfBytes = false;	
		mReadPointer = 0;
		mReadEnd = 0;
		mTrackList = new ArrayList<MidiParseMessageList>();

		boolean ret = procHead();
		if ( !ret ) return mRetCode;	
		
		mParseState = STATE_HEADER;	
		while (true) {
			// バイト列の最後になれば、終了する
			if ( isEndOfBytes ) break;
			// エラーになれば、終了する
			if ( mRetCode != RET_SUCCESS ) break;
			procTrackLoop();
		}

		return mRetCode;		
	}

	/*
	 * 解析されたMIDIのリストを取得する
	 * @return List<MidiParseMessageList>
	 */ 
	public List<MidiParseMessageList> getList() {
		return mTrackList;
	}

	/*
	 * ベース時間を取得する
	 * @return long
	 */ 
	public long getTimebase() {	
		return mTimebase;
	}	

	/*
	 * ヘッダチャンクの処理
	 * @return boolean	 
	 */
	private boolean procHead() {
		clearLogText();
		byte b1 = getOneByte();
		byte b2 = getOneByte();
		byte b3 = getOneByte();
		byte b4 = getOneByte();
		if (( b1 == 0x4d )&&( b2 == 0x54 )&&( b3 == 0x68 )&&( b4 == 0x64 )) {
			log_write_with_text( "head chunk" );
			int len = getLength4();
			if ( len == 6 ) {
				mParseState = STATE_HEAD_CHUNK;
				procHeadBody( len );
				return true; // 正常
			} else {
				log_d( "invalid header length" );				
				mRetCode = RET_ERR_HEADER_LENGTH;		
			}	
		} else {
			log_d( "invalid header" );
			mRetCode = RET_ERR_HEADER;		
		}
		return false; // エラー
	}

	/*
	 * ヘッダチャンクの本体の処理
	 * @param int len 
	 */      		
	private void procHeadBody( int len ) {	
		mReadEnd = mReadPointer + len;
		// format
		getOneByte();
		getOneByte();
		log_write_with_text( "format" );
		// tracks
		getOneByte();
		getOneByte();
		log_write_with_text( "number of tracks" );
		// timebase
		byte b1 = getOneByte();
		byte b2 = getOneByte();
		mTimebase = getValue2( b1, b2 );												
		log_write_with_text( "timebase" );
	}

	/*
	 * トラックチャンクの処理
	 */
	private void procTrackLoop() {
		// ヘッダの検索中のとき
		if ( mParseState == STATE_HEADER ) {
			boolean ret1 = procTrackHeader();
			if ( ret1 ) {
				// ヘッダが見つかれば、本体の処理に進む
				mParseState = STATE_BODY;
			}				
		}
		// 本体の処理中のとき  
		if ( mParseState == STATE_BODY ) {
			while( true ) {
				// バイト列の最後になれば、終了する
				if ( isEndOfBytes ) break;
				// エラーになれば、終了する
				if ( mRetCode != RET_SUCCESS ) break;
				// 状態が変わったとき
				if ( mParseState != STATE_BODY ) break;
				int ret2 = procTrackBody();
      			switch ( ret2 ) {
      				case MSG_END:
      					// 本体の最後まで処理したときは、次のトラックチャックの処理に進む
      					mParseState = STATE_HEADER;
      					break;
      				case MSG_ERROR:
      					// エラーのときは、終了する
      					break;
      			}	
			}
		}
	}

	/*
	 * トラックチャンクのヘッダを検索する
	 * @return boolean
	 */ 				
	private boolean procTrackHeader() {
		clearLogText();
		byte b1 = 0;
		byte b2 = 0;
		byte b3 = 0;
		byte b4 = 0;					
		while( true ) {
			// バイト列の最後になれば、終了する
			if ( isEndOfBytes ) break;
			// ヘッダが見つかれば、次の処理に進む
			if (( b1 == 0x4d )&&( b2 == 0x54 )&&( b3 == 0x72 )&&( b4 == 0x6b )) {
				log_write_with_text(  "track" );
				// 新しいトラックの入れ物を用意する
				mMidiParseMessageList	= new MidiParseMessageList();
				mReadEnd = mReadPointer + getLength4();
      			// デルタタイムを加算する
				addDeltaTime();
				return true;
			} 
			// ヘッダが見つかるまで、１バイトづつ読み進める
			// 正しい形式であれば、最初の４バイトがヘッダである
			b1 = b2;
			b2 = b3;
			b3 = b4;
			b4 = getOneByte();
		}
		return false;
	}

	/*
	 * トラックチャンクの本体の処理
	 * @return int
	 */ 
	private int procTrackBody() {
      	int ret = procTrackMessage();
      	switch ( ret ) {
      		case MSG_NORMAL:
      			// デルタタイムを加算する
				addDeltaTime();
      			break;
      		case MSG_END:
      		case MSG_ERROR:
      		     // 何もしない
      			break; 	     			
      	}	
      	return ret;	
     }


	/*
	 * トラックチャンクのメッセージの処理
	 * @return int
	 */ 
	private int procTrackMessage() {
		log_d("procTrackMessage");

		clearLogText();
		int ret = MSG_NORMAL;

		byte status_byte = getOneByte();	
		String msg = "status: " + toHexString( status_byte ) ;
		log_d(msg);

		int status_code = status_byte & 0x00f0;
		switch ( status_code ) {
			case 0x0080:
				procTrackMessage3Byte( 
					MidiMessage.STATUS_NOTE_OFF, status_byte );
				break;
			case 0x0090:
				procTrackMessage3Byte( 
					MidiMessage.STATUS_NOTE_ON, status_byte );
				break;
			case 0x00A0:
				procTrackMessage3Byte( 
					MidiMessage.STATUS_POLYPHONIC_AFTERTOUCH, status_byte );
				break;
			case 0x00B0:
				procTrackMessage3Byte( 
					MidiMessage.STATUS_CONTROL_CHANGE, status_byte );
				break;
			case 0x00C0:
				procTrackMessage2Byte( 
					MidiMessage.STATUS_PROGRAM_CHANGE, status_byte );
				break;
			case 0x00D0:
				procTrackMessage2Byte( 
					MidiMessage.STATUS_CHANNEL_AFTERTOUCH, status_byte );
				break;
			case 0x00E0:
				procTrackMessage3Byte( 
					MidiMessage.STATUS_PITCH_WHEEL, status_byte );		
				break;
			case 0x00F0:
				ret = procTrackMessageF0( status_byte );
				break;
			default:
				// ステータスバイトが、0x8* から 0xF* 以外のときは、エラーにする
				log_d( "Invalid Status Byte" );
      			mRetCode = RET_ERR_STATUS_BYTE;	
      			ret = MSG_ERROR;		
		}
		return ret;
	}

				
	/*
	 * トラックチャンクのメッセージの処理
	 * ステータスが F0 のとき
	 * @param byte status_byte
	 * @return int
	 */ 
	private int procTrackMessageF0( byte status_byte ) {
		int ret = MSG_NORMAL;
		int status_code = status_byte & 0x000f;			
		switch( status_code ) {
    		case 0x0000 : // F0 : sysex
    		case 0x0007 : // F7 : Sysex2
    			procMsgSysex( status_byte );
      			break;
    		case 0x000F:  // FF
    			byte b1 = getOneByte();
      			switch ( b1 ) {
     				case 0x51: // FF51
     					ret = procMsgTempo( status_byte, b1 );
       					break;
      				case 0x2F:  // FF2F
      					ret = procMsgEndOfTrack( status_byte, b1 );
      					break;
      				default:  // FF**
      					procMsgOthersF0( status_byte, b1 );
        				break;
				} // switch end
				break;
      		default:
				log_d( "Invalid F0 Message" );
      			mRetCode = RET_ERR_F0;
      			ret = MSG_ERROR;
        		break;
		} // switch end
		return ret;
	}

	/*
	 * ２バイトのメッセージの処理
	 * @param int status_code
	 * @param byte status_byte
	 */ 
	private void procTrackMessage2Byte( int status_code, byte status_byte ) {	
		byte[] bytes = new byte[ 2 ];
		bytes[0] = status_byte;
		bytes[1] = getOneByte();		
		procMsgDevice( status_code, bytes );
	}

	/*
	 * ３バイトのメッセージの処理
	 * @param int status_code
	 * @param byte status_byte
	 */ 
	private void procTrackMessage3Byte( int status_code, byte status_byte ) {	
		byte[] bytes = new byte[ 3 ];
		bytes[0] = status_byte;
		bytes[1] = getOneByte();	
		bytes[2] = getOneByte();
		procMsgDevice( status_code, bytes );
	}

	/*
	 * Sys Ex のメッセージの処理
	 * @param byte status_byte
	 * @return int
	 */ 
	private void procMsgSysex( byte status_byte ) {
		// １バイト目はデータ長
	    int len = 0x00ff & getOneByte();
		byte[] bytes = new byte[ len + 1 ];
		bytes[0] = status_byte;
		// １バイト目を除いて機器に送信する形式にする
      	for ( int i=0; i<len; i++ ) {
      		bytes[ i + 1 ] = getOneByte();
      	}
      	procMsgDevice( MidiMessage.STATUS_SYSTEM_EXCLUSIVE, bytes );
    }

	/*
	 * MIDI機器に送信するメッセージの処理
	 * @param byte status_byte
	 * @param byte[] bytes
	 */	
	private void procMsgDevice( int status_code, byte[] bytes ) {
		log_write_with_text( "device" );
		mMidiParseMessageList.add( 
			new MidiParseMessage( status_code, bytes ) );
		String str = "";
		for ( int i=0; i<bytes.length; i++ ) {
			str += toHexString( bytes[ i ] ) + CHAR_SPACE;			
		}
		log_write_1( "Device : " + str + " : " + getMsgName( bytes ) );
	}
      			
	/*
	 * テンポ・メッセージの処理
	 * @param byte status_byte
	 * @param byte b1
	 * @return int
	 */ 
	private int procMsgTempo( byte status_byte, byte b1 ) {
		// 2バイト目はデータ長、３のはず
       	byte b2 = getOneByte();       			
       	int len = 0x00ff & b2;
       	// ３でないときは、エラーにする
       	if ( len != 3 ) {
			log_d( "Invalid Tempo Message" );
			mRetCode = RET_ERR_TEMPO;
			return MSG_ERROR;
       	}
       	byte[] bytes = new byte[ len + 3 ];
       	bytes[ 0 ] = status_byte;
       	bytes[ 1 ] = b1;
       	bytes[ 2 ] = b2;
		for ( int i=0; i<len; i++ ){
			bytes[ i + 3 ] = getOneByte();		
		}
		mMidiParseMessageList.add( 
			new MidiParseMessage( MidiParseMessage.STATUS_TEMPO, bytes ) );	
		log_write_with_text( "tempo" );
		return MSG_NORMAL;
    }

	/*
	 * エンド・メッセージの処理
	 * @param byte status_byte
	 * @param byte b1
	 * @return int
	 */         					
	private int procMsgEndOfTrack( byte status_byte, byte b1 ) {
		// 2バイト目はデータ長
		byte b2 = getOneByte(); 
		// 0でないときは、エラーにする
       	if ( b2 != 0x00 ) {
			log_d( "Invalid EndOfTrack Message" );
			mRetCode = RET_ERR_END_OF_TRACK;
			return MSG_ERROR;
       	}
    	log_write_with_text( "end of track" );
    	// トラックに格納する
    	mTrackList.add( mMidiParseMessageList );
    	// 空にする
    	mMidiParseMessageList = null;
		return MSG_END;	
    }

	/*
	 * その他のメッセージの処理
	 * @param byte status_byte
	 * @param byte b1
	 */       					
	private void procMsgOthersF0( byte status_byte, byte b1 ) {
		// 2バイト目はデータ長
		byte b2 = getOneByte();
		int len = 0x00ff & b2;
		byte[] bytes = new byte[ len + 3 ];
		bytes[ 0 ] = status_byte;
		bytes[ 1 ] = b1;
		bytes[ 2 ] = b2;		
       for ( int i=0; i<len; i++ ) {
        	bytes[ i + 3 ] = getOneByte();
        }
        mMidiParseMessageList.add( 
        	new MidiParseMessage( MidiParseMessage.STATUS_OHERS, bytes ));
        log_write_with_text( getMsgNameOthers( b1 ) );	
    }

	/*
	 * デルタタイムをリストに追加する
	 */  
	private void addDeltaTime() {
		long time = procDeltaTime();
		if ( time > 0 ) {
			mMidiParseMessageList.add( 
				new MidiParseMessage( MidiParseMessage.STATUS_DELTA_TIME, time ));
		}
	}
	      				
	/*
	 * デルタタイムの処理
	 * @return long
	 */  
	private long procDeltaTime() {
		byte b;
		long ret = 0;
		while (true) {
			// バイト列の最後になれば、終了する
			if ( isEndOfBytes ) return 0;
			b = getOneByte();
			ret = (ret <<7) | (b & 0x7f);
			// ８ビット目が0のときは、デルタタイムを終了する
			if((b & 0x80) == 0) break;
		}
		log_write_with_text( "DeltaTime" );
		return ret;
	}

	/*
	 * ４バイト単位のバイト長を取得する
	 */ 
	private int getLength4() {
		byte b1 = getOneByte();
		byte b2 = getOneByte();
		byte b3 = getOneByte();
		byte b4 = getOneByte();
		log_write_with_text( "length" );
      	return getValue4( b1, b2, b3, b4 );
     }

	/*
	 * バイト列から１バイトを取得する
	 * @return byte
	 */ 
	private byte getOneByte() {
		byte b =  getOneByteByPointer( mReadPointer ); 
		mReadPointer ++;
		// チャンクの最後になったら、ヘッダの検索状態にする
		if ( mReadPointer >= mReadEnd ) {
			mParseState = STATE_END;
		}
		addHexLogText( b );
		return b;
	}

	/*
	 * バイト列から１バイトを取得する
	 * @param int ptr 
	 * @return byte
	 */       		    			
	private byte getOneByteByPointer( int ptr ) {
		int len = mMidiBytes.length;
		if ( len > ptr ) {
			return mMidiBytes[ ptr ];
		}
		// バイト列の最後になったときは。終了フラグを立てる
		isEndOfBytes = true;
		return 0x00;
	}

	/*
	 * ２バイト単位の値を取得する
	 */ 
	private int getValue2( byte b1, byte b2 ) {
		int v = b1 & 0x00ff;
        v = (v << 8) | (b2 & 0x00ff);
      	return v;
    }
 
 	/*
	 * ４バイト単位の値を取得する
	 */    
	private int getValue4( byte b1, byte b2, byte b3, byte b4 ) {
		int v = b1 & 0x00ff;
        v = (v << 8 ) | (b2 & 0x00ff);
      	v = (v << 8 ) | (b3 & 0x00ff);
      	v = (v << 8 ) | (b4 & 0x00ff);
      	return v;
    }
			
// --- デバック ---
	/*
	 * メッセージの名称を取得する
	 * @param byte[] buf 
	 */
	private String getMsgName( byte[] buf ) {
		String str = "その他";
		switch ( buf[0] & 0xf0 ) {
			case 0x80:
				str = "Key Off";
				break;
			case 0x90:
				str = "Key On";
				break;
			case 0xA0:
				str = "Polyphonic After Touch";
				break;
			case 0xB0:
				str = "Control Change";
				break;
			case 0xC0:
				str = "Program Change";
				break;
			case 0xD0:
				str = "Channnel After Touch";
				break;
			case 0xE0:	
				str = "Pitch Wheel Change";
				break;
			case 0xF0:					
				str = getMsgNameF0( buf );
				break;
		}
		return str;	
	}

	/*
	 * メッセージの名称を取得する
	 * @param byte[] buf 
	 */
	private String getMsgNameF0( byte[] buf  ) {
	 	int num = buf.length;
		String str = "その他";
		if ( num < 5 ) return str;
		switch ( buf[1] ) {
			case 0x7E:
				if (( buf[3] == 0x09 )&&( buf[4] == 0x01 )) {
					str = "GM1 System On";
				} else if (( buf[3] == 0x09 )&&( buf[4] == 0x02 )) {
					str = "GM1 System Off";
				} else {
					str = "F* 7E";				
				}
				break;
			case 0x43:			
				if ( buf[3] == 0x4c ) {
					str = "XG Parameter Changes";
				} else if (( buf[2] == 0x79 )&&( buf[3] == 0x09 )) {
					str = "Phonetic symbols";
				} else {
					str = "F* 43";				
				}
				break;	
		}
		return str;	
	}

	/*
	 * メッセージの名称を取得する
	 * @param byte b
	 */     					
	private String getMsgNameOthers( byte b ) {
		String str = "その他";
    	switch( b ) {
    	    case 0x00:
				str = "シーケンス番号";
				break;
    	    case 0x01:
				str = "テキスト";
				break;
    	    case 0x02:
				str = "著作権表示";
				break;
    	    case 0x03:
				str = "シーケンス名";
				break;
    	    case 0x04:
				str = "楽器名";
				break; 
    	    case 0x05:
				str = "歌詞";
				break;  
    	    case 0x06:
				str = "マーカー";
				break;   
    	    case 0x07:
				str = "キュー・ポイント";
				break;   
    	    case 0x20:
				str = "チャンネル・プリフィクス ";
				break;
    	    case 0x21:
				str = "エンド・オブ・トラック";
				break;
    	    case 0x51:
				str = "セット・テンポ";
				break;
    	    case 0x54:
				str = "SMPTEオフセット";
				break;
    	    case 0x58:
				str = "拍子";
				break;
    	    case 0x59:
				str = "調";
				break;
    	    case 0x7F:
				str = "シーケンサー固有メタ・イベント";
				break;
		}		
		return str;
	}

	/*
	 * ログデータのクリア
	 */ 
	public void clearLogText() {	
		mLogText = "";
	}

	/*
	 * ログデータに追加する
	 * @param byte
	 */ 
	public void addHexLogText( byte b ) {	
		mLogText += toHexString( b ) + CHAR_SPACE;
	}	
		
	/*
	 * ログデータの取得
	 */ 
	public String getLogText() {	
		String str = mLogText;
		mLogText = "";
		return str;
	}


	/*
	 * HEX文字列に変換する
	 * @param byte b
	 * @return String
	 */ 
	private String toHexString( byte b ) {
		int n = b & 0x00ff;
		String str = Integer.toHexString( n );
		if ( n < 0x0010 ) {
			str = "0" + str;
		}
    	return str;
    } //  toHexString
       

	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, msg );
	}

	/**
	 * write into log file
	 * @param String msg
	 */
	@SuppressWarnings("unused")
	private void log_write_1( String msg ) {
	    if (DEBUG_LEVEL > 0) log_write( msg );
	}

	/**
	 * write into log file
	 * @param String msg
	 */
	@SuppressWarnings("unused")
	private void log_write_with_text( String msg ) {
	    if (DEBUG_LEVEL > 1) log_write( getLogText() + " :  " + msg );
	}

	/**
	 * write into log file
	 * @param String msg
	 */	
	@SuppressWarnings("unused")
	private void log_write( String msg ) {
	    if (DEBUG_LEVEL > 0) {
			// TODO : runtime permission
			// LogFile.write( mContext, msg );
		}
	}
	 
}

