package egd.fmre.qslbureau.capture.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.SummaryQslDto;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.exception.JsonParserException;

public abstract class JsonParserUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String parse(QslDto qslDto) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(qslDto);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }

    public static String parse(Qsl save) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(save);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }

    public static String parse(SummaryQslDto qslInfoForCallsign) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(qslInfoForCallsign != null ? qslInfoForCallsign : null);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }
}
