package egd.fmre.qslbureau.capture.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import egd.fmre.qslbureau.capture.dto.CallsignRuleDto;
import egd.fmre.qslbureau.capture.dto.InputValidationDto;
import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.RegionalRepresentativeDto;
import egd.fmre.qslbureau.capture.dto.ShippingMethodDto;
import egd.fmre.qslbureau.capture.dto.SlotDto;
import egd.fmre.qslbureau.capture.dto.SummaryQslDto;
import egd.fmre.qslbureau.capture.dto.ZoneruleDto;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.exception.JsonParserException;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
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

    public static String parse(SlotDto slotDto) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(slotDto);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }

    public static String parseSlotList(List<SlotDto> slotDtoList) throws JsonParserException {
        if (slotDtoList == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StaticValuesHelper.JSON_ARRAY_OPEN);
        for (SlotDto slotDto : slotDtoList) {
            stringBuilder.append(JsonParserUtil.parse(slotDto));
            stringBuilder.append(StaticValuesHelper.COMMA);
        }
        stringBuilder.append(StaticValuesHelper.JSON_ARRAY_CLOSE);
        return stringBuilder.toString().replaceAll("\\,\\]$", "]");
    }

    private static String parse(ShippingMethodDto slotDto) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(slotDto);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }

    public static String parseList(List<ShippingMethodDto> shippingMethodDtoList) throws JsonParserException {
        if (shippingMethodDtoList == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StaticValuesHelper.JSON_ARRAY_OPEN);
        for (ShippingMethodDto slotDto : shippingMethodDtoList) {
            stringBuilder.append(JsonParserUtil.parse(slotDto));
            stringBuilder.append(StaticValuesHelper.COMMA);
        }
        stringBuilder.append(StaticValuesHelper.JSON_ARRAY_CLOSE);
        return stringBuilder.toString().replaceAll("\\,\\]$", "]");
    }

    public static String parseList(ZoneruleDto zoneruleDtoForCallsing) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(zoneruleDtoForCallsing);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }

    public static String parseListRegionalRepresentatives(List<RegionalRepresentativeDto> regionalRepresentatives) throws JsonParserException {
        if (regionalRepresentatives == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StaticValuesHelper.JSON_ARRAY_OPEN);
        for (RegionalRepresentativeDto regionalRepresentative : regionalRepresentatives) {
            stringBuilder.append(JsonParserUtil.parse(regionalRepresentative));
            stringBuilder.append(StaticValuesHelper.COMMA);
        }
        stringBuilder.append(StaticValuesHelper.JSON_ARRAY_CLOSE);
        return stringBuilder.toString().replaceAll("\\,\\]$", "]");
    }

    private static Object parse(RegionalRepresentativeDto regionalRepresentative) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(regionalRepresentative);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }

    public static String parse(InputValidationDto validateInputs) throws JsonParserException {
        try {
            return objectMapper.writeValueAsString(validateInputs);
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }
    }
}
