package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.InputValidationDto;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Ship;
import egd.fmre.qslbureau.capture.entity.ShippingMethod;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Zone;
import egd.fmre.qslbureau.capture.entity.Zonerule;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import egd.fmre.qslbureau.capture.repo.ShipRepository;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import egd.fmre.qslbureau.capture.service.ShipSevice;
import egd.fmre.qslbureau.capture.service.ShippingMethodService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.service.ZoneService;
import egd.fmre.qslbureau.capture.service.ZoneruleService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class ShipSeviceImpl implements ShipSevice {
    
    @Autowired private ShipRepository shipRepository;

    @Autowired private ShippingMethodService shippingMethodService;
    @Autowired private SlotLogicService slotLogicService;
    @Autowired private ZoneService zoneService;
    @Autowired private ZoneruleService zoneruleService;
    @Autowired private CapturerService capturerService;
    @Autowired private RepresentativeService representativeService;

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
    public Ship registerOrUpdateShip(InputValidationDto inputValidationDto) {
        Slot slot = null;
        Integer slotId = inputValidationDto.getIdSlot();
        if (slotId != null) {
            slot = slotLogicService.findById(slotId);
            slotLogicService.changeSlotstatusToSend(slot);
        }

		Zone zone = null;
		ShippingMethod shippingMethod = null;
		Integer shippingMethodId = inputValidationDto.getShippingMethodId();
		if (shippingMethodId != null) {
			shippingMethod = shippingMethodService.findById(shippingMethodId);
			Integer zoneId = null;
			if (slot != null) {
				Zonerule zr = zoneruleService.findActiveByCallsign(slot.getCallsignto());
				if (zr != null) {
					zoneId = zr.getZone().getId();
				}
			}
			if (shippingMethod.equals(shippingMethodRegional) && zoneId != null) {
				zone = zoneService.findById(zoneId);
			}
		}
        
        Integer regionalRepresentativeId = inputValidationDto.getRegionalRepresentativeId();
        Representative representative = null;
        if (regionalRepresentativeId != null) {
            representative = representativeService.findById(regionalRepresentativeId);
        }

        Ship ship = new Ship();
        ship.setId(inputValidationDto.getShipId());
        ship.setDatetime(DateTimeUtil.getDateTime());
        ship.setSlot(slot);
        ship.setShippingMethod(shippingMethod);
        ship.setZone(zone);
        ship.setAddress(inputValidationDto.getAddress());
        ship.setRepresentative(representative);
        ship.setTrackingCode(inputValidationDto.getTrackingCode());
        return shipRepository.save(ship);
    }

    @Override
    public InputValidationDto validateInputs(InputValidationDto inputValidationDto) {
        Integer shippingMethodId;
        shippingMethodId = inputValidationDto.getShippingMethodId();

        boolean valid = true;
        StringBuilder errorSb = new StringBuilder();

        ShippingMethod shippingMethod = null;
        if (shippingMethodId == null) {
            valid = false;
            errorSb.append("|El método de envío es requerido");
        } else {
            shippingMethod = shippingMethodService.findById(shippingMethodId);
        }

        if (shippingMethod != null && shippingMethod.isRequireAddress()) {
            String address = inputValidationDto.getAddress() != null ? inputValidationDto.getAddress().trim() : null;
            address = StaticValuesHelper.EMPTY_STRING.equals(address) ? null : address;
            if (address == null) {
                valid = false;
                errorSb.append("|La dirección de envío es requerido");
            }
        }
        
        Integer regionalRepresentativeId = inputValidationDto.getRegionalRepresentativeId();
        Capturer capturer = null;
        if (regionalRepresentativeId != null) {
            capturer = capturerService.findById(regionalRepresentativeId);
        }
        
        if(shippingMethod != null && shippingMethod.equals(shippingMethodRegional) && capturer==null) {
            valid = false;
            errorSb.append("|Se requiere seleccionar un representante regional");
        }
        

        inputValidationDto.setValid(valid);
        inputValidationDto.setError(errorSb.toString());
        return inputValidationDto;
    }
    
    @Override
    public Ship findBySlot(Slot slot) {
        return shipRepository.findBySlot(slot);
    }

}
