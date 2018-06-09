/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

/**
 * １つの MIDI メッセージを格納するクラス
 */
public class MidiMessage {

	// ステータス・バイトの定義
	public final static int STATUS_NOTE_OFF = 0x0080;
	public final static int STATUS_NOTE_ON = 0x0090;
	public final static int STATUS_POLYPHONIC_AFTERTOUCH = 0x00A0;
	public final static int STATUS_CONTROL_CHANGE = 0x00B0;
	public final static int STATUS_PROGRAM_CHANGE = 0x00C0;
	public final static int STATUS_CHANNEL_AFTERTOUCH = 0x00D0;
	public final static int STATUS_PITCH_WHEEL = 0x00E0;
	public final static int STATUS_SYSTEM_EXCLUSIVE = 0x00F0;		

	// トラック番号
	public int track = 0;
	// ステータス・コード
	public int status = 0;
	// MIDIのバイト列
	public byte[] bytes = null;
	// 演奏時間
	public long playtime = 0;

	/**
	 * コンストラクタ
	 * @param int _track
	 * @param int _status
	 * @param byte[] _bytes
	 * @param int _time
	 */
	public MidiMessage( int _track, int _status, byte[] _bytes, long _time ) {
		track = _track;
		status = _status;
		bytes = _bytes;
		playtime = _time;
	}		
}				
