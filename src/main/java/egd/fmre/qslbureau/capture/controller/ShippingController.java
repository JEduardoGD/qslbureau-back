package egd.fmre.qslbureau.capture.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.InputValidationDto;
import egd.fmre.qslbureau.capture.dto.RegionalRepresentativeDto;
import egd.fmre.qslbureau.capture.dto.ShippingLabelDto;
import egd.fmre.qslbureau.capture.dto.ShippingMethodDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.dto.ZoneruleDto;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Ship;
import egd.fmre.qslbureau.capture.entity.ShippingMethod;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Zonerule;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.ShipSevice;
import egd.fmre.qslbureau.capture.service.ShippingMethodService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.service.ZoneruleService;
import egd.fmre.qslbureau.capture.util.JsonParserUtil;

@RestController
@RequestMapping("shipping")
public class ShippingController {
    @Autowired private ShippingMethodService shippingMethodService;
    @Autowired private SlotLogicService slotLogicService;
    @Autowired private ZoneruleService zoneruleService;
    @Autowired private ShipSevice shipSevice;
    @Autowired private CapturerService capturerService;
    @Autowired private ModelMapper shippingMethodModelMapper;
    @Autowired private ModelMapper shipModelMapper;
    @Autowired private ModelMapper zoneruleModelMapper;
    @Autowired private ModelMapper regionalRepresentativeModelMapper;
    

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getActiveShippingMethods() throws QslcaptureException {
        
        List<ShippingMethodDto> shippingMethodDtoList = shippingMethodService.getAll()
        .stream()
        .map(sp -> shippingMethodModelMapper.map(sp, ShippingMethodDto.class)).collect(Collectors.toList());

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parseList(shippingMethodDtoList));
        } catch (QslcaptureException e) {
            standardResponse = new StandardResponse(true, e.getMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/ship/byslotid/{slotid}")
    public ResponseEntity<StandardResponse> getActiveShippingMethodsForSlotId(
            @PathVariable(value = "slotid") int slotid) throws QslcaptureException {
        Ship ship = shipSevice.getBySlotId(slotid);

        StandardResponse standardResponse;
        if (ship == null) {
            standardResponse = new StandardResponse(StaticValuesHelper.JSON_ARRAY_EMPTY);
            return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
        }
        
        InputValidationDto inputValidationDto = shipModelMapper.map(ship, InputValidationDto.class);
        
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parse(inputValidationDto));
        } catch (QslcaptureException e) {
            standardResponse = new StandardResponse(true, e.getMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/zonerules/forcallsign/{callsign}")
    public ResponseEntity<StandardResponse> getZonerulesForCallsign(@PathVariable(value = "callsign") String callsign) throws QslcaptureException {
        Zonerule zoneruleForCallsing = zoneruleService.findActiveByCallsign(callsign);
        
        ZoneruleDto zoneruleDtoForCallsing = zoneruleModelMapper.map(zoneruleForCallsing, ZoneruleDto.class);
        
        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parseList(zoneruleDtoForCallsing));
        } catch (QslcaptureException e) {
            standardResponse = new StandardResponse(true, e.getMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/forslotid/{slotid}")
    public ResponseEntity<StandardResponse> getShippingMethodBySlotId(@PathVariable(value = "slotid") int slotid) throws QslcaptureException {
        
        List<ShippingMethod> shippingMethodList = shippingMethodService.getAll();

        Slot slot = null;
        if (slotid > 0) {
            slot = slotLogicService.findById(slotid);
        }

        Zonerule zoneRule = null;
        if (slot != null && slot.getCallsignto() != null) {
            zoneRule = zoneruleService.findActiveByCallsign(slot.getCallsignto());
        }

        ShippingMethod spRegional = null;
        if (zoneRule == null) {
            spRegional = shippingMethodList.stream()
                    .filter(sp -> StaticValuesHelper.SHIPPING_METHOD_KEY_REGIONAL.equals(sp.getKey())).findFirst()
                    .orElse(null);
        }

        if (spRegional != null) {
            shippingMethodList.remove(spRegional);
        }
        
        List<ShippingMethodDto> shippingMethodDtoList = shippingMethodList.stream()
        .map(sp -> shippingMethodModelMapper.map(sp, ShippingMethodDto.class)).collect(Collectors.toList());

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parseList(shippingMethodDtoList));
        } catch (QslcaptureException e) {
            standardResponse = new StandardResponse(true, e.getMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<StandardResponse> ship(@RequestBody InputValidationDto inputValidationDto) {
        Ship ship = shipSevice.registerOrUpdateShip(inputValidationDto);
        
        inputValidationDto = shipModelMapper.map(ship, InputValidationDto.class);

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parse(inputValidationDto));
        } catch (QslcaptureException e) {
            standardResponse = new StandardResponse(true, e.getMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
        
    }

    @GetMapping("/regionalrepresentatives/forcallsign/{callsign}")
    public ResponseEntity<StandardResponse> regionalRepsForCallsign(@PathVariable(value = "callsign") String callsign) throws QslcaptureException {
        List<Capturer> capturers = capturerService.getCapturersforCallsign(callsign);
        
        List<RegionalRepresentativeDto> regionalRepresentatives = capturers.stream().map(c -> regionalRepresentativeModelMapper.map(c, RegionalRepresentativeDto.class)).collect(Collectors.toList());

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parseListRegionalRepresentatives(regionalRepresentatives));
        } catch (QslcaptureException e) {
            standardResponse = new StandardResponse(true, e.getMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PostMapping("/inputvalidation")
    public ResponseEntity<StandardResponse> regionalRepsForCallsign(@RequestBody InputValidationDto inputValidationDto) throws QslcaptureException {
        StandardResponse standardResponse;
        standardResponse = new StandardResponse(JsonParserUtil.parse(shipSevice.validateInputs(inputValidationDto)));
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/createshiplabel/byslotid/{slotid}")
    public ShippingLabelDto createshiplabel( @PathVariable(value = "slotid") int slotid) throws QslcaptureException {
    	return shipSevice.createShipLabel(slotid);
    }
}



















