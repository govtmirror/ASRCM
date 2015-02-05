package gov.va.med.srcalc.domain.model;

import java.util.*;

import gov.va.med.srcalc.domain.variable.Value;
import gov.va.med.srcalc.domain.variable.Variable;
import gov.va.med.srcalc.util.NoNullSet;

/**
 * <p>A {@link ModelTerm} derived via a {@link Rule}. A common use-case is
 * a summand added based on the values of two different variables.</p>
 * 
 * <p>Per Effective Java Item 17, this class is marked final because it was not
 * designed for inheritance.</p>
 */
public final class DerivedTerm extends ModelTerm
{
    private final Rule fRule;
    
    public DerivedTerm(final float coefficient, final Rule rule)
    {
        super(coefficient);
        fRule = rule;
    }

    /**
     * Returns the {@link Rule}.
     */
    public Rule getRule()
    {
        return fRule;
    }

    @Override
    public NoNullSet<Variable> getRequiredVariables()
    {
        return fRule.getRequiredVariables();
    }

    @Override
    public double getSummand(final Map<Variable, Value> inputValues)
    {
        return fRule.apply(
                new Rule.EvaluationContext(getCoefficient(), inputValues));
    }
    
    @Override
    public boolean equals(Object o)
    {
        // Performance optimization.
        if (o == this)
        {
            return true;
        }
        
        if (o instanceof DerivedTerm)
        {
            final DerivedTerm other = (DerivedTerm)o;
            return
                    baseEquals(other) &&
                    this.getRule().equals(other.getRule());
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(getCoefficient(), getRule());
    }
    
}