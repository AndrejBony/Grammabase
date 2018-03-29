package Application.Controllers;

import Application.Common.BaseXClient;
import Application.Common.DBUtils;
import Application.App.*;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Contorller for HTTP requests
 *
 * @author Andrej Bonis
 */
@Controller
public class ApiController {

    private static final Logger LOGGER = Logger.getLogger(ApiController.class);
    private BaseXClient baseXClient = null;

    /**
     * Shows XML of grammar.
     *
     * @param name Name of grammar
     * @return MIME application/xml type of grammar.
     */
    @RequestMapping(value = "/api/search-grammar")
    @GET
    @ResponseStatus(HttpStatus.OK)
    @Produces("application/xml")
    @ResponseBody
    public String searchGrammar(@RequestParam("grammar") String name) {
        if (name == null) {
            throw new BadRequestException();//Error 400 - the request is sematically incorrect
        }

        try {
            baseXClient = DBUtils.connectToDatabase();
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);

            Grammar grammar = grammarManager.findGrammar(name);

            if (grammar == null) {
                throw new BadRequestException(); //Error 400 - grammar is null
            }

            return grammarManager.headerXml(grammar.getContent());
        } catch (BaseXException e) {
            throw new WebApplicationException(500); //Error 500 - database error
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }
    
     /**
     * Shows XML of rule.
     *
     * @param name Name of grammar
     * @param ruleId Id of rule..
     * @return MIME application/xml type of rule.
     */
    @RequestMapping(value = "/api/search-rule")
    @GET
    @Produces("application/xml")
    @ResponseBody
    public String searchRule(@RequestParam("grammar") String name, 
                             @RequestParam("rule") String ruleId) {
        try {
            baseXClient = DBUtils.connectToDatabase();
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);
            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);
            
            Grammar grammar = grammarManager.findGrammar(name);
            
            if (grammar == null) {
                throw new WebApplicationException(400); //400
            }
            Rule rule = ruleManager.findRuleById(ruleId, grammar);
            if (rule == null) {
                throw new WebApplicationException(400); //400
            }

            return rule.getContent();
        } catch (BaseXException e) {
            throw new WebApplicationException(500); //500
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }
  

    /**
     * Creates rule into given grammar.
     *
     * @param name Name of grammar
     * @param xml
     * @param ruleId id of rule.
     * @return HTTP status.
     */
    @RequestMapping(value = "/api/create-rule")
    @POST
    @Produces("application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createRule(@RequestParam("grammar") String name, 
                             @RequestParam("xml") String xml,
                             @RequestParam("rule") String ruleId) {
        try {
            baseXClient = DBUtils.connectToDatabase();
            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);

            Rule rule = new Rule();
            rule.setId(ruleId);
            if(!"".equals(xml))
                rule.setContent(xml);

            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);
            Grammar grammar = grammarManager.findGrammar(name);
            if (grammar == null) {
                throw new WebApplicationException(400);
            }

            rule.setGrammarId(grammar.getId());

            ruleManager.createRule(rule);
            
            return rule.toString();
        } catch (BaseXException e) {
            throw new WebApplicationException(500);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(400);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }      
    }

       
    /**
     * Deletes rule from given grammar.
     *
     * @param name Name of grammar.
     * @param ruleId ID of rule.
     * @return Information about deleting.
     */
    @RequestMapping(value = "/api/delete-rule")
    @POST 
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public String deleteRule(@RequestParam("grammar") String name, 
                             @RequestParam("rule") String ruleId) {
        try {
            baseXClient = DBUtils.connectToDatabase();
            
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);
            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);
           
            Grammar grammar = grammarManager.findGrammar(name);
            
            if (grammar == null) {
                throw new WebApplicationException(400); //400
            }
            
            Rule rule = new Rule();
            rule.setId(ruleId);
            rule.setGrammarId(grammar.getId());
            ruleManager.deleteRule(rule);
            
            return "";
        } catch (BaseXException e) {
            throw new WebApplicationException(500); //500
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }
}
