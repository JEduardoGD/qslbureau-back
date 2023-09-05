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
import egd.fmre.qslbureau.capture.dto.qrz.QrzSessionDAO;
import egd.fmre.qslbureau.capture.entity.DxccEntity;
import egd.fmre.qslbureau.capture.entity.DxccSession;
import egd.fmre.qslbureau.capture.exception.FmreContestException;

public class y {

    public static class QrzUtil {

        private static final String QRZ_URL = "http://xmldata.qrz.com/xml/1.31";

        public static QrzSessionDAO getNewQrzSession(String username, String password) throws FmreContestException {
            QRZDatabaseDAO qrzdatabase = null;

            String url = QRZ_URL + "/?username=" + username + ";password=" + password;

            qrzdatabase = callToQrz(url);

            if (qrzdatabase.getSession() != null && qrzdatabase.getSession().getKey() != null
                    && qrzdatabase.getSession().getError() == null) {
                return qrzdatabase.getSession();
            }
            throw new FmreContestException(qrzdatabase.getSession().getError());
        }

        public static QRZDatabaseDAO callToQrz(String url) throws FmreContestException {
            QRZDatabaseDAO qrzdatabase;
            byte[] targetArray;
            try {
                targetArray = HttpUtil.httpCall(url);
            } catch (IOException e) {
                throw new FmreContestException(e.getLocalizedMessage());
            }

            try (InputStream is = FileUtil.byteArrayToInputStream(targetArray)) {
                qrzdatabase = parseQrz(new InputSource(is));
            } catch (JAXBException | SAXException | IOException e) {
                throw new FmreContestException(e.getLocalizedMessage());
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

        public static DxccEntity parse(CallsignDAO callsignDAO) {
            DxccEntity dxccEntity = new DxccEntity();
            // dxccEntity.setId(callsignDAO.getDxcc());
            dxccEntity.setId(null);
            dxccEntity.setEntityCode(callsignDAO.getDxcc());
            dxccEntity.setEntity(callsignDAO.getCountry());
            dxccEntity.setCont(callsignDAO.getCont());
            dxccEntity.setItu(callsignDAO.getItuzone());
            dxccEntity.setCq(callsignDAO.getCqzone());
            dxccEntity.setUpdatedAt(new Date());
            return dxccEntity;
        }

        public static DxccEntity parse(DxccentityModelDAO dxccentityModelDAO) {
            DxccEntity dxccEntity = new DxccEntity();
            dxccEntity.setId(null);
            dxccEntity.setEntityCode((long) dxccentityModelDAO.getDxcc());
            dxccEntity.setEntity(dxccentityModelDAO.getName());
            dxccEntity.setCont(dxccentityModelDAO.getContinent());
            dxccEntity.setItu(dxccentityModelDAO.getItuzone());
            dxccEntity.setCq(dxccentityModelDAO.getCqzone());
            dxccEntity.setUpdatedAt(new Date());
            return dxccEntity;
        }

        public static DxccSession parse(QrzSessionDAO qrzSessionDAO) {
            DxccSession dxccSession = new DxccSession();
            dxccSession.setKey(qrzSessionDAO.getKey());
            dxccSession.setCount(qrzSessionDAO.getCount());
            dxccSession.setSubExp(qrzSessionDAO.getSubExp());
            dxccSession.setGmTime(qrzSessionDAO.getGmTime());
            dxccSession.setRemark(qrzSessionDAO.getRemark());
            dxccSession.setError(qrzSessionDAO.getError());
            dxccSession.setError(qrzSessionDAO.getError());
            return dxccSession;
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
}
