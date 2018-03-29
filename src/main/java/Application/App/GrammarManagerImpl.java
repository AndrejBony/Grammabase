package Application.App;

import Application.Common.BaseXClient;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Manager for BaseX database working with grammars
 *
 * @author Andrej Bonis
 */
public class GrammarManagerImpl implements GrammarManager {

    private BaseXClient baseXClient;
    private static final Logger LOGGER = Logger.getLogger(GrammarManagerImpl.class);
    private final ResourceBundle bundle = ResourceBundle.getBundle("messages/messages");
    private static final String XML_HEADER = 
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
            + "\n"
            + "<!DOCTYPE grammar PUBLIC \"-//W3C//DTD GRAMMAR 1.0//EN\"\n"
            + "                  \"http://www.w3.org/TR/speech-grammar/grammar.dtd\">\n"
            + "\n"
            + "<grammar version=\"1.0\"\n"
            + "         xml:lang=\"en-US\"\n"
            + "         xmlns=\"http://www.w3.org/2001/06/grammar\"\n"
            + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n"
            + "         xsi:schemaLocation=\"http://www.w3.org/2001/06/grammar \n"
            + "                             http://www.w3.org/TR/speech-grammar/grammar.xsd\">";

    /**
     * Sets BaseX connection.
     *
     * @param baseXClient BaseXClient instance
     */
    public void setBaseXClient(BaseXClient baseXClient) {
        if (baseXClient == null) {
            throw new IllegalArgumentException();
        }

        this.baseXClient = baseXClient;
    }

