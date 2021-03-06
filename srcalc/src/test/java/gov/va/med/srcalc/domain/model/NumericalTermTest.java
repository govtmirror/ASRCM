package gov.va.med.srcalc.domain.model;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests the {@link NumericalTerm} class.
 */
public class NumericalTermTest
{
    @Test
    public final void testBasic()
    {
        final float coeff = 10.1f;
        final NumericalVariable var = SampleModels.ageVariable();
        final NumericalTerm term = new NumericalTerm(var, coeff);
        
        assertEquals(coeff, term.getCoefficient(), 0.0f);
        assertSame(var, term.getVariable());
        assertEquals(ImmutableSet.of(var), term.getRequiredVariables());
        assertThat(term.toString(), allOf(
                containsString(Float.toString(coeff)),
                containsString(var.toString())));
    }
    
    @Test
    public final void testEquals()
    {
        EqualsVerifier.forClass(NumericalTerm.class)
            // Public interface disallows null.
            .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
            .verify();
    }
}
