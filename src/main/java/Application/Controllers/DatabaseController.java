package Application.Controllers;

import Application.App.Grammar;
import Application.App.GrammarManagerImpl;
import Application.App.Rule;
import Application.App.RuleManagerImpl;
import Application.Common.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controllers for work with JSP
 *
 * @author Andrej Bonis
 */
@Controller
public class DatabaseController {

    private final ResourceBundle bundle = ResourceBundle.getBundle("messages/messages");
    private static final Logger LOGGER = Logger.getLogger(DatabaseController.class);
    private BaseXClient baseXClient = null;

    /**
     * Shows home page, if database does not exist, shows login page, otherwise
     * show grammars
     *
     * @param model Empty ModelMap.
     * @return if database does not exist, return login page, otherwise return
     * table of grammars
     * @throws ConfigurationException
     */
    @RequestMapping("/")
    public String index(ModelMap model) throws ConfigurationException {
        if (!Configuration.dbInstalled()) {
            return "login";
        }
        try {
            baseXClient = DBUtils.connectToDatabase();

            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);

            List<Grammar> grammars = grammarManager.findAllGrammars();

            model.addAttribute("grammars", grammars);
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
        return "index";
    }

    /**
     * Creates new database
     *
     * @param name database name
     * @return redirects to the grammar page view
     */
    @RequestMapping(value = "/create-database" , method = RequestMethod.POST)
    public String createDB(@RequestParam("inputName") String name) {
        try {
            DBUtils.createDatabase(name);
            return "redirect:/";
        } catch (ConfigurationException e) {
            throw new RuntimeDBException(bundle.getString("configEr"), e);
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        }
    }

    /**
     * Creates new grammar
     *
     * @return Create grammar page
     */
    @RequestMapping(value = "/add-grammar", method = RequestMethod.GET)
    public String addGrammar() {
        return "add-grammar";
    }

    /**
     * Shows grammar page.
     *
     * @param model Empty ModelMap.
     * @param id ID of grammar.
     * @param search Search indicator.
     * @param name Search string.
     * @return the grammar page view.
     */
    @RequestMapping(value = "/grammar/{id}", method = RequestMethod.GET)
    public String grammar(ModelMap model, @PathVariable Long id, @RequestParam(required = false) String search, @RequestParam(required = false) String name) {
        boolean isSearch = false;

        if (search != null) {
            isSearch = true;
            if (name.trim().equals("")) {
                return "redirect:/grammar/" + id;
            }
        }

        try {
            baseXClient = DBUtils.connectToDatabase();
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);

            Grammar grammar = grammarManager.findGrammarById(id);

            if (grammar == null) {
                throw new IllegalArgumentException("grammar is null");
            }

            String xml = grammarManager.headerXml(grammar.getContent());
            model.addAttribute("xml", xml);
            model.addAttribute("grammar", grammar);

            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);

            List<Rule> rules = ruleManager.findAllRules(grammar);

            if (name != null) {
                List<Rule> foundRules = new ArrayList<>();
                for (Rule rule : rules) {
                    if (rule.getId().toLowerCase().contains(name.toLowerCase())) {
                        foundRules.add(rule);
                    }
                }
                rules.clear();
                rules.addAll(foundRules);
            }

            model.addAttribute("rules", rules);
            model.addAttribute("search", isSearch);
            model.addAttribute("searchString", name);

