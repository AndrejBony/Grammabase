package Application.Common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.w3c.dom.Element;


/**
 *
 * @author Bono
 */
public class DBUtils {

    private static final Logger LOGGER = Logger.getLogger(DBUtils.class);

    /**
     * Convert XML object hierarchy into string. Used for printing XML directly
     * into UI.
     *
     * @param element XML element.
     * @return String XML without prolog as a text.
     * @throws TransformerException
     */
    public static String serializeXml(Element element) throws TransformerException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(buffer);
        DOMSource source = new DOMSource(element);
        TransformerFactory.newInstance().newTransformer().transform(source, result);
        String xml = "";
        try {
            xml = buffer.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.ERROR, e);
        }
        return xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
    }

    /**
     * Connects to database
     *
     * @return BaseXClient instance
     * @throws BaseXException
     */
    public static BaseXClient connectToDatabase() throws BaseXException {
        try {
            BaseXClient baseXClient = new BaseXClient("localhost", 1984, "admin", "admin");
            baseXClient.execute("OPEN " + Configuration.getValue(Configuration.DATABASE_NAME));
            return baseXClient;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            throw new BaseXException(e);
        } catch (ConfigurationException | NumberFormatException e) {
            throw new BaseXException(e);
        }
    }

    /**
     * Create new database 
     * @param name name of database 
     * @throws BaseXException
     * @throws ConfigurationException
     */
    public static void createDatabase(String name) throws BaseXException, ConfigurationException {
        BaseXClient baseXClient = null;
        try {
            baseXClient = new BaseXClient("localhost", 1984, "admin", "admin");
            baseXClient.execute("CREATE DB " + name);
            baseXClient.execute("OPEN " + name);
            InputStream bais = new ByteArrayInputStream("<grammars></grammars>".getBytes("UTF-8"));
            baseXClient.add("config.properties", bais);

            Configuration.setValue(Configuration.DATABASE_NAME, name);
            Configuration.setValue(Configuration.CREATED, Boolean.TRUE.toString());

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            throw new BaseXException(e);
        } catch (ConfigurationException | NumberFormatException e) {
            throw new BaseXException(e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }
}
