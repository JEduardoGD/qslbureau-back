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
public class QRZDatabaseDto implements Serializable {

    private static final long serialVersionUID = 4440460204920525292L;

    @XmlAttribute
    private BigDecimal version;

    @XmlElement(name = "Callsign")
    private QrzCallsignDto callsign;

    @XmlElement(name = "DXCC")
    private DxccentityModelDto dxcc;

    @XmlElement(name = "Session")
    private QrzSessionDto session;
}
