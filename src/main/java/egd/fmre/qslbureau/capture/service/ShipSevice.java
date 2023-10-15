package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.dto.InputValidationDto;
import egd.fmre.qslbureau.capture.entity.Ship;
import egd.fmre.qslbureau.capture.entity.Slot;

public interface ShipSevice {

    Ship getBySlotId(int slotId);

    InputValidationDto validateInputs(InputValidationDto inputValidationDto);

    Ship registerOrUpdateShip(InputValidationDto inputValidationDto);

    Ship findBySlot(Slot slot);
}
