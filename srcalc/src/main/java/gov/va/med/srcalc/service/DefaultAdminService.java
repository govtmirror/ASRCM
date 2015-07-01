package gov.va.med.srcalc.service;

import java.util.List;

import gov.va.med.srcalc.db.RiskModelDao;
import gov.va.med.srcalc.db.RuleDao;
import gov.va.med.srcalc.db.VariableDao;
import gov.va.med.srcalc.domain.model.*;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableCollection;

public class DefaultAdminService implements AdminService
{
    private static final Logger fLogger = LoggerFactory.getLogger(DefaultAdminService.class);
    
    private final VariableDao fVariableDao;
    private final RiskModelDao fRiskModelDao;
    private final RuleDao fRuleDao;
    
    @Inject
    public DefaultAdminService(
            final VariableDao variableDao, final RiskModelDao riskModelDao, final RuleDao ruleDao)
    {
        fVariableDao = variableDao;
        fRiskModelDao = riskModelDao;
        fRuleDao = ruleDao;
    }
    
    @Override
    @Transactional
    public List<AbstractVariable> getAllVariables()
    {
        fLogger.debug("Getting all Variables.");

        return fVariableDao.getAllVariables();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ImmutableCollection<VariableGroup> getAllVariableGroups()
    {
        fLogger.debug("Getting all VariableGroups.");
        
        return fVariableDao.getAllVariableGroups();
    }
    
    @Override
    @Transactional
    public AbstractVariable getVariable(final String keyName)
        throws InvalidIdentifierException
    {
        fLogger.debug("Getting Variable {}.", keyName);
        
        final AbstractVariable var = fVariableDao.getByKey(keyName);
        if (var == null)
        {
            throw new InvalidIdentifierException(
                    "There is no Variable called " + keyName);
        }
        return var;
    }
    
    /**
     * Returns true if there is already a different variable with the same key.
     * @param variable
     * @return
     */
    private boolean keyAlreadyExists(final AbstractVariable variable)
    {
        final AbstractVariable existingVar = fVariableDao.getByKey(variable.getKey());
        fLogger.debug("Existing variable with key {}: {}", variable.getKey(), existingVar);
        // If there is an existing var with the same key and it is not actually
        // the same variable, then we have a conflict.
        return (existingVar != null && variable.getId() != existingVar.getId());
    }
    
    @Override
    @Transactional
    public void saveVariable(final AbstractVariable variable)
    {
        fLogger.debug("Saving {}.", variable);

        // Per method Javadoc, throw a DuplicateVariableKeyException if a
        // different variable with the same variable key already exists.
        // (mergeVariable() below would throw a DataAccessException in this
        // case, but we cannot robustly determine the cause of the Exception so
        // we explicitly check here.)
        if (keyAlreadyExists(variable))
        {
            throw new DuplicateVariableKeyException(
                    "Duplicate variable key " + variable.getKey());
        }

        fVariableDao.mergeVariable(variable);
        // This is a significant (and infrequent) transaction: log it at INFO
        // level.
        fLogger.info("Saved variable {}.", variable.getKey());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ImmutableCollection<RiskModel> getAllRiskModels()
    {
        fLogger.debug("Getting all RiskModels.");
        return fRiskModelDao.getAllRiskModels();
    }
    
    @Override
    @Transactional
    public ImmutableCollection<Rule> getAllRules()
    {
        fLogger.debug("Getting all Rules.");
        return fRuleDao.getAllRules();
    }
    
    @Override
    @Transactional
    public Rule getRule(final String displayName) throws InvalidIdentifierException
    {
        fLogger.debug("Getting Rule by key {}.", displayName);
        final Rule rule = fRuleDao.getByDisplayName(displayName);
        if (rule == null)
        {
            throw new InvalidIdentifierException("There is no Rule called " + displayName);
        }
        return rule;
    }
    
    /**
     * Returns true if there is already a different rule with the same display name.
     * @param rule
     */
    private boolean ruleNameAlreadyExists(final Rule rule)
    {
        final Rule existingRule = fRuleDao.getByDisplayName(rule.getDisplayName());
        fLogger.debug("Existing rule with name {}: {}", rule.getDisplayName(), existingRule);
        // If there is an existing rule with the same name and it is not actually
        // the same rule, then we have a conflict.
        return (existingRule != null && rule.getId() != existingRule.getId());
    }

    @Override
    @Transactional
    public void saveRule(final Rule rule)
    {
        fLogger.debug("Saving Rule {}.", rule);

        // Per method Javadoc, throw a DuplicateRuleNameException if a
        // different rule with the same display name already exists.
        // (mergeRule() below would throw a DataAccessException in this
        // case, but we cannot robustly determine the cause of the Exception so
        // we explicitly check here.)
        if (ruleNameAlreadyExists(rule))
        {
            throw new DuplicateRuleNameException(
                    "Duplicate rule name " + rule.getDisplayName());
        }

        fRuleDao.mergeRule(rule);
        // This is a significant (and infrequent) transaction: log it at INFO
        // level.
        fLogger.info("Saved rule {}.", rule.getDisplayName());
    }
}
