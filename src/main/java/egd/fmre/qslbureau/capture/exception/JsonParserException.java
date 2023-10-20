package egd.fmre.qslbureau.capture.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonParserException extends QslcaptureException {

    private static final long serialVersionUID = -1573639711712010320L;

    public JsonParserException(JsonParserException e) {
        super(e);
    }

    public JsonParserException(JsonProcessingException e) {
        super(e);
    }

}
