package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.controller.MigrationSlotDto;
import egd.fmre.qslbureau.capture.dto.SlotCountqslDTO;
import egd.fmre.qslbureau.capture.dto.SlotDto;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;
import egd.fmre.qslbureau.capture.exception.QrzException;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;

public interface SlotLogicService {

	public Slot getSlotForQsl(String callsignTo, Local local) throws MaximumSlotNumberReachedException;

	public Slot findById(int slotId);

	void changeSlotstatusToOpen(Slot slot);

	List<Slot> getOpenedOrCreatedSlotsInLocal(Local local);

	List<Slot> getOpenedOrCreatedSlotsForCallsignInLocal(String callsign, Local local);

	List<Status> getCreatedAndOpenStatuses();

	Slot getSlotByCountry(String callsignTo, Local local) throws MaximumSlotNumberReachedException, QrzException;

	List<Slot> getOpenedOrCreatedSlots();

	List<Slot> getSlotsOfLocal(Local local);

	List<SlotCountqslDTO> getQslsBySlotIdList(List<Integer> slotIds);

    Slot changeSlotstatusToClosed(Slot slot, boolean createConfirmCode);

    Slot changeSlotstatusToIntl(Slot slot);

    List<Slot> orderAndFilterForFront(List<Slot> slots);

    List<Slot> orderAndFilterReadyForSend(List<Slot> slots);

    Slot changeSlotstatusToSend(Slot slot);

	Slot getNullSlot();

    Slot changeSlotstatusToUnconfirmable(Slot slot);

    void runCloseCloseableSlots(Local local);

    void runOpenOpenableSlots(Local local);

	SlotDto migrateSlot(MigrationSlotDto migrationSlotDto) throws QslcaptureException;

	Slot getOpenedOrCreatedSlotByCallsign(String calssingTo);
}
