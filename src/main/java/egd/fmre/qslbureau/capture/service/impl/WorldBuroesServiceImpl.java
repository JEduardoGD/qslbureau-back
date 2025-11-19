package egd.fmre.qslbureau.capture.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import egd.fmre.qslbureau.capture.dto.jsonburo.BuroDto;
import egd.fmre.qslbureau.capture.dto.jsonburo.BuroNodoPrincipalDto;
import egd.fmre.qslbureau.capture.dto.jsonburo.PrefijoDto;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
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

        // FOR FIRST 3 CHARACTERS
        List<BuroDto> buroes = null;
        int startLength = Math.min(StaticValuesHelper.N_3, callsing.length());
        for(int i = startLength ; i > StaticValuesHelper.N_0 ; i--) {
            buroes = findBuroesForCallsign(callsing, allBuroes, i);
            if (!buroes.isEmpty()) {
                return buroes;
            }
        }
        return buroes;
    }

    private List<BuroDto> findBuroesForCallsign(String callsing, BuroNodoPrincipalDto allBuroes, int length) {
        String prefix = callsing.substring(StaticValuesHelper.N_0, length).toUpperCase();
        List<BuroDto> filteredBuroes = allBuroes.getBuroes().stream()
                .filter(buro -> {
                    for(PrefijoDto prefijo:buro.getPrefijos()) {
            if (prefijo.getInicio().length() == length) {
                return true;
            }
                    }
                    return false;
                })
                .collect(Collectors.toList());
        List<BuroDto> buroesEncontrados = new ArrayList<>();
        for (BuroDto buro : filteredBuroes) {
            List<String> prefixList = new ArrayList<>();
            for (PrefijoDto prefijo : buro.getPrefijos()) {
                String strInicio = prefijo.getInicio();
                String strFin = prefijo.getFin();
                String strActual = strInicio;
                prefixList.add(strInicio);
                while (!strActual.equals(strFin)) {
                    char q = strActual.charAt(strActual.length() - StaticValuesHelper.N_1);
                    int nextCharInt = q + StaticValuesHelper.C_1;
                    char nextChar = (char) nextCharInt;
                    strActual = strActual.substring(0, strActual.length() - StaticValuesHelper.C_1) + nextChar;
                    prefixList.add(strActual);
                }
            }
            if (prefixList.contains(prefix)) {
                buroesEncontrados.add(buro);
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
