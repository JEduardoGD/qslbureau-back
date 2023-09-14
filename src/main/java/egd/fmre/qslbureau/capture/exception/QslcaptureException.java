package egd.fmre.qslbureau.capture.exception;

public class QslcaptureException extends Exception {

    private static final long serialVersionUID = 6090995118750240154L;

    public QslcaptureException(Throwable t) {
        super(t);
    }

    public QslcaptureException(String string) {
        super(string);
    }

}
