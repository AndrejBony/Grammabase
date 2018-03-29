package Application.Common;

/**
 * Exception is thrown when there are some issues with configuration
 * 
 * @author Andrej Bonis
 */
public class ConfigurationException extends Exception {
    
    public ConfigurationException() {
        super();
    }
    
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
    
    public ConfigurationException(String msg) {
        super(msg);
    }
    
    public ConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
