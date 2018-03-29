package Application.App;

/**
 * Represents rule of grammar
 * 
 * @author Andrej Bonis
 */
public class Rule {
    
    private Long grammarId;
    private String id;
    private String content;

    /**
     * Gets ID of grammar where belongs given rule
     * 
     * @return ID of grammar
     */
    public Long getGrammarId() {
        return grammarId;
    }

    /**
     * Sets ID of grammar where belongs given rule
     * 
     * @param grammarId grammar ID
     */
    public void setGrammarId(Long grammarId) {
        this.grammarId = grammarId;
    }

    /**
     * Gets ID of rule
     * 
     * @return ID of rule
     */
    public String getId() {
        return id;
    }

    /**
     * Sets ID of rule
     * 
     * @param id rule ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets XML content of grammar as a string
     * @return grammar content as a string
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets XML content of grammar as a string
     * 
     * @param content grammar conent
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "{ id: " + getId() + " }";
    }
    
}
