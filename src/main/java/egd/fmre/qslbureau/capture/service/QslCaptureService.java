package egd.fmre.qslbureau.capture.service;

import java.util.Set;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;

public interface QslCaptureService {

    QslDto captureQsl(QslDto qsl);

    Integer countQslsBySlot(int slotId) throws QslcaptureException;

    Set<QslDto> qslsBySlot(int slotId) throws QslcaptureException;

    Set<QslDto> qslsByLocal(int localId) throws QslcaptureException;

}
