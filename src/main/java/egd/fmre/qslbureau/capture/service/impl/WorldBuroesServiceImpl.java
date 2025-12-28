package egd.fmre.qslbureau.capture.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import egd.fmre.qslbureau.capture.dto.jsonburo.BuroDto;
import egd.fmre.qslbureau.capture.dto.jsonburo.BuroNodoPrincipalDto;
import egd.fmre.qslbureau.capture.dto.jsonburo.PrefijoDto;
import egd.fmre.qslbureau.capture.service.WorldBuroesService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorldBuroesServiceImpl implements WorldBuroesService {
    @Override
    public List<BuroDto> findByCallsign(String callsing) throws WorldBuroesServiceImplException {
        BuroNodoPrincipalDto allBuroes = null;
        try {
            allBuroes = readJsonFile();
        } catch (IOException e) {
            throw new WorldBuroesServiceImplException("Error reading buroes JSON file", e);
        }

	return findBuroesForCallsign(callsing, allBuroes);
    }

    private List<BuroDto> findBuroesForCallsign(String callsing, BuroNodoPrincipalDto allBuroes) {
	List<BuroDto> buroesEncontrados = new ArrayList<>();
        List<BuroDto> buroDtoList = allBuroes.getBuroes();
        for (BuroDto buroDto : buroDtoList) {
            for (PrefijoDto prefijo : buroDto.getPrefijos()) {
        	String regex = String.format("^%s[A-Z0-9]*$",prefijo.getRegex());
        	 Pattern pattern = Pattern.compile(regex);
        	 Matcher matcher = pattern.matcher(callsing);
		 if (matcher.matches() && !buroesEncontrados.contains(buroDto)) {
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
