package gov.va.med.srcalc.web.view;

import gov.va.med.srcalc.domain.model.BooleanVariable;

/**
 * This class represents the visible attributes of a {@link BooleanVariable} in order to
 * provide other attributes that are only necessary for display.
 */
public class BooleanVariableView extends VariableView
{
    private static final String BOOLEAN_FRAGMENT = "booleanInputs.jsp";
    
    /**
     * Constructs an instance.
     * @param variable the BooleanVariable to copy properties from
     * @param referenceInfo the reference information for this BooleanVariable
     */
    public BooleanVariableView(final BooleanVariable variable, final String referenceInfo)
    {
        super(variable, referenceInfo);
    }

    @Override
    public String getFragmentName()
    {
        return BOOLEAN_FRAGMENT;
    }
}
