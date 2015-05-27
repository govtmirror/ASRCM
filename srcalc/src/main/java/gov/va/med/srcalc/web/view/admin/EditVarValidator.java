package gov.va.med.srcalc.web.view.admin;

import gov.va.med.srcalc.domain.model.AbstractVariable;
import gov.va.med.srcalc.util.ValidationCodes;
import gov.va.med.srcalc.util.ValidationUtils2;

import org.springframework.validation.*;

/**
 * This Validator validates the base {@link EditVar} properties. Other
 * validators validate subclasses' properties.
 */
public class EditVarValidator implements Validator
{
    /**
     * Returns true if, and only if, the given class is {@link EditVar} or
     * a subclass.
     */
    @Override
    public boolean supports(final Class<?> clazz)
    {
        return EditVar.class.isAssignableFrom(clazz);
    }

    /**
     * Validates the given object, using error codes from {@link ValidationCodes}.
     * @param obj the object to validate. Must be an instance of {@link EditVar}.
     * @throws ClassCastException if the given object is not an EditVar
     */
    @Override
    public void validate(final Object obj, final Errors e)
    {
        // See method Javadoc: we assume that it's an instance of EditVar.
        final EditVar editVar = (EditVar)obj;
        
        // Validate variable key constraints. See AbstractVariable.getKey().
        ValidationUtils.rejectIfEmpty(e, "key", ValidationCodes.NO_VALUE);
        ValidationUtils2.rejectIfTooLong(e, "key", AbstractVariable.KEY_MAX);
        ValidationUtils2.rejectIfDoesntMatch(
                e, "key", AbstractVariable.VALID_KEY_PATTERN,
                new Object[] {AbstractVariable.VALID_KEY_CHARACTERS});

        // Validate displayName constraints. See
        // AbstractVariable.setDisplayName().
        ValidationUtils.rejectIfEmpty(e, "displayName", ValidationCodes.NO_VALUE);
        ValidationUtils2.rejectIfTooLong(e, "displayName", AbstractVariable.DISPLAY_NAME_MAX);
        ValidationUtils2.rejectIfDoesntMatch(
                e, "displayName", AbstractVariable.VALID_DISPLAY_NAME_PATTERN,
                new Object[] {AbstractVariable.VALID_DISPLAY_NAME_CHARACTERS});
        
        // Ensure we have a valid VariableGroup ID.
        if (!editVar.getGroup().isPresent())
        {
            e.rejectValue("groupId", ValidationCodes.INVALID_OPTION);
        }
        
        // Validate helpText constraints. See AbstractVariable.setHelpText().
        ValidationUtils2.rejectIfTooLong(e, "helpText", AbstractVariable.HELP_TEXT_MAX);
    }
    
}
