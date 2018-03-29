package Application.App;

import org.basex.core.BaseXException;
/**
 * Methods for work with grammars.
 * 
 * @author Andrej Bonis
 */
public interface GrammarManager {
    
    /**
     * Creates new SRGS grammar
     * 
     * @param grammar grammar to be created
     * @throws BaseXException 
     */
    void createGrammar(Grammar grammar) throws BaseXException;
    /**
     * Updates SRGS grammar
     * 
     * @param grammar grammar to be updated
     * @throws BaseXException
     */
    void updateGrammar(Grammar grammar) throws BaseXException;
    
    /**
     * Deletes SRGS grammar
     * 
     * @param grammar grammar to be deleted
     * @throws BaseXException
     */
    void deleteGrammar(Grammar grammar)throws BaseXException;
    
    /**
     * Finds grammar by given ID
     * 
     * @param id grammar id
     * @throws BaseXException
     * @return grammar with given ID
     */
    Grammar findGrammarById(Long id) throws BaseXException;
}
