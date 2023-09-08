package egd.fmre.qslbureau.capture.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import egd.fmre.qslbureau.capture.dto.qrz.QRZDatabaseDto;
import egd.fmre.qslbureau.capture.dto.qrz.QrzCallsignDto;
import egd.fmre.qslbureau.capture.dto.qrz.QrzSessionDto;
import egd.fmre.qslbureau.capture.entity.Qrzreg;
import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.exception.QrzException;

public abstract class QrzUtil {

    private static final String QRZ_BASE_URL = "http://xmldata.qrz.com/xml/1.31";
    private static final String QRZ_LOGIN_URL = QRZ_BASE_URL + "/?username=%s;password=%s";
    public static final String QRZ_ASK_FOR_CALL = QRZ_BASE_URL + "/?s=%s;callsign=%s";

    public static QrzSessionDto getNewQrzSession(String username, String password) throws QrzException {
        QRZDatabaseDto qrzdatabase = null;

        String url = String.format(QRZ_LOGIN_URL, username, password);

        qrzdatabase = callToQrz(url);

        if (qrzdatabase.getSession() != null && qrzdatabase.getSession().getKey() != null
                && qrzdatabase.getSession().getError() == null) {
            return qrzdatabase.getSession();
        }
        throw new QrzException(qrzdatabase.getSession().getError());
    }

    public static QRZDatabaseDto callToQrz(String url) throws QrzException {
        QRZDatabaseDto qrzdatabase;
        byte[] targetArray;
        try {
            targetArray = HttpUtil.httpCall(url);
        } catch (IOException e) {
            throw new QrzException(e.getLocalizedMessage());
        }

        try (InputStream is = FileUtil.byteArrayToInputStream(targetArray)) {
            qrzdatabase = parseQrz(new InputSource(is));
        } catch (JAXBException | SAXException | IOException | ParserConfigurationException e) {
            throw new QrzException(e.getLocalizedMessage());
        }
        return qrzdatabase;
    }

    private static QRZDatabaseDto parseQrz(InputSource is)
            throws JAXBException, ParserConfigurationException, SAXException {
        JAXBContext jc = JAXBContext.newInstance(QRZDatabaseDto.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        // This may not be strictly required as DTDs shouldn't be allowed at all, per
        // previous line.
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        MyNamespaceFilter inFilter = new MyNamespaceFilter(null, true);
        inFilter.setParent(reader);
        SAXSource source = new SAXSource(inFilter, is);

        QRZDatabaseDto qrzDatabase = (QRZDatabaseDto) unmarshaller.unmarshal(source);
        return qrzDatabase;
    }

    public static Qrzsession parse(QrzSessionDto qrzSessionDto) {
        Qrzsession qrzsession = new Qrzsession();
        qrzsession.setKey(qrzSessionDto.getKey());
        qrzsession.setCount(qrzSessionDto.getCount());
        qrzsession.setSubExp(qrzSessionDto.getSubExp());
        qrzsession.setGmTime(qrzSessionDto.getGmTime());
        qrzsession.setRemark(qrzSessionDto.getRemark());
        qrzsession.setError(qrzSessionDto.getError());
        qrzsession.setError(qrzSessionDto.getError());
        return qrzsession;
    }

    public static Qrzreg parse(QrzCallsignDto qrzCallsignDto) {
        Qrzreg qrzreg = new Qrzreg();
        qrzreg.setCallsign(qrzCallsignDto.getCall());
        qrzreg.setDxcc(qrzCallsignDto.getDxcc());
        qrzreg.setState(qrzCallsignDto.getState());
        qrzreg.setItu(qrzCallsignDto.getItuzone());
        qrzreg.setCq(qrzCallsignDto.getCqzone());
        qrzreg.setCountry(qrzCallsignDto.getCountry());
        return qrzreg;
    }

}
