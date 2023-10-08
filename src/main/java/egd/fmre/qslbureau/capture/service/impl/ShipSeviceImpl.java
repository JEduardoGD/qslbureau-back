package egd.fmre.qslbureau.capture.service.impl;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.ShipDto;
import egd.fmre.qslbureau.capture.entity.Ship;
import egd.fmre.qslbureau.capture.entity.ShippingMethod;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Zone;
import egd.fmre.qslbureau.capture.repo.ShipRepository;
import egd.fmre.qslbureau.capture.service.ShipSevice;
import egd.fmre.qslbureau.capture.service.ShippingMethodService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.service.ZoneService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;

@Service
public class ShipSeviceImpl implements ShipSevice {

    @Autowired private ShippingMethodService shippingMethodService;
    @Autowired private ShipRepository        shipRepository;
    @Autowired private SlotLogicService      slotLogicService;
    @Autowired ZoneService zoneService;
    
    private ShippingMethod shippingMethodRegional;
    
    @PostConstruct
    private void init() {
        shippingMethodRegional = shippingMethodService.findByKey("REGIONAL");
    }

    @Override
    public Ship getBySlotId(int slotId) {
        Slot slot = slotLogicService.findById(slotId);
        if (slot == null) {
            return null;
        }
        return shipRepository.findBySlot(slot);
    }
    
    @Override
    @Transactional
    public Ship registerNewShip(ShipDto shipDto) {
        Slot slot = null;
        Integer slotId = shipDto.getSlotId();
        if (slotId != null) {
            slot = slotLogicService.findById(slotId);
            slotLogicService.changeSlotstatusToSend(slot);
        }

        Zone zone = null;
        ShippingMethod shippingMethod = null;
        Integer shippingMethodId = shipDto.getShippingMethodId();
        if (shippingMethodId != null) {
            shippingMethod = shippingMethodService.findById(shippingMethodId);
            if(shippingMethod.equals(shippingMethodRegional) && shipDto.getZoneId() !=null) {
                zone = zoneService.findById(shipDto.getZoneId());
            }
        }

        Ship ship = new Ship();
        ship.setId(shipDto.getId());
        ship.setDatetime(DateTimeUtil.getDateTime());
        ship.setSlot(slot);
        ship.setShippingMethod(shippingMethod);
        ship.setZone(zone);
        ship.setAddress(shipDto.getAddress());
        ship.setTrackingCode(shipDto.getTrackingCode());
        return shipRepository.save(ship);
    }

}
