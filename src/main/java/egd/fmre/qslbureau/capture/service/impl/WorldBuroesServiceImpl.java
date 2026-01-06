package egd.fmre.qslbureau.capture.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import egd.fmre.qslbureau.capture.dto.jsonburo.BuroDto;
import egd.fmre.qslbureau.capture.dto.jsonburo.BuroNodoPrincipalDto;
import egd.fmre.qslbureau.capture.dto.jsonburo.PrefijoDto;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.enums.SlotstatusEnum;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.repo.SlotRepository;
import egd.fmre.qslbureau.capture.service.WorldBuroesService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorldBuroesServiceImpl implements WorldBuroesService {
	@Autowired SlotRepository slotRepository;
	@Autowired LocalRepository localRepository;


	private Status slotstatusCreated;
	private Status slotstatusOpen;
	
	@PostConstruct
	private void Init() {
		slotstatusCreated = new Status(SlotstatusEnum.CREATED.getIdstatus());
		slotstatusOpen = new Status(SlotstatusEnum.OPEN.getIdstatus());
	}
	
    @Override
    public List<BuroDto> findByCallsign(String callsing, int localId) throws WorldBuroesServiceImplException {
        BuroNodoPrincipalDto allBuroes = null;
        try {
            allBuroes = readJsonFile();
        } catch (IOException e) {
            throw new WorldBuroesServiceImplException("Error reading buroes JSON file", e);
        }

	return findBuroesForCallsign(callsing, localId, allBuroes);
    }

    private List<BuroDto> findBuroesForCallsign(String callsing, int localId, BuroNodoPrincipalDto allBuroes) {
	List<BuroDto> buroesEncontrados = new ArrayList<>();
        List<BuroDto> buroDtoList = allBuroes.getBuroes();
	for (BuroDto buroDto : buroDtoList) {
	    for (PrefijoDto prefijo : buroDto.getPrefijos()) {
		String regex = String.format("^%s[A-Z0-9]*$", prefijo.getRegex());
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(callsing);
		if (matcher.matches() && !buroesEncontrados.contains(buroDto)) {
		    // Slot slot = slotLogicService.findSlotByCountryAndLocalId(callsing, localId);
		        Local local = localRepository.findById(localId);
			List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		    List<Slot> slots = slotRepository.findByLocalAndCountryAndStatuses(buroDto.getPais(), slotStatuses, local);
		    Slot slot = null;
		    if (slots != null && !slots.isEmpty()) {
			slot = slots.get(0);
		    }
		    if (slot != null) {
			buroDto.setSlotNumber(slot.getSlotNumber());
		    }
		    buroesEncontrados.add(buroDto);
		}
	    }
	}
        return buroesEncontrados;
    }

    private BuroNodoPrincipalDto readJsonFile() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("buroes.json");

        // Read the file content into a string
        String content = new String(is.readAllBytes());
        //String content = new String(Files.rea
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, BuroNodoPrincipalDto.class);
    }
}
