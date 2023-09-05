package egd.fmre.qslbureau.capture.dto.qrz;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;



import lombok.Data;

@XmlRootElement(name = "Session")
@XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@Data
public class QrzSessionDAO implements Serializable {


    private static final long serialVersionUID = 6338929926411760720L;

    @XmlElement(name = "Key")
    private String key;

    @XmlElement(name = "Count")
    private Integer count;

    @XmlElement(name = "SubExp")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date subExp;

    @XmlElement(name = "GMTime")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date gmTime;

    @XmlElement(name = "Remark")
    private String remark;

    @XmlElement(name = "Error")
    private String error;

    static class DateTimeAdapter extends XmlAdapter<String, Date> {
        private final DateFormat dateFormat = new SimpleDateFormat("EEE MMM DD HH:mm:ss yyyy", Locale.ENGLISH);

        @Override
        public Date unmarshal(String xml) throws Exception {
            return dateFormat.parse(xml);
        }

        @Override
        public String marshal(Date object) throws Exception {
            return dateFormat.format(object);
        }

    }

}
