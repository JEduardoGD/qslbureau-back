package egd.fmre.qslbureau.capture.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.SlotDto;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Qrzreg;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;

public abstract class QsldtoTransformer {
    public static QslDto map(Qsl qsl) {
        if (qsl == null) {
            return null;
        }
        QslDto qslDto = new QslDto();
        qslDto.setQslId(qsl.getId());
        qslDto.setTo(qsl.getTo());
        qslDto.setVia(qsl.getVia());
        qslDto.setSlotNumber(qsl.getSlot().getSlotNumber());
        qslDto.setDateTimeCapture(qsl.getDatetimecapture());
        
        qslDto.setStatus(qsl.getStatus().getId());
        return qslDto;
    }

    public static Set<QslDto> map(Set<Qsl> qsls) {
        return qsls.stream().map(QsldtoTransformer::map).collect(Collectors.toSet());
    }

    public static List<QslDto> map(List<Qsl> qsls, List<Qrzreg> qrzregs) {
        return qsls.stream().map(QsldtoTransformer::map).map(qslDto -> {
            Qrzreg qrzregTo = qrzregs.stream().filter(q -> q.getCallsign().equals(qslDto.getTo())).findFirst()
                    .orElse(null);
            qslDto.setQslToRecordFound(qrzregTo != null ? Boolean.TRUE : Boolean.FALSE);
            if (qslDto.getVia() != null) {
                Qrzreg qrzregVia = qrzregs.stream().filter(q -> q.getCallsign().equals(qslDto.getVia())).findFirst()
                        .orElse(null);
                qslDto.setQslViaRecordFound(qrzregVia != null ? Boolean.TRUE : Boolean.FALSE);
            }
            return qslDto;
        }).collect(Collectors.toList());
    }

    public static Qsl map(QslDto qslDto, Capturer capturer, Slot slot, Status status) {
        String via = (qslDto.getVia() == null || StaticValuesHelper.EMPTY_STRING.equals(qslDto.getVia())) ? null
                : qslDto.getVia();
        Qsl qsl = new Qsl();
        qsl.setCapturer(capturer);
        qsl.setTo(qslDto.getTo());
        qsl.setVia(via);
        qsl.setDatetimecapture(DateTimeUtil.getDateTime());
        qsl.setSlot(slot);
        qsl.setStatus(status);
        return qsl;
    }
    
    public static SlotDto map(Slot slot) {
        SlotDto s = new SlotDto();
        s.setId(slot.getId());
        s.setLocalId(slot.getLocal().getId());
        s.setCallsignto(slot.getCallsignto());
        s.setSlotNumber(slot.getSlotNumber());
        s.setCountry(slot.getCountry());
        s.setCreatedAt(slot.getCreatedAt());
        s.setClosedAt(slot.getCreatedAt());
        s.setStatusId(slot.getStatus().getId());
        return s;
    }
    
    public static SlotDto map(Slot slot, int qslsInSlot) {
        SlotDto s = new SlotDto();
        s.setId(slot.getId());
        s.setLocalId(slot.getLocal().getId());
        s.setCallsignto(slot.getCallsignto());
        s.setSlotNumber(slot.getSlotNumber());
        s.setCountry(slot.getCountry());
        s.setCreatedAt(slot.getCreatedAt());
        s.setClosedAt(slot.getCreatedAt());
        s.setSendAt(slot.getSendAt());
        s.setMovedToIntAt(slot.getMovedToIntAt());
        s.setStatusId(slot.getStatus().getId());
        s.setConfirmCode(slot.getConfirmCode());
        s.setQslsInSlot(qslsInSlot);
        return s;
    }
}































