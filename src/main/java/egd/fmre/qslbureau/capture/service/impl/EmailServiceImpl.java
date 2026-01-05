package egd.fmre.qslbureau.capture.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.AttachmentObject;
import egd.fmre.qslbureau.capture.dto.EmailDataDto;
import egd.fmre.qslbureau.capture.dto.EmailDetailsObject;
import egd.fmre.qslbureau.capture.dto.EmailRepresentativeDTO;
import egd.fmre.qslbureau.capture.dto.OrphanCallsignReportObjectDTO;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.exception.SendMailException;
import egd.fmre.qslbureau.capture.service.EmailService;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import egd.fmre.qslbureau.capture.util.ClassOfEmailEnum;
import egd.fmre.qslbureau.capture.util.EmailHelper;
import egd.fmre.qslbureau.capture.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class EmailServiceImpl extends EmailHelper implements EmailService {

	@Value("${MAIL.USER}")
	private String mailUser;
	
	@Value("${MAIL.PASSWORD}")
	private String mailPassword;
	
	@Autowired RepresentativeService representativeService;
	@Autowired ReportsService reportsService;
	
	private static final String EMAIL_FROM = "qslbureau@fmre.mx";
	private static final String EMAIL_SUBJECT = "Tarjetas QSL en poder del Buro de la FMRE para ";
	private static final String EMAIL_FROM_NAME = "QSL Bureau FMRE A.C.";
	
	private static final String PROP_MAIL_SMTP_HOST_NAME = "mail.smtp.host";
	private static final String PROP_MAIL_SMTP_HOST_VALUE = "fmre.mx";
	private static final String PROP_MAIL_SMTP_SOCKETFACTORY_PORT_NAME = "mail.smtp.socketFactory.port";
	private static final String PROP_MAIL_SMTP_SOCKETFACTORY_PORT_VALUE = "465";
	private static final String PROP_MAIL_SMTP_SOCKETFACTORY_CLASS_NAME = "mail.smtp.socketFactory.class";
	private static final String PROP_MAIL_SMTP_SOCKETFACTORY_CLASS_VALUE = "javax.net.ssl.SSLSocketFactory";
	private static final String PROP_MAIL_SMTP_AUTH_NAME = "mail.smtp.auth";
	private static final String PROP_MAIL_SMTP_AUTH_VALUE = "true";
	private static final String PROP_MAIL_SMTP_PORT_NAME = "mail.smtp.port";
	private static final String PROP_MAIL_SMTP_PORT_VALUE = "465";
	
	private static final String STR_NOMBRE = "[[nombre]]";
	private static final String STR_APELLIDO = "[[apellido]]";
	private static final String STR_INDICATIVO = "[[indicativo]]";
	private static final String STR_GRID = "[[grid]]";
	
	private static final String EMPTY_STRING = "";
	private static final String EMPTY_TD = "<td>&nbsp;</td>";

	@Override
	public boolean sendMail(EmailDataDto emailData) throws SendMailException {
		String emailFrom = EMAIL_FROM;
		
		String correoXdeY = EMPTY_STRING;
		if (emailData.getNumOfContact() == 1 || emailData.getNumOfContact() > 12) {
			correoXdeY = EMPTY_STRING;
		}
		if (emailData.getNumOfContact() > 1) {
			correoXdeY = String.format("(%d de 12)", emailData.getNumOfContact());
		}
		if (emailData.getNumOfContact() == 12) {
			correoXdeY = String.format("(%d)", emailData.getNumOfContact());
		}
		
		ClassOfEmailEnum classOfEmailEnum = null;
		if (emailData.getNumOfContact() == 1) {
			classOfEmailEnum = ClassOfEmailEnum.X_OF_Y;
		}
		if (emailData.getNumOfContact() > 1) {
			classOfEmailEnum = ClassOfEmailEnum.X_OF_Y;
		}
		if (emailData.getNumOfContact() == 12) {
			classOfEmailEnum = ClassOfEmailEnum.FINAL_EMAIL;
		}
		if (emailData.getNumOfContact() == 1 || emailData.getNumOfContact() > 12) {
			classOfEmailEnum = ClassOfEmailEnum.POST_FINAL_EMAIL;
		}
		
		
		String emailSubject = EMAIL_SUBJECT + emailData.getIndicativo() + " " + correoXdeY;
		String emailTo = emailData.getEmailAddress();
		String emailFromName = EMAIL_FROM_NAME;

		String contentEmail = loadEmailFile(classOfEmailEnum);
		
		contentEmail = contentEmail.replace(STR_NOMBRE, emailData.getNombre());
		contentEmail = contentEmail.replace(STR_APELLIDO, emailData.getApellido() != null ? emailData.getApellido() : "");
		contentEmail = contentEmail.replace(STR_INDICATIVO, emailData.getIndicativo());
		contentEmail = contentEmail.replace(STR_GRID, emailData.getGrid());
		
		/**/

		log.info("SSLEmail Start");
		Properties props = new Properties();
		props.put(PROP_MAIL_SMTP_HOST_NAME, PROP_MAIL_SMTP_HOST_VALUE); // SMTP Host
		props.put(PROP_MAIL_SMTP_SOCKETFACTORY_PORT_NAME, PROP_MAIL_SMTP_SOCKETFACTORY_PORT_VALUE); // SSL Port
		props.put(PROP_MAIL_SMTP_SOCKETFACTORY_CLASS_NAME, PROP_MAIL_SMTP_SOCKETFACTORY_CLASS_VALUE); // SSL Factory Class
		props.put(PROP_MAIL_SMTP_AUTH_NAME, PROP_MAIL_SMTP_AUTH_VALUE); // Enabling SMTP Authentication
		props.put(PROP_MAIL_SMTP_PORT_NAME, PROP_MAIL_SMTP_PORT_VALUE); // SMTP Port

		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUser, mailPassword);
			}
		};

		Session session = Session.getInstance(props, auth);
		log.info("Session created");
		EmailDetailsObject emailDetailsObject = new EmailDetailsObject(session, emailFrom, emailFromName, emailTo, emailSubject, contentEmail, null);
		EmailUtil.sendEmail(emailDetailsObject);
		return true;
	}
	
	private String createTableContent(RedirectListObjectRepresentative redirectListObjectRepresentative) {
	    StringBuffer tableContentSb = new StringBuffer();
	    for (RedirectListObjectZone redirectListObjectZone : redirectListObjectRepresentative.getRlozList()) {
		int total = 0;
		for (RedirectListObjecCallsignRule redirectListObjecCallsignRule : redirectListObjectZone.getRlocList()) {
		    String cellCallsignTo = String.format("<td>%s</td>", redirectListObjecCallsignRule.getCallsignTo());
		    String callsignRedirect = redirectListObjecCallsignRule.getCallsignRedirect() != null ? redirectListObjecCallsignRule.getCallsignRedirect() : "&nbsp;";
		    String cellCallsignRedirect = String.format("<td>%s</td>", callsignRedirect);
		    String slotNum = redirectListObjecCallsignRule.getSlotNum() != null ? redirectListObjecCallsignRule.getSlotNum() + "" : "&nbsp;";
		    String cellSlotNum = String.format("<td>%s</td>", slotNum);
		    String totalCell = redirectListObjecCallsignRule.getTotal() != null ? redirectListObjecCallsignRule.getTotal() + "" : "&nbsp;";
		    String cellTotal = String.format("<td>%s</td>", totalCell);
		    
		    String row = String.format("<tr>%s%s%s%s</tr>", cellCallsignTo, cellCallsignRedirect, cellSlotNum,
			    cellTotal);
		    tableContentSb.append(row);
		    total += redirectListObjecCallsignRule.getTotal() != null ? redirectListObjecCallsignRule.getTotal() : 0;
		}
		String cellTotal = String.format("<td><strong>%s</strong></td>", total);
		String rowTotal = String.format("<tr><td><strong>Total:</strong></td>%s%s%s</tr>", EMPTY_TD, EMPTY_TD, cellTotal);
		tableContentSb.append(rowTotal);
	    }
	    return tableContentSb.toString();
	}

	@Override
	public boolean sendMailRepresentative(EmailRepresentativeDTO emailRepresentative) throws SendMailException {
		String emailFrom = EMAIL_FROM;
		
		
		String emailSubject =String.format("%s %s", EMAIL_SUBJECT, emailRepresentative.getZoneName());
		String emailTo = emailRepresentative.getEmailAddress();
		String emailFromName = EMAIL_FROM_NAME;

		String contentEmail = loadEmailFileRepresentative();
		
		/**/

		Representative representative = representativeService.getActiveRepresentativesById(emailRepresentative.getRepresentativeId());
		RedirectListObjectRepresentative redirectListObjectRepresentative = reportsService
			.redirectLists(representative);
		
		contentEmail = contentEmail.replace(STR_NOMBRE, emailRepresentative.getRepresentativeName());
		contentEmail = contentEmail.replace(STR_APELLIDO, emailRepresentative.getRepresentativeLastName() != null ? emailRepresentative.getRepresentativeLastName() : "");
		contentEmail = contentEmail.replace(STR_GRID, createTableContent(redirectListObjectRepresentative));
		

		List<OrphanCallsignReportObjectDTO> prphanCallsignReportObjectDTOList = reportsService
			.getOrphansCallsignsReport();
		byte[] reportInBytes = reportsService.generateOrphansReport(prphanCallsignReportObjectDTOList);
		AttachmentObject attachment = new AttachmentObject("OrphanCallsignsReport.xlsx", reportInBytes);

		log.info("SSLEmail Start");
		Properties props = new Properties();
		props.put(PROP_MAIL_SMTP_HOST_NAME, PROP_MAIL_SMTP_HOST_VALUE); // SMTP Host
		props.put(PROP_MAIL_SMTP_SOCKETFACTORY_PORT_NAME, PROP_MAIL_SMTP_SOCKETFACTORY_PORT_VALUE); // SSL Port
		props.put(PROP_MAIL_SMTP_SOCKETFACTORY_CLASS_NAME, PROP_MAIL_SMTP_SOCKETFACTORY_CLASS_VALUE); // SSL Factory Class
		props.put(PROP_MAIL_SMTP_AUTH_NAME, PROP_MAIL_SMTP_AUTH_VALUE); // Enabling SMTP Authentication
		props.put(PROP_MAIL_SMTP_PORT_NAME, PROP_MAIL_SMTP_PORT_VALUE); // SMTP Port

		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUser, mailPassword);
			}
		};

		Session session = Session.getInstance(props, auth);
		log.info("Session created");
		EmailDetailsObject emailDetailsObject = new EmailDetailsObject(session, emailFrom, emailFromName, emailTo, emailSubject, contentEmail, Arrays.asList(attachment));
		EmailUtil.sendEmail(emailDetailsObject);
		return true;
	}
}
