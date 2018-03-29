package Application.App;

import Application.Common.BaseXClient;
import Application.Common.DBUtils;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.bind.ValidationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Manager for BaseX database working with rules
 *
 * @author Andrej Bonis
 */
public class RuleManagerImpl implements RuleManager {

    private BaseXClient baseXClient;
    private static final Logger LOGGER = Logger.getLogger(RuleManagerImpl.class);
    private final ResourceBundle bundle = ResourceBundle.getBundle("messages/messages");

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
    public void createRule(Rule rule) throws BaseXException {
        validateRule(rule);

        if (ruleExists(rule)) {
            throw new IllegalArgumentException(bundle.getString("r.name_exists"));
        }

        try {
            String query = "xquery insert node "
                    + (rule.getContent() == null ? "<rule id='" + rule.getId() + "' />" : rule.getContent())
                    + "into //grammars/grammarData[@id=" + rule.getGrammarId() + "]/grammar";

            LOGGER.log(Level.DEBUG, "Executing query: " + query);
            baseXClient.execute(query);
            LOGGER.log(Level.INFO, "addRule - rule added with ID = " + rule.getId());
        } catch (IOException e) {
            String msg = "Error when created rule in grammar";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    /**
     * Checks if rule exists
     *
     * @param rule rule to be checked
     * @return true if exists, false if not
     * @throws BaseXException
     */
    private Boolean ruleExists(Rule rule) throws BaseXException {
        validateRule(rule);

        try {
            String grammarQuery = String.format("xquery exists(//grammars/grammarData[@id=%s])", rule.getGrammarId());
            LOGGER.log(Level.DEBUG, "Executing query: " + grammarQuery);
            String grammarResult = baseXClient.execute(grammarQuery);
            if (!Boolean.parseBoolean(grammarResult)) {
                throw new IllegalArgumentException("Grammar does not exists.");
            }
            String query = String.format("xquery exists(//grammars/grammarData[@id=%s]/grammar//rule[@id='%s'])", rule.getGrammarId(), rule.getId());
            LOGGER.log(Level.DEBUG, "Executing query: " + query);
            String result = baseXClient.execute(query);

            return Boolean.parseBoolean(result);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            throw new BaseXException(e);
        }
    }

    @Override
    public Rule findRuleById(String id, Grammar grammar) throws BaseXException {
        if (id == null || grammar == null || grammar.getId() == null || baseXClient == null) {
            throw new IllegalArgumentException();
        }

        try {
            String queryString = String.format("//grammars/grammarData[@id=%s]/grammar//rule[@id='%s'][1]", grammar.getId(), id);
            BaseXClient.Query query = baseXClient.query(queryString);
            LOGGER.log(Level.DEBUG, "Executing query: " + queryString);

            String xml = query.execute();

            if ("".equals(xml)) 
                throw new IllegalArgumentException("Rule is null");
            

            Rule rule = new Rule();
            rule.setId(id);
            rule.setGrammarId(grammar.getId());
            rule.setContent(xml);

            return rule;
        } catch (IOException e) {
            String msg = "Error when finding rule in grammar";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    @Override
    public List<Rule> findAllRules(Grammar grammar) throws BaseXException {
        GrammarManagerImpl.validateGrammar(grammar);

        try {
            BaseXClient.Query query = baseXClient.query("<rules> "
                    + "{for $rule in //grammars/grammarData[@id=" + grammar.getId() + "]/grammar//rule "
                    + "return $rule} "
                    + "</rules>");
            String xml = query.execute();

            List<Rule> rules = new ArrayList<>();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document doc = dBuilder.parse(is);
            NodeList rulesNode = doc.getElementsByTagName("rule");

            for (int i = 0; i < rulesNode.getLength(); i++) {
                Rule rule = new Rule();
                Element element = (Element) rulesNode.item(i);

                rule.setId(element.getAttribute("id"));
                rule.setContent(DBUtils.serializeXml(element));
                rule.setGrammarId(grammar.getId());

                rules.add(rule);
            }

            return rules;
        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
            String msg = "Error with finding rules in grammar";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    @Override
    public void updateRule(Rule rule) throws BaseXException {
        if (rule.getContent() == null) {
            throw new IllegalArgumentException();
        }

        if (!ruleExists(rule)) {
            throw new IllegalArgumentException();
        }

        String newRule = "<rule id='" + rule.getId() + "' />";
        if (!"".equals(rule.getContent().trim())) {
            newRule = rule.getContent();
        }
        
        try {
            String query = String.format("xquery replace node //grammars/grammarData[@id=%s]/grammar//rule[@id='%s'][1] with %s",
                    rule.getGrammarId(), rule.getId(), newRule);
            LOGGER.log(Level.DEBUG, "Executing query: " + query);
            baseXClient.execute(query);
        } catch (IOException e) {
            String msg = "Error when updating rule in grammar";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    @Override
    public void deleteRule(Rule rule) throws BaseXException {
        validateRule(rule);

        if (!ruleExists(rule)) {
            throw new IllegalArgumentException("Rule doesn't exist");
        }

        try {
            String query = String.format("xquery delete node //grammars/grammarData[@id=%s]/grammar//rule[@id='%s'][1]", rule.getGrammarId(), rule.getId());
            LOGGER.log(Level.DEBUG, "Executing query: " + query);
            baseXClient.execute(query);
            LOGGER.log(Level.INFO, "deleteRule - rule deleted with id = '" + rule.getId() + "' in grammar " + rule.getGrammarId());
        } catch (IOException e) {
            String msg = "Error when deleting rule in grammar";
            LOGGER.log(Level.ERROR, msg, e);
            throw new BaseXException(msg, e);
        }
    }

    /**
     * Validates rule attributes
     *
     * @param rule rule to be validated
     */
    public static void validateRule(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("rule is null");
        }
        if (rule.getId() == null || "".equals(rule.getId())) {
            throw new IllegalArgumentException("rule without id");
        }
        if (rule.getGrammarId() == null) {
            throw new IllegalArgumentException("grammar id is null");
        }
    }
}
