package duber.engine.exceptions;

public class LWJGLException extends Exception {
    private static final long serialVersionUID = 1945532624676212988L;

    public LWJGLException() {
        super();
    }
    
    public LWJGLException(String message){
        super(message);    
    }
}