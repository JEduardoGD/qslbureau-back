package egd.fmre.qslbureau.capture.exception;

public class CallsignRuleException extends QslcaptureException {

    private static final long serialVersionUID = 1594484906911489745L;

    public CallsignRuleException(Throwable t) {
        super(t);
    }

    public CallsignRuleException(String string) {
        super(string);
    }
}
