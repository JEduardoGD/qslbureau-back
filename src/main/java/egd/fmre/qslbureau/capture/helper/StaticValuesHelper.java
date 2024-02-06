package egd.fmre.qslbureau.capture.helper;

public abstract class StaticValuesHelper {
    public static final String EMPTY_STRING = "";
    public static final String COMMA_REGEX = "\\,";
    public static final String COMMA = ",";
    public static final String SLASH = "/";
    
    public static final String JSON_ARRAY_OPEN = "[";
    public static final String JSON_ARRAY_CLOSE = "]";
    public static final String JSON_ARRAY_EMPTY = "[]";
    
    public static final int ZERO   =  0;
    public static final int TWELVE = 12;
    
    public static final String QRZ_ERROR_INVALID_SESSION_KEY = "Invalid session key";
    public static final String QRZ_ERROR_SESSION_TIMEOUT = "Session Timeout";
    public static final String QRZ_NEW_SESSION_MESSAGE= "Getting new session, the original session has error: {}";
    
    public static final String SHIPPING_METHOD_KEY_REGIONAL = "REGIONAL";
    
    public static final String QSLBUREAU_URL = "https://buro.fmre.mx";
    public static final String QSLBUREAU_CONFIRM_PATH = "confirm";
}
