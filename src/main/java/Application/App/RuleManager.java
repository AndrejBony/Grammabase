package Application.App;

import java.util.List;
import org.basex.core.BaseXException;

/**
 * Methods for work with rules
 * 
 * @author Andrej Bonis
 */
public interface RuleManager {
    
    /**
     * Creates new rule
     * 
     * @param rule rule stated for create
     * @throws BaseXException
     */
    void createRule(Rule rule) throws BaseXException;
    
    /**
     * Updates new rule
     * 
     * @param rule rule stated for update
     * @throws BaseXException
     */
    void updateRule(Rule rule) throws BaseXException;
    
    /**
     * Deletes new rule
     * 
     * @param rule rule stated for delete
     * @throws BaseXException
     */
    void deleteRule(Rule rule) throws BaseXException;
    
    /**
     * Finds rule by given ID in grammar
     * 
     * @param id rule ID
     * @param grammar grammar where rule belongs
     * @return rule with given id in grammar
     * @throws BaseXException
     */
    Rule findRuleById(String id, Grammar grammar) throws BaseXException;
    
    /**
     * Finds all rules in grammar
     * 
     * @param grammar grammar where rules belong
     * @return list of rules
     * @throws BaseXException
     */
    List<Rule> findAllRules(Grammar grammar) throws BaseXException;
}
