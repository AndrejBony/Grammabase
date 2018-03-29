package Application.App;

/**
 * Represents grammar in SRGS format
 * 
 * @author Andrej Bonis
 */
public class Grammar {
    
    private Long id;
    private String name;
    private String description;
    private String content;    
    
    public Grammar(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.content = null;
    }
    
    public Grammar() {
    }
    
    /**
     * Gets name of grammar
     * 
     * @return name of grammar
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of grammar
     * 
     * @param name grammar name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets description of grammar
     * 
     * @return description of grammar
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of grammar
     * 
     * @param description grammar description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets XML content of grammar as string 
     * 
     * @return content of grammar as string
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets XML content of grammar as string
     * 
     * @param content grammar content as string
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Gets ID of grammar
     * 
     * @return ID of grammar
     */
    public Long getId() {
        return this.id;
    }
    
    /**
     * Sets ID of grammar
     * 
     * @param id grammar ID
     */
    public void setId(Long id) {
        this.id = id;
    }  
    
    @Override
    public String toString(){
        return "{ id: " + getId() + ", name: " + getName() + " }";                     
    }
}
