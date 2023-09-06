package egd.fmre.qslbureau.capture.dto.qrz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "Callsign")
@XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@Data
public class QrzCallsignDto implements Serializable {

    private static final long serialVersionUID = 4436853846557275450L;
    private String call;
    private String aliases;
    private Long dxcc;
    private String fname;
    private String name;
    private String addr1;
    private String addr2;
    private String state;
    private Integer zip;
    private String country;
    private Integer ccode;
    private BigDecimal lat;
    private BigDecimal lon;
    private String grid;
    private String county;
    private Integer fips;
    private String land;
    private Date efdate;
    private Date expdate;
    private String p_call;
    @XmlElement(name = "class")
    private String licence_class;
    private String codes;
    private String qslmgr;
    private String email;
    private String url;
    private Integer u_views;
    private String bio;
    private String image;
    private Integer serial;
    private Date moddate;
    @XmlElement(name = "MSA")
    private Integer msa;
    @XmlElement(name = "AreaCode")
    private Integer areaCode;
    @XmlElement(name = "TimeZone")
    private String timeZone;
    @XmlElement(name = "GMTOffset")
    private Integer gmtOffset;
    @XmlElement(name = "DST")
    private String dst;
    private String eqsl;
    private String mqsl;
    private Integer cqzone;
    private Integer ituzone;
    private String geoloc;
    private Integer born;
}
