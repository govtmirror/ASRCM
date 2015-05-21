package gov.va.med.srcalc.service;

import java.util.List;

import gov.va.med.srcalc.domain.model.*;

/**
 * Service Layer facade for administrative functionalty.
 * @see gov.va.med.srcalc.service
 */
public interface AdminService extends ModelInspectionService
{
    /**
     * Returns all Variables in the database.
     * @return a list, in display name order
     */
    public List<AbstractVariable> getAllVariables();
    
    /**
     * Returns the Variable with the given display name for editing. Note that
     * the returned object must be given back to {@link
     * #saveVariable(AbstractVariable)} to persist any changes.
     * @throws InvalidIdentifierException if no such Variable exists
     */
    public AbstractVariable getVariable(final String key)
        throws InvalidIdentifierException;
    
    /**
     * Saves the given variable to the persistent store. The given variable may
     * be brand-new or one previously loaded by {@link #getVariable(String)}.
     * @param variable the variable to save
     */
    public void saveVariable(final AbstractVariable variable);
}
