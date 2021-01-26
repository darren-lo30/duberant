package duber.engine.exceptions;

/**
 * An exception used when an error occurs in the game
 * @author Darren Lo
 * @version 1.0
 */
public class LWJGLException extends Exception {
    private static final long serialVersionUID = 1945532624676212988L;

    /**
     * Constructs an exception without a message.
     */
    public LWJGLException() {
        super();
    }
    
    /**
     * Constructs an exception with a message
     * @param message the message
     */
    public LWJGLException(String message) {
        super(message);    
    }
}