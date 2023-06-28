package egd.fmre.qslbureau.capture.util;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.entity.Qsl;

public abstract class QsldtoTransformer {
    public static QslDto map(Qsl qsl) {
        if (qsl == null) {
            return null;
        }
        QslDto qslDto = new QslDto();
        qslDto.setQslId(qsl.getId());
        qslDto.setToCallsign(qsl.getCallsignTo());
        qslDto.setSlotNumber(qsl.getSlot().getSlotNumber());
        return qslDto;
    }
}
