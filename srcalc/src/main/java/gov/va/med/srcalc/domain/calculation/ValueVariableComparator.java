package gov.va.med.srcalc.domain.calculation;

import gov.va.med.srcalc.domain.model.Variable;

import java.util.Comparator;

/**
 * Compares two {@link Value}s by their {@link Variable}s.
 */
public class ValueVariableComparator implements Comparator<Value>
{
    private final Comparator<Variable> fVariableComparator;
    
    /**
     * Constructs an instance using the specified Comparator.
     * @param comparator the Comparator used to compare variables for the specified
     *          values
     */
    public ValueVariableComparator(final Comparator<Variable> comparator)
    {
        fVariableComparator = comparator;
    }
    
    @Override
    public int compare(Value v1, Value v2)
    {
        return fVariableComparator.compare(v1.getVariable(), v2.getVariable());
    }
    
}
