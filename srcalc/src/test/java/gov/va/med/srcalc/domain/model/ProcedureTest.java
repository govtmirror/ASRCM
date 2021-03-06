package gov.va.med.srcalc.domain.model;

import static org.junit.Assert.*;
import gov.va.med.srcalc.domain.model.Procedure;
import gov.va.med.srcalc.test.util.TestHelpers;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

/**
 * Tests the {@link Procedure} class.
 */
public class ProcedureTest
{
    @Test
    public final void testToString()
    {
        final Procedure p = SampleModels.repairLeftProcedure();
        assertEquals("26546 - Repair left hand (10.06)", p.toString());
    }

    @Test
    public final void testLongString()
    {
        final Procedure p = SampleModels.repairLeftProcedure();
        assertEquals(
                "26546 - Repair left hand - you know, the thing with fingers (10.06)",
                p.getLongString());
    }
    
    @Test
    public final void testEquals()
    {
        EqualsVerifier.forClass(Procedure.class).verify();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testCptTooShort()
    {
        new Procedure(
                "1234", 1.0f, "short desc", "long description", "Standard", true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testCptTooLong()
    {
        new Procedure(
                "123456", 1.0f, "short desc", "long description", "Standard", true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testLongDescEmpty()
    {
        new Procedure(
                "1234",
                1.0f,
                "short desc",
                "",
                "Standard",
                true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testLongDescTooLong()
    {
        new Procedure(
                "1234",
                1.0f,
                "short desc",
                TestHelpers.stringOfLength(Procedure.DESCRIPTION_MAX + 1),
                "Standard",
                true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testShortDescEmpty()
    {
        new Procedure(
                "1234",
                1.0f,
                "",
                "long description",
                "Standard",
                true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testShortDescTooLong()
    {
        new Procedure(
                "1234",
                1.0f,
                TestHelpers.stringOfLength(Procedure.DESCRIPTION_MAX + 1),
                "long description",
                "Standard",
                true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testComplexityEmpty()
    {
        new Procedure(
                "1234",
                1.0f,
                "short desc",
                "long description",
                "",
                true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testComplexityTooLong()
    {
        new Procedure(
                "1234",
                1.0f,
                "short desc",
                "long description",
                TestHelpers.stringOfLength(Procedure.COMPLEXITY_MAX + 1),
                true);
    }
    
}
