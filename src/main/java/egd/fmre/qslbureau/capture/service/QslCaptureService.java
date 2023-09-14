package egd.fmre.qslbureau.capture.service;

import java.util.List;
import java.util.Set;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.dto.SummaryQslDto;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;

public interface QslCaptureService {

    Integer countQslsBySlot(int slotId) throws QslcaptureException;

    Set<QslDto> qslsBySlot(int slotId) throws QslcaptureException;

    List<QslDto> qslsByLocal(int localId) throws QslcaptureException;

    StandardResponse deleteById(int qslid) throws QslcaptureException;

    QslDto captureQsl(QslDto qslDto) throws QslcaptureException;

    SummaryQslDto getActiveQslsForCallsign(String callsign);
}
