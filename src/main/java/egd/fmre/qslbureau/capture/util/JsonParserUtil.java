package egd.fmre.qslbureau.capture.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import egd.fmre.qslbureau.capture.dto.CallsignRuleDto;
import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.SummaryQslDto;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.exception.JsonParserException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JsonParserUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String parse(List<CallsignRuleDto> qslSlotTrasladeList) throws JsonParserException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(out, qslSlotTrasladeList);
            final byte[] data = out.toByteArray();
            return new String(data);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String parse(Qsl qsl) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(qsl);
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

    public static String parse(QslDto captureQsl) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(captureQsl != null ? captureQsl : null);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }
}
