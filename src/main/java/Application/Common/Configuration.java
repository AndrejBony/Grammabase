package Application.Common;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Static fields and methods for application configuration.
 *
 * @author Peter Gren
 */
public class Configuration {
    private static final Logger LOGGER = Logger.getLogger(Configuration.class);

    /**
     * Loads properties from file.
     *
     * @return Properties object which contains loaded items.
     */
    private static Properties init() throws IOException {
        Properties props = new Properties();
        props.load(Configuration.class.getClassLoader().getResourceAsStream("config.properties"));
        return props;
    }

    public static final String DATABASE_NAME = "DB_NAME";
    public static final String CREATED = "DB_CREATED";

    /**
     * Check if database is logged.
     * 
     * @return true if database is logged, false if it is not.
     * @throws ConfigurationException
     */
    public static Boolean dbInstalled() throws ConfigurationException {
        return Boolean.parseBoolean(Configuration.getValue(Configuration.CREATED));
    }
    
    /**
     * Gets value of given key from properties file.
     *
     * @param key key in config file.
     * @return value of key, otherwise returns empty string.
     * @throws ConfigurationException
     */
    public static String getValue(String key) throws ConfigurationException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        
        try {
            Properties props = init();
            return props.getProperty(key, "");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            throw new ConfigurationException(e);
        }
    }

    /**
     * Sets given key value into properties file.
     *
     * @param key key in config file.
     * @param value key value to store.
     * @throws ConfigurationException
     */
    public static void setValue(String key, String value) throws ConfigurationException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        Writer writer = null;
        
        try {
            Properties props = init();
            props.setProperty(key, value);
            URL url = Configuration.class.getClassLoader().getResource("config.properties");
            String path = url.getPath();
            writer = new FileWriter(path);
            props.store(writer, "");            
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            throw new ConfigurationException(e);
        } finally {
            try {writer.close();} catch (Exception e) {LOGGER.log(Level.ERROR, e);}
        }
    }
}
