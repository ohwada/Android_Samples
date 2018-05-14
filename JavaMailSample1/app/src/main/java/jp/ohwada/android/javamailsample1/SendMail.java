/**
 *  Send Mail 
 *  with JavaMail
 *  via GMAIL SMTP Server
 * reference : https://javaee.github.io/javamail/Android
 *  2018-05-01 K.OHWADA
 */


// =====
// 注意：GMAILのアカウント設定にて、
//「安全性の低いアプリの許可」が必要です
// =====


package jp.ohwada.android.javamailsample1;


import android.util.Log;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
     * class SendMail
     */ 
public class SendMail  {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Mail";
    	private final static String TAG_SUB = "SendMail";

    	private final static String KEY_SMTP_HOST  = "mail.smtp.host";
    	private final static String KEY_MAIL_HOST  = "mail.host";
    	private final static String KEY_SMTP_PORT  = "mail.smtp.port";
    	private final static String KEY_SOCKET_PORT  = "mail.smtp.socketFactory.port";
    	private final static String KEY_SOCKET_FACTORY_CLASS  = "mail.smtp.socketFactory.class";

    	private final static String UTF_8 = "utf-8";
    	private final static String TRANSPORT_SMTP ="smtp";

    	private final static String SMTP_HOST = "smtp.gmail.com";
    	private final static String SMTP_PORT = "465";
    	private final static String SMTP_SSL_FACTORY_CLASS =  "javax.net.ssl.SSLSocketFactory";


            	private String mGmailUser;
            	private String mGmailPassword;


 // callback 
    private Callback mCallback; 


   /*
     * callback interface
     */    
    public interface Callback {
        public void onFinish();
        public void onError(Exception e);
    } // interface

/**
     * constractor
     */ 
public SendMail() {
    // dummy
} // SendMail




    /*
     * setCallback
     */ 
    public void setCallback( Callback callback ) {
        log_d("setCallback");
        mCallback = callback;
    } // setCallback


/**
     * setGmailUser
     */ 
public void setGmailUser(String user)  {
     mGmailUser = user;
} // setGmailUser


/**
     * setGmailPassword
     */ 
public void setGmailPassword(String password)  {
     mGmailPassword = password;
} // setGmailPassword


 	/**
	 * sendAsync
	 */ 
public void sendAsync(final String mail_to, final String subject, final String body)  {

// run it in a separate thread
 new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            send(mail_to, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e );
        }
    }
  }).start();

            notifyFinish();
} // sendAsync


 	/**
	 * send
	 */ 
public void send(String mail_to, String subject, String body) throws Exception {

            final Properties property = createProperties();

            final Session session = createSession(property);

            final MimeMessage  mimeMsg = createMimeMessage( session, mail_to, subject, body );

            final Transport transport = session.getTransport(TRANSPORT_SMTP);

            transport.connect(mGmailUser, mGmailPassword);
            transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
            transport.close();

} // send


 	/**
	 * createProperties
	 */ 
private Properties createProperties() {
            Properties property = new Properties();
            property.put(KEY_SMTP_HOST,  SMTP_HOST);
            property.put(KEY_MAIL_HOST,    SMTP_HOST);
            property.put(KEY_SMTP_PORT,    SMTP_PORT);
            property.put(KEY_SOCKET_PORT,  SMTP_PORT);
            property.put(KEY_SOCKET_FACTORY_CLASS, SMTP_SSL_FACTORY_CLASS);

    return property;
} // createProperties


 	/**
	 * createSession
	 */ 
private Session createSession(Properties property) throws Exception {
            Session session = Session.getInstance(property, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mGmailUser, mGmailPassword);
                }
            });

    return session;
} // createSession


 	/**
	 * createMimeMessage
	 */ 
private MimeMessage createMimeMessage(Session session, String mail_to, String subject, String body) throws Exception {
MimeMessage mimeMsg = new MimeMessage(session);

            mimeMsg.setSubject(subject, UTF_8);
            mimeMsg.setFrom(new InternetAddress(mGmailUser));
            mimeMsg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(mail_to));

    final MimeBodyPart txtPart = new MimeBodyPart();
            txtPart.setText(body, UTF_8);

  final Multipart mp = new MimeMultipart();
            mp.addBodyPart(txtPart);

            mimeMsg.setContent(mp);

    return mimeMsg;
} // createMimeMessage


    /**
     * notifyError
     */
    private void notifyError(Exception e ) {
                    log_d("notifyError");
           if ( mCallback != null ) {
            mCallback.onError(e);
        } 
}	// notifyError


    /**
     * notifyFinish
     */
    private void notifyFinish() {
                    log_d("notifyFinish");
           if ( mCallback != null ) {
            mCallback.onFinish();
        } 
}	// notifyFinish

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class SendMail
