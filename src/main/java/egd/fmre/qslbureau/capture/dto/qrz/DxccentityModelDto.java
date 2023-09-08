package egd.fmre.qslbureau.capture.dto.qrz;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "DXCC")
@XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@Data
public class DxccentityModelDto implements Serializable {
    private static final long serialVersionUID = 504739903644405865L;
    private Integer dxcc;
    private String cc;
    private String ccc;
    private String name;
    private String continent;
    private Integer ituzone;
    private Integer cqzone;
    private Integer timezone;
    private BigDecimal lat;
    private BigDecimal lon;
    @XmlElement(name = "Session")
    private QrzSessionDto session;
}
