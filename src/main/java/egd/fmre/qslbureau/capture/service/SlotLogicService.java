package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.SlotCountqslDTO;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;
import egd.fmre.qslbureau.capture.exception.QrzException;

public interface SlotLogicService {

	public Slot getSlotForQsl(String callsignTo, Local local) throws MaximumSlotNumberReachedException;

	public Slot findById(int slotId);

	List<SlotCountqslDTO> getQslsBySlot(List<Integer> SlotsInLocalIds);

	void changeSlotstatusToOpen(Slot slot);

	List<Slot> getOpenedOrCreatedSlotsInLocal(Local local);

	List<Slot> getOpenedOrCreatedSlotsForCallsignInLocal(String callsign, Local local);

	List<Status> getCreatedAndOpenStatuses();

	Slot getSlotByCountry(String callsignTo, Local local) throws MaximumSlotNumberReachedException, QrzException;

	List<Slot> getOpenedOrCreatedSlots();

	List<Slot> getSlotsOfLocal(Local local);

	void changeSlotstatusToClosed(Slot slot);

	void runCloseCloseableLocals(Local local);

	Slot getNullSlot();
}
