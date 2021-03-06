package gov.va.med.srcalc.domain.calculation;

import gov.va.med.srcalc.domain.model.*;

/**
 * A value of a {@link BooleanVariable}.
 */
public class BooleanValue implements Value
{
    private final BooleanVariable fVariable;
    private final boolean fValue;

    /**
     * Constructs an instance.
     * @param variable the BooleanVariable to which this value belongs
     * @param value the value of this instance
     */
    public BooleanValue(final BooleanVariable variable, final boolean value)
    {
        fVariable = variable;
        fValue = value;
    }
    
    @Override
    public BooleanVariable getVariable()
    {
        return fVariable;
    }
    
    @Override
    public Boolean getValue()
    {
        return fValue;
    }
    
    @Override
    public String getDisplayString()
    {
        return fValue ? "Yes" : "No";
    }
    
    @Override
    public void accept(final ValueVisitor visitor)
    {
        visitor.visitBoolean(this);
    }
    
    @Override
    public String toString()
    {
        return String.format("%s = %s", getVariable(), getValue());
    }
    
}
