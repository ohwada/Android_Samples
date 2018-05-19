/**
 *  JavaMail Sample
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.javamailsample2;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.*;


 	/**
	 * class MailMessage
	 */ 
public class MailMessage {

        private final static String LF = "\n";

         private final static String RFC822_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

         private final static String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";

        public String subject;
        public String content;
        public String mail_from;
        public Date sent_date;


/**
     * constractor
     */ 
public MailMessage() {
    // nothing to do
} // MailMessage


/**
     * constractor
     */ 
public MailMessage(Message m) {
    try {
        createFromMessage(m);
    } catch (Exception e) {
        e.printStackTrace();
    }
} // MailMessage

/**
     * constractor
     */ 
public MailMessage(String subject, String content, String from, Date date) {
    setMessage(subject, content, from, date);
} // MailMessage


/**
     * createFromMessage
     */ 
private void createFromMessage(Message m) throws Exception {
            String subject = m.getSubject().toString();
            String content = m.getContent().toString();
            Date date = m.getSentDate();
            String from = null;
            Address[] addr_arr = m.getFrom();
        if ((addr_arr != null)&&(addr_arr.length >0)) {
            from =addr_arr[0].toString();
        }
    setMessage(subject, content, from, date);
} // createFromMessage


/**
     * setMessage
     */ 
private void setMessage(String _subject, String _content, String _from, Date _date) {
    this.subject  = _subject;
    this.content = _content;
    this.mail_from = _from;
    this.sent_date = _date;
} // setMessage


/**
     * getMessage
     */ 
public String getMessage() {
        String msg = "";
        msg += subject + LF;
        msg += mail_from + LF;
        msg += getDateRFC822() + LF;
        msg += content + LF;
        return msg;
} // getMessage


/**
     * getDateRFC822
     */ 
public String getDateRFC822() {
    SimpleDateFormat sdf = new SimpleDateFormat(RFC822_FORMAT);
    return sdf.format ( sent_date );
} // ggetDateRFC822

} // class class MailMessage

