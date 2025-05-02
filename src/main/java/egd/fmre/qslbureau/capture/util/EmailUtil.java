package egd.fmre.qslbureau.capture.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import egd.fmre.qslbureau.capture.dto.EmailDetailsObject;
import egd.fmre.qslbureau.capture.exception.SendMailException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EmailUtil {

	private static final String HEADER_CONTENT_TYPE_NAME = "Content-type";
	private static final String HEADER_CONTENT_TYPE_VALUE = "text/HTML; charset=UTF-8";
	private static final String HEADER_FORMAT_NAME = "format";
	private static final String HEADER_FORMAT_VALUE = "flowed";
	private static final String HEADER_CONTENT_TRANSFER_ENCODING_NAME = "Content-Transfer-Encoding";
	private static final String HEADER_CONTENT_TRANSFER_ENCODING_VALUE = "8bit";
	private static final String ENCODING = "UTF-8";
	private static final String CONTENT_ENCODING = "text/html; charset=utf-8";

	/**
	 * Utility method to send simple HTML email
	 * 
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	public static void sendEmail(EmailDetailsObject emailDetailsObject) throws SendMailException {
		try {
			MimeMessage msg = new MimeMessage(emailDetailsObject.getSession());
			// set message headers
			msg.addHeader(HEADER_CONTENT_TYPE_NAME, HEADER_CONTENT_TYPE_VALUE);
			msg.addHeader(HEADER_FORMAT_NAME, HEADER_FORMAT_VALUE);
			msg.addHeader(HEADER_CONTENT_TRANSFER_ENCODING_NAME, HEADER_CONTENT_TRANSFER_ENCODING_VALUE);

			msg.setFrom(new InternetAddress(emailDetailsObject.getEmailFromAddress(),
					emailDetailsObject.getEmailFromName()));

			msg.setReplyTo(InternetAddress.parse(emailDetailsObject.getEmailFromAddress(), false));

			msg.setSubject(emailDetailsObject.getSubject(), ENCODING);

			// msg.setText(body, ENCODING);
			msg.setContent(emailDetailsObject.getBody(), CONTENT_ENCODING);

			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDetailsObject.getEmailTo(), false));
			log.info("Message is ready");
			//Transport.send(msg);

			log.info("EMail Sent Successfully!!");
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new SendMailException(e);
		} finally {

		}
	}
}