            return "grammar";
        } catch (IllegalArgumentException | BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }

    /**
     * Shows udpate grammar.
     *
     * @param model Empty ModelMap
     * @param id ID of grammar
     * @return update grammar view
     */
    @RequestMapping(value = "/update-grammar/{id}")
    public String doUpdateGrammar(ModelMap model, @PathVariable Long id) {
        try {
            baseXClient = DBUtils.connectToDatabase();
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);

            Grammar grammar = grammarManager.findGrammarById(id);
            if (grammar == null) {
                throw new IllegalArgumentException();
            }

            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);
            List<Rule> rules = ruleManager.findAllRules(grammar);

            model.addAttribute("grammar", grammar);
            model.addAttribute("rules", rules);

            return "update-grammar";
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }

    /**
     * Saves grammar.
     *
     *
     * @param grammar grammar to be saved
     * @return redirects to grammar page view
     */
    @RequestMapping(value = "/save-grammar", method = RequestMethod.POST)
    public String doSaveGrammar(Grammar grammar) {
        if (grammar == null) {
            throw new IllegalArgumentException();
        }

        if (grammar.getName() == null || "".equals(grammar.getName().trim())) {
            throw new RuntimeDBException(bundle.getString("g.empty_name"));
        }

        try {
            baseXClient = DBUtils.connectToDatabase();
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);

            grammarManager.updateGrammar(grammar);

            return "redirect:/grammar/" + grammar.getId();
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }

    /**
     * Saves rule.
     *
     * @param rule rule to be saved
     * @return redirects to grammar page view.
     * @throws IOException
     */
    @RequestMapping(value = "/save-rule", method = RequestMethod.POST)
    public String doSaveRule(Rule rule) throws IOException {
        if (rule == null) {
            throw new IllegalArgumentException();
        }

        try {
            baseXClient = DBUtils.connectToDatabase();
            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);
            ruleManager.updateRule(rule);
            return "redirect:/grammar/" + rule.getGrammarId().toString();
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }

    @RequestMapping(value = "/create-grammar", method = RequestMethod.POST)
    public String doCreateGrammar(@RequestParam("inputName") String name,
                                  @RequestParam("inputDes") String des) {
        if ("".equals(name)) {
            throw new RuntimeDBException(bundle.getString("g.empty_name"));
        }

        Grammar grammar = new Grammar();
        grammar.setName(name.trim());
        grammar.setDescription(des);

        try {
            baseXClient = DBUtils.connectToDatabase();
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);
            List<Grammar> grammars = grammarManager.findAllGrammars();
            for (Grammar g : grammars) {
                if (g.getName().toLowerCase().equals(grammar.getName().toLowerCase())) {
                    throw new RuntimeDBException(bundle.getString("g.name_exists"));
                }
            }
            grammarManager.createGrammar(grammar);
            Long id = grammar.getId();

            return "redirect:/grammar/" + id;
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }

    /**
     * Deletes grammar.
     *
     * @param id ID of grammar.
     * @return grammar page view.
     */
    @RequestMapping(value = "/delete-grammar/{id}")
    public String doDeleteGrammar(@PathVariable Long id) {
        Grammar grammar = new Grammar();
        grammar.setId(id);

        try {
            baseXClient = DBUtils.connectToDatabase();
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);

            grammar = grammarManager.findGrammarById(id);
            grammarManager.deleteGrammar(grammar);

            return "redirect:/";
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }

    /**
     * Creates rule.
     *
     * @param ruleId Rule ID.
     * @param grammarId Grammar ID.
     * @return redirects to grammar page view.
     */
    @RequestMapping(value = "/create-rule", method = RequestMethod.POST)
    public String doCreateRule(@RequestParam String ruleId, @RequestParam Long grammarId) {
        if ("".equals(ruleId)) {
            throw new RuntimeDBException(bundle.getString("r.empty_name"));
        }

        try {
            baseXClient = DBUtils.connectToDatabase();
            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);
            Rule rule = new Rule();
            rule.setId(ruleId.trim());
            rule.setGrammarId(grammarId);
            ruleManager.createRule(rule);

            return "redirect:/grammar/" + grammarId;
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }

    /**
     * Updates rule.
     *
     * @param model Empty ModelMap
     * @param grammarId ID of grammar containg rule
     * @param ruleId ID of rule
     * @return update rule view
     */
    @RequestMapping(value = "/grammar/{grammarId}/rule/{ruleId}")
    public String doUpdateRule(ModelMap model, @PathVariable Long grammarId, @PathVariable String ruleId) {
        try {
            baseXClient = DBUtils.connectToDatabase();
            GrammarManagerImpl grammarManager = new GrammarManagerImpl();
            grammarManager.setBaseXClient(baseXClient);
            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);

            Grammar grammar = grammarManager.findGrammarById(grammarId);
            if (grammar == null) {
                throw new IllegalArgumentException();
            }
            Rule rule = ruleManager.findRuleById(ruleId, grammar);
            List<Rule> rules = ruleManager.findAllRules(grammar);

            model.addAttribute("grammar", grammar);
            model.addAttribute("rule", rule);
            model.addAttribute("rules", rules);

            return "update-rule";
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }

    /**
     * Deletes rule
     *
     * @param grammarId ID of grammar containg rule
     * @param ruleId ID of rule
     * @return redirects to grammar page view
     */
    @RequestMapping(value = "/delete-rule/{grammarId}/{ruleId}")
    public String doDeleteRule(@PathVariable Long grammarId, @PathVariable String ruleId) {
        if ("".equals(ruleId)) {
            throw new RuntimeDBException(bundle.getString("r.empty_name"));
        }
        Rule rule = new Rule();
        rule.setId(ruleId.trim());
        rule.setGrammarId(grammarId);

        try {
            baseXClient = DBUtils.connectToDatabase();
            RuleManagerImpl ruleManager = new RuleManagerImpl();
            ruleManager.setBaseXClient(baseXClient);

            ruleManager.deleteRule(rule);

            return "redirect:/grammar/" + grammarId;
        } catch (BaseXException e) {
            throw new RuntimeDBException(bundle.getString("runtimeEr"), e);
        } finally {
            try {
                baseXClient.close();
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, e);
            }
        }
    }
}
