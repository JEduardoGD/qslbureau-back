package egd.fmre.qslbureau.capture.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import egd.fmre.qslbureau.capture.dto.LocalDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.CapturerLocal;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.exception.JsonParserException;
import egd.fmre.qslbureau.capture.service.CapturerLocalService;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.LocalService;
import egd.fmre.qslbureau.capture.util.JsonParserUtil;

@RestController
@RequestMapping("local")
public class LocalController  {

    @Autowired private CapturerService      capturerService;
    @Autowired private CapturerLocalService capturerLocalService;
    @Autowired private LocalService         localService;
    @Autowired private ModelMapper modelMapper;
    
    @RequestMapping(value = "/getlocalsofidcapturer/{idcapturer}", method = RequestMethod.GET)
    //public ResponseEntity<Set<LocalDto>> captureQsl(@PathVariable(value = "idcapturer") int idcapturer) {
    public ResponseEntity<Set<LocalDto>> captureQsl(@PathVariable int idcapturer) {
        Capturer activeCapturer = capturerService.getActiveCapturerById(idcapturer);
        if (activeCapturer == null) {
            return ResponseEntity.ok(null);
        }

        Set<CapturerLocal> capturerLocals = capturerLocalService.findCapturerLocalActiveByCapturer(activeCapturer);

        Set<Local> locals = new HashSet<>();
        for (CapturerLocal c : capturerLocals) {
            locals.add(localService.getById(c.getLocal().getId()));
        }
        
        return ResponseEntity.ok(locals.stream().map(l -> modelMapper.map(l, LocalDto.class)).collect(Collectors.toSet()));
    }
    
    @RequestMapping(value = "/getlocals/{idcapturer}", method = RequestMethod.GET)
    //public ResponseEntity<Set<LocalDto>> captureQsl(@PathVariable(value = "idcapturer") int idcapturer) {
    public ResponseEntity<StandardResponse> getLocalByIdCapturer(@PathVariable int idcapturer) {
        Capturer activeCapturer = capturerService.getActiveCapturerById(idcapturer);
        if (activeCapturer == null) {
            return ResponseEntity.ok(null);
        }

        Set<CapturerLocal> capturerLocals = capturerLocalService.findCapturerLocalActiveByCapturer(activeCapturer);

        Set<Local> locals = new HashSet<>();
        for (CapturerLocal c : capturerLocals) {
            locals.add(localService.getById(c.getLocal().getId()));
        }
        
        Set<LocalDto> LocalDtoSet = locals.stream().map(l -> modelMapper.map(l, LocalDto.class)).collect(Collectors.toSet());
        
        StandardResponse sr = null;
		try {
			sr = new StandardResponse(JsonParserUtil.parse(LocalDtoSet));
		} catch (JsonParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //return ResponseEntity.ok(locals.stream().map(l -> modelMapper.map(l, LocalDto.class)).collect(Collectors.toSet()));
		return new ResponseEntity<StandardResponse>(sr, new HttpHeaders(), HttpStatus.OK);
    }
    
}
