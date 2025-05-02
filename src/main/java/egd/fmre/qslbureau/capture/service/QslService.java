package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.CapturedCallsign;
import egd.fmre.qslbureau.capture.dto.QslSumatoryDto;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.enums.QslstatusEnum;

public interface QslService {

    List<Qsl> getActiveQslsForSlot(Slot slot);
    
    Qsl save(Qsl qsl);

    List<Qsl> getBySlotAndStatus(Slot slot, List<QslstatusEnum> qslstatusEnumList);
	List<Qsl> listOfActiveQslsWithCallsign(List<String> callsigns, Local local);

	Qsl getById(Integer id);

	List<CapturedCallsign> getCapturedCallsigns();
}