    @Override
    public void createGrammar(Grammar grammar) throws BaseXException {
        if (grammar == null) {
            throw new IllegalArgumentException("Grammar is null");
        }
        try {
            String lastIdQuery = "max(//grammars/grammarData/string(@id))";
            LOGGER.log(Level.DEBUG, "Executing query: " + lastIdQuery);
            BaseXClient.Query basexQuery = baseXClient.query(lastIdQuery);
            String lastId = basexQuery.execute();
            Long newId;
            try {
                newId = Long.parseLong(lastId);
            } catch (NumberFormatException nfe) {
                newId = 0L;
            }
            newId++;

            String query = "xquery insert node "
                    + "<grammarData id='" + newId + "'>"
                    + "  <name>" + HtmlUtils.htmlEscape(grammar.getName()) + "</name>"
                    + "  <description>" + ((grammar.getDescription() == null) ? "" : HtmlUtils.htmlEscape(grammar.getDescription().trim())) + "</description>"
                    + "  <grammar></grammar>"
                    + "</grammarData>"
                    + "into //grammars";

            LOGGER.log(Level.DEBUG, "Executing query: " + query);
            baseXClient.execute(query);
            LOGGER.log(Level.INFO, "createGrammar - grammar created with id = " + newId);

            grammar.setId(newId);

        } catch (IOException e) {
            String msg = "Error when creating grammar in database";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    @Override
    public Grammar findGrammarById(Long id) throws BaseXException {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        try {
            String query = String.format("//grammars/grammarData[@id=%s]", id);
            String queryGrammar = new StringBuilder().append(query).append("/grammar").toString();
            BaseXClient.Query basexQuery = baseXClient.query(query);
            String xml = basexQuery.execute();

            if ("".equals(xml)) {
                return null;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document doc = dBuilder.parse(is);

            Element rootElement = doc.getDocumentElement();
            rootElement.normalize();

            NodeList nameNode = rootElement.getElementsByTagName("name");
            Element nodeElement = (Element) nameNode.item(0);
            String name = nodeElement.getFirstChild().getNodeValue();

            NodeList desNode = rootElement.getElementsByTagName("description");
            nodeElement = (Element) desNode.item(0);
            String description = null;
            if (nodeElement.getFirstChild() != null) {
                description = nodeElement.getFirstChild().getNodeValue();
            }

            Grammar grammar = new Grammar(id, name, description);
            basexQuery = baseXClient.query(queryGrammar);
            xml = basexQuery.execute();
            grammar.setContent(xml);

            return grammar;

        } catch (IOException | DOMException | ParserConfigurationException | SAXException e) {
            String msg = "Error when finding grammar in database";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    /**
     * Find list of grammars
     *
     * @return list of grammars
     * @throws BaseXException
     */
    public List<Grammar> findAllGrammars() throws BaseXException {
        try {
            BaseXClient.Query query = baseXClient.query("<grammars> "
                    + "{for $grammarData in //grammars/grammarData "
                    + "return $grammarData} "
                    + "</grammars>");
            String xml = query.execute();

            List<Grammar> grammars = new ArrayList<>();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document doc = dBuilder.parse(is);
            NodeList grammarData = doc.getElementsByTagName("grammarData");

            for (int i = 0; i < grammarData.getLength(); i++) {
                Grammar grammar = new Grammar();
                Element element = (Element) grammarData.item(i);

                grammar.setId(Long.parseLong(element.getAttribute("id")));

                NodeList nodes = element.getElementsByTagName("name");
                Element nodeElement = (Element) nodes.item(0);
                grammar.setName(nodeElement.getFirstChild().getNodeValue());

                nodes = element.getElementsByTagName("description");
                nodeElement = (Element) nodes.item(0);
                if (nodeElement.getFirstChild() != null) {
                    grammar.setDescription(nodeElement.getFirstChild().getNodeValue());
                }

                grammars.add(grammar);
            }

            return grammars;
        } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
            String msg = "Error when finding all grammars in database";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    @Override
    public void updateGrammar(Grammar grammar) throws BaseXException {
        validateGrammar(grammar);

        try {
            Grammar oldGrammar = findGrammarById(grammar.getId());
            if (oldGrammar == null) {
                throw new IllegalArgumentException();
            }

            String newDescription = null;
            String newName;
            String newContent = "<grammar></grammar>";

            if (grammar.getDescription() == null) {
                if (oldGrammar.getDescription() != null) {
                    newDescription = oldGrammar.getDescription();
                }
            } else {
                newDescription = HtmlUtils.htmlEscape(grammar.getDescription().trim());
            }

            if (grammar.getName() == null || grammar.getName().toLowerCase().equals(oldGrammar.getName().toLowerCase())) {
                newName = oldGrammar.getName();
            } else {
                newName = HtmlUtils.htmlEscape(grammar.getName());
                List<Grammar> grammars = findAllGrammars();
                for (Grammar g : grammars) {
                    if (g.getName().toLowerCase().equals(newName.toLowerCase())) {
                        throw new IllegalArgumentException(bundle.getString("g.name_exists"));
                    }
                }
            }

            if (!"".equals(grammar.getContent())) {
                newContent = grammar.getContent();
            }

            String query = "xquery replace node //grammars/grammarData[@id=" + grammar.getId() + "] with"
                    + "<grammarData id='" + grammar.getId() + "'>"
                    + "  <name>" + newName + "</name>"
                    + "  <description>" + newDescription + "</description>"
                    + "  " + newContent
                    + "</grammarData>";

            LOGGER.log(Level.DEBUG, "Executing query: " + query);
            baseXClient.execute(query);
        } catch (IOException e) {
            String msg = "Error when updating grammar in database";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    @Override
    public void deleteGrammar(Grammar grammar) throws BaseXException {
        validateGrammar(grammar);        
        
        try {
            if (!grammarExists(grammar)) {
                throw new IllegalArgumentException();
            }
            String query = "xquery delete node //grammars/grammarData[@id=" + grammar.getId() + "]";
            LOGGER.log(Level.DEBUG, "Executing query: " + query);
            LOGGER.debug("Executing query: " + query);
            baseXClient.execute(query);
            LOGGER.log(Level.INFO, "deleteGrammar -  grammar deleted with id = " + grammar.getId());
        } catch (IOException e) {
            String msg = "Error when deleting grammar in database";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    
        /**
     * Finds grammar by name.
     * 
     * @param name
     * @return Grammar with given name.
     * @throws org.basex.core.BaseXException
     */
   public Grammar findGrammar(String name) throws BaseXException {
        if (name == null) 
            throw new IllegalArgumentException("Grammar name is null");
        if("".equals(name))
            throw new IllegalArgumentException("Grammar name is empty");
        
        try {
            String idString = baseXClient.execute("xquery string(//grammars/grammarData[name/text()='" + name + "'][1]/@id)");
            if(idString == null || "".equals(idString))
                return null;
            Long id = Long.parseLong(idString);
            return findGrammarById(id);
        } catch (NumberFormatException | IOException e) {
            LOGGER.log(Level.ERROR, e);
            throw new BaseXException(e);
        } 
   }
    
        
    
    /**
     * Checks if grammar exists.
     *
     * @param grammar grammar to be checked.
     * @return true if grammar exists, false if not.
     * @throws BaseXException
     */
    public boolean grammarExists(Grammar grammar) throws BaseXException {
        try {
            String exists = baseXClient.execute("xquery exists(//grammars/grammarData[@id=" + grammar.getId() + "])");
            return exists.equals("true");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            throw new BaseXException(e);
        }
    }

    public String headerXml(String xml) {
        return xml.replace("<grammar>", XML_HEADER);
    }

    /**
     * Validates grammar attributes.
     *
     * @param grammar grammar to be validated
     */
    public static void validateGrammar(Grammar grammar) {
        if (grammar == null) {
            throw new IllegalArgumentException("Grammar is null");
        }
        if (grammar.getId() == null) {
            throw new IllegalArgumentException("Grammar id is null");
        }
        if ("".equals(grammar.getName()) || grammar.getName() == null) {
            throw new IllegalArgumentException("Grammar without name");
        }
    }

}
