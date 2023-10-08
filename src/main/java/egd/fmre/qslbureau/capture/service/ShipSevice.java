package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.dto.ShipDto;
import egd.fmre.qslbureau.capture.entity.Ship;

public interface ShipSevice {

    Ship getBySlotId(int slotId);

    Ship registerNewShip(ShipDto shipDto);

}
