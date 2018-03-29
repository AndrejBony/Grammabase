package Application.Common;

/**
 * Runtime issues reporting by tiles.
 * 
 * @author Andrej Bonis
 */
public class RuntimeDBException extends RuntimeException{
    
    public RuntimeDBException() {
        super();
    }
    
    public RuntimeDBException(String msg) {
        super(msg);
    }
    
    public RuntimeDBException(Throwable cause) {
        super(cause);
    }
    
    public RuntimeDBException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
