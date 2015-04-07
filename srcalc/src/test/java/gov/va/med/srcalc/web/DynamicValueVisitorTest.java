package gov.va.med.srcalc.web;

import static org.junit.Assert.*;

import java.util.ArrayList;

import gov.va.med.srcalc.domain.SampleObjects;
import gov.va.med.srcalc.domain.variable.BooleanValue;
import gov.va.med.srcalc.domain.variable.BooleanVariable;
import gov.va.med.srcalc.domain.variable.DiscreteNumericalValue;
import gov.va.med.srcalc.domain.variable.DiscreteNumericalVariable;
import gov.va.med.srcalc.domain.variable.MultiSelectOption;
import gov.va.med.srcalc.domain.variable.MultiSelectValue;
import gov.va.med.srcalc.domain.variable.MultiSelectVariable;
import gov.va.med.srcalc.domain.variable.NumericalRange;
import gov.va.med.srcalc.domain.variable.NumericalValue;
import gov.va.med.srcalc.domain.variable.NumericalVariable;
import gov.va.med.srcalc.domain.variable.ProcedureValue;
import gov.va.med.srcalc.domain.variable.ProcedureVariable;
import gov.va.med.srcalc.domain.variable.Variable;
import gov.va.med.srcalc.web.view.VariableEntry;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests {@link DynamicValueVisitor}.
 */
public class DynamicValueVisitorTest
{
	private DynamicValueVisitor fVisitor;
	
	@Before
	public void visitorSetup()
	{
		final VariableEntry variableEntry = new VariableEntry(new ArrayList<Variable>());
		fVisitor = new DynamicValueVisitor(variableEntry);
	}
	
	@Test
	public final void testNumerical() throws Exception
	{
		final NumericalVariable var = SampleObjects.sampleAgeVariable();
        final NumericalValue val = new NumericalValue(var, 1.2f);

        fVisitor.visitNumerical(val);
        assertEquals(String.valueOf(1.2f), fVisitor.getValues().getDynamicValues().get("age"));
	}
	
	@Test
	public final void testDiscreteNumerical() throws Exception
    {
		final DiscreteNumericalVariable var = SampleObjects.wbcVariable();
		final NumericalRange range = new NumericalRange(1.0f, true, 20.0f, true);
		final MultiSelectOption option = new MultiSelectOption("WNL");
        final DiscreteNumericalVariable.Category wnl = new DiscreteNumericalVariable.Category(range, option);
        final DiscreteNumericalValue val = DiscreteNumericalValue.fromCategory(var, wnl);

        fVisitor.visitDiscreteNumerical(val);
        assertNotNull("should have a value", fVisitor.getValues().getDynamicValues().get("wbc"));
    }
	
	@Test
	public final void testBoolean()
	{
		final BooleanVariable var = SampleObjects.dnrVariable();
		final BooleanValue val = new BooleanValue(var, true);
		fVisitor.visitBoolean(val);
		assertEquals("true", fVisitor.getValues().getDynamicValues().get("dnr"));
	}
	
	@Test
	public final void testMultiSelect()
	{
		final MultiSelectVariable var = SampleObjects.sampleGenderVariable();
		final MultiSelectValue val = new MultiSelectValue(var, new MultiSelectOption("Male"));
		fVisitor.visitMultiSelect(val);
		assertEquals("Male", fVisitor.getValues().getDynamicValues().get("gender"));
	}
	
	@Test
	public final void testProcedure()
	{
		final ProcedureVariable var = SampleObjects.sampleProcedureVariable();
		final ProcedureValue val = new ProcedureValue(var, SampleObjects.sampleRepairLeftProcedure());
		fVisitor.visitProcedure(val);
		assertEquals("26546", fVisitor.getValues().getDynamicValues().get("procedure"));
	}
}