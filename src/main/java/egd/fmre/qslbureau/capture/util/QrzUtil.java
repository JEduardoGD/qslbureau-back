package egd.fmre.qslbureau.capture.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import egd.fmre.qslbureau.capture.dto.qrz.CallsignDAO;
import egd.fmre.qslbureau.capture.dto.qrz.DxccentityModelDAO;
import egd.fmre.qslbureau.capture.dto.qrz.QRZDatabaseDAO;
import egd.fmre.qslbureau.capture.dto.qrz.QrzCallsignDAO;
import egd.fmre.qslbureau.capture.dto.qrz.QrzSessionDTO;
import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.exception.QrzException;

public abstract class QrzUtil {

	private static final String QRZ_BASE_URL = "http://xmldata.qrz.com/xml/1.31";
	private static final String QRZ_LOGIN_URL = QRZ_BASE_URL + "/?username=%s;password=%s";
	public static final String QRZ_ASK_FOR_CALL = QRZ_BASE_URL + "/?s=%s;callsign=%s";

	
	public static QrzSessionDTO getNewQrzSession(String username, String password) throws QrzException {
		QRZDatabaseDAO qrzdatabase = null;

		String url = String.format(QRZ_LOGIN_URL, username, password);

		qrzdatabase = callToQrz(url);

		if (qrzdatabase.getSession() != null && qrzdatabase.getSession().getKey() != null
				&& qrzdatabase.getSession().getError() == null) {
			return qrzdatabase.getSession();
		}
		throw new QrzException(qrzdatabase.getSession().getError());
	}

	public static QRZDatabaseDAO callToQrz(String url) throws QrzException {
		QRZDatabaseDAO qrzdatabase;
		byte[] targetArray;
		try {
			targetArray = HttpUtil.httpCall(url);
		} catch (IOException e) {
			throw new QrzException(e.getLocalizedMessage());
		}

		try (InputStream is = FileUtil.byteArrayToInputStream(targetArray)) {
			qrzdatabase = parseQrz(new InputSource(is));
		} catch (JAXBException | SAXException | IOException e) {
			throw new QrzException(e.getLocalizedMessage());
		}
		return qrzdatabase;
	}

	
	private static QRZDatabaseDAO parseQrz(InputSource is) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(QRZDatabaseDAO.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		// This may not be strictly required as DTDs shouldn't be allowed at all, per
		// previous line.
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
		reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

		MyNamespaceFilter inFilter = new MyNamespaceFilter(null, true);
		inFilter.setParent(reader);
		SAXSource source = new SAXSource(inFilter, is);

		QRZDatabaseDAO qrzDatabase = (QRZDatabaseDAO) unmarshaller.unmarshal(source);
		return qrzDatabase;
	}

	public static Qrzsession parse(QrzSessionDTO qrzSessionDAO) {
		Qrzsession qrzsession = new Qrzsession();
		qrzsession.setKey(qrzSessionDAO.getKey());
		qrzsession.setCount(qrzSessionDAO.getCount());
		qrzsession.setSubExp(qrzSessionDAO.getSubExp());
		qrzsession.setGmTime(qrzSessionDAO.getGmTime());
		qrzsession.setRemark(qrzSessionDAO.getRemark());
		qrzsession.setError(qrzSessionDAO.getError());
		qrzsession.setError(qrzSessionDAO.getError());
		return qrzsession;
	}

	public static CallsignDAO parse(QrzCallsignDAO qrzCallsignDAO) {
		CallsignDAO callsignDAO = new CallsignDAO();
		callsignDAO.setCall(qrzCallsignDAO.getCall());
		callsignDAO.setDxcc(qrzCallsignDAO.getDxcc());
		callsignDAO.setState(qrzCallsignDAO.getState());
		callsignDAO.setItuzone(qrzCallsignDAO.getItuzone());
		callsignDAO.setCqzone(qrzCallsignDAO.getCqzone());
		callsignDAO.setCountry(qrzCallsignDAO.getCountry());
		callsignDAO.setCont(null);
		return callsignDAO;
	}

}
