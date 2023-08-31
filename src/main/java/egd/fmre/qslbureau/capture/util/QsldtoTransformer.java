package egd.fmre.qslbureau.capture.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;

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

    public static List<QslDto> map(List<Qsl> qsls) {
        return qsls.stream().map(QsldtoTransformer::map).collect(Collectors.toList());
    }

    public static Qsl map(QslDto qslDto, Capturer capturer, Slot slot, Status status) {
        Qsl qsl = new Qsl();
        qsl.setCapturer(capturer);
        qsl.setTo(qslDto.getTo());
        qsl.setDatetimecapture(DateTimeUtil.getDateTime());
        qsl.setSlot(slot);
        qsl.setStatus(status);
        return qsl;
    }
}
