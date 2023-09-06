package egd.fmre.qslbureau.capture.dto.qrz;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "QRZDatabase")
@XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@Data
public class QRZDatabaseDAO implements Serializable {

    @XmlAttribute
    private BigDecimal version;

    @XmlElement(name = "Callsign")
    private QrzCallsignDAO callsign;

    @XmlElement(name = "DXCC")
    private DxccentityModelDAO dxcc;

    @XmlElement(name = "Session")
    private QrzSessionDTO session;
}
