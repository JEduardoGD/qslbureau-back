package egd.fmre.qslbureau.capture.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.SlotDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.enums.QslstatusEnum;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.LocalService;
import egd.fmre.qslbureau.capture.service.QslService;
import egd.fmre.qslbureau.capture.service.ShipSevice;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.util.JsonParserUtil;
import egd.fmre.qslbureau.capture.util.QsldtoTransformer;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("slot")
@Slf4j
public class SlotController {

    @Autowired SlotLogicService slotLogicService;
    @Autowired LocalService     localService;
    @Autowired QslService       qslService;
    @Autowired ShipSevice       shipSevice; 

    @GetMapping("/list/bylocalid/{localid}")
    public ResponseEntity<StandardResponse> getApplicableRules(@PathVariable int localid) {
        List<Slot> slots = null;

        Local local = localService.getById(localid);
        if (local != null) {
            slots = slotLogicService.getOpenedOrCreatedSlotsInLocal(local);
        } else {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(true, String.format("cant found local with id: %s", localid)),
                    new HttpHeaders(), HttpStatus.CREATED);
        }

        List<SlotDto> slotDtoList = slots.stream().map(s -> {
            List<Qsl> qsls = qslService.getBySlotAndStatus(s, Arrays.asList(QslstatusEnum.QSL_VIGENTE));
            return QsldtoTransformer.map(s, qsls.size());
        }).collect(Collectors.toList());

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parseSlotList(slotDtoList));
        } catch (QslcaptureException e) {
            log.error(e.getMessage());
            standardResponse = new StandardResponse(true, e.getLocalizedMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/bylocalid/{localid}")
    public ResponseEntity<StandardResponse> getSlotsByLocalId(@PathVariable int localid) {
        List<Slot> slots;
        Local local = localService.getById(localid);
        if (local != null) {
            slots = slotLogicService.getSlotsOfLocal(local);
        } else {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(true, String.format("cant found local with id: %s", localid)),
                    new HttpHeaders(), HttpStatus.CREATED);
        }
        
        slots = slotLogicService.orderAndFilterForFront(slots);

        List<SlotDto> slotDtoList = slots.stream().map(s -> {
            List<Qsl> qsls = qslService.getBySlotAndStatus(s, Arrays.asList(QslstatusEnum.QSL_VIGENTE));
            return QsldtoTransformer.map(s, qsls.size());
        }).collect(Collectors.toList());

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parseSlotList(slotDtoList));
        } catch (QslcaptureException e) {
            log.error(e.getMessage());
            standardResponse = new StandardResponse(true, e.getLocalizedMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/forSend/bylocalid/{localid}")
    public ResponseEntity<StandardResponse> getSlotsForSendByLocalId(@PathVariable int localid) {
        List<Slot> slots;
        Local local = localService.getById(localid);
        if (local != null) {
            slots = slotLogicService.getSlotsOfLocal(local);
        } else {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(true, String.format("cant found local with id: %s", localid)),
                    new HttpHeaders(), HttpStatus.CREATED);
        }
        
        slots = slotLogicService.orderAndFilterReadyForSend(slots);

        List<SlotDto> slotDtoList = slots.stream().map(s -> {
            List<Qsl> qsls = qslService.getBySlotAndStatus(s, Arrays.asList(QslstatusEnum.QSL_VIGENTE));
            return QsldtoTransformer.map(s, qsls.size());
        }).collect(Collectors.toList());

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parseSlotList(slotDtoList));
        } catch (QslcaptureException e) {
            log.error(e.getMessage());
            standardResponse = new StandardResponse(true, e.getLocalizedMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/close/byid/{slotid}")
    public ResponseEntity<StandardResponse> closeSlotForSend(@PathVariable int slotid) {
        Slot slot = slotLogicService.findById(slotid);
        if (slot == null) {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(true, String.format("El slot con id %s no se encuentra", slotid)),
                    new HttpHeaders(), HttpStatus.CREATED);
        }
        
        slot = slotLogicService.changeSlotstatusToClosed(slot, true);
        
        Local local  = new Local();
        local.setId(slot.getLocal().getId());
        slotLogicService.runCloseCloseableSlots(local);
        slotLogicService.runOpenOpenableSlots(local);
        
        SlotDto slotDto = QsldtoTransformer.map(slot, 0);

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parse(slotDto));
        } catch (QslcaptureException e) {
            log.error(e.getMessage());
            standardResponse = new StandardResponse(true, e.getLocalizedMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/movetointl/byid/{slotid}")
    public ResponseEntity<StandardResponse> moveToIntl(@PathVariable int slotid) {
        Slot slot = slotLogicService.findById(slotid);
        if (slot == null) {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(true, String.format("El slot con id %s no se encuentra", slotid)),
                    new HttpHeaders(), HttpStatus.CREATED);
        }
        
        slot = slotLogicService.changeSlotstatusToIntl(slot);
        
        SlotDto slotDto = QsldtoTransformer.map(slot, 0);

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parse(slotDto));
        } catch (QslcaptureException e) {
            log.error(e.getMessage());
            standardResponse = new StandardResponse(true, e.getLocalizedMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/setasunconfirmable/byid/{slotid}")
    public ResponseEntity<StandardResponse> setAsUnconfirmable(@PathVariable int slotid) {
        Slot slot = slotLogicService.findById(slotid);
        if (slot == null) {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(true, String.format("El slot con id %s no se encuentra", slotid)),
                    new HttpHeaders(), HttpStatus.CREATED);
        }
        
        slot = slotLogicService.changeSlotstatusToIntl(slot);
        
        SlotDto slotDto = QsldtoTransformer.map(slot, 0);

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parse(slotDto));
        } catch (QslcaptureException e) {
            log.error(e.getMessage());
            standardResponse = new StandardResponse(true, e.getLocalizedMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/byid/{slotid}")
    public ResponseEntity<StandardResponse> getSlotById(@PathVariable int slotid) {
        Slot slot = slotLogicService.findById(slotid);
        if (slot == null) {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(true, String.format("El slot con id %s no se encuentra", slotid)),
                    new HttpHeaders(), HttpStatus.CREATED);
        }
        
        SlotDto slotDto = QsldtoTransformer.map(slot, qslService.getActiveQslsForSlot(slot).size());
        
        //Ship ship = shipSevice.findBySlot(slot);
        //slotDto.
        
        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parse(slotDto));
        } catch (QslcaptureException e) {
            log.error(e.getMessage());
            standardResponse = new StandardResponse(true, e.getLocalizedMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }
}
