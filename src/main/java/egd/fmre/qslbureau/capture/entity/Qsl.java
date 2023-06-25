package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import egd.fmre.qslbureau.capture.dto.QslDto;
import lombok.Data;

@Data
@Entity
@Table(name = "T_QSL")
public class Qsl implements Serializable {
    private static final long serialVersionUID = 7308799840296779543L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDQSL")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="IDSLOT")
    private Slot slot;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="IDCAPTURER")
    private Capturer capturer;
    
    @Column(name = "CALLSIGNTO")
    private String callsignTo;
    
    @Column(name = "DATETIMECAPTURE")
    private Date datetimecapture;
    
    public QslDto toDto() {
        QslDto qslDto = new QslDto();
        qslDto.setQslId(this.id);
        qslDto.setToCallsign(this.getCallsignTo());
        qslDto.setSlotNumber(slot.getSlotNumber());
        qslDto.setDateTimeCapture(datetimecapture);
        return qslDto;
    }
}
