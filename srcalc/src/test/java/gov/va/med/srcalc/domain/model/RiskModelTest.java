package gov.va.med.srcalc.domain.model;

import static org.junit.Assert.*;

import java.util.*;

import gov.va.med.srcalc.domain.calculation.*;
import gov.va.med.srcalc.test.util.TestHelpers;
import gov.va.med.srcalc.util.MissingValuesException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class RiskModelTest
{
    @Test
    public final void testGetRequiredVariables()
    {
        // Setup
        final ProcedureVariable procedureVar = SampleModels.procedureVariable();
        final BooleanVariable dnrVar = SampleModels.dnrVariable();
        final NumericalVariable ageVar = SampleModels.ageVariable();
        final DiscreteNumericalVariable wbcVar = SampleModels.wbcVariable();
        final MultiSelectVariable fsVar = SampleModels.functionalStatusVariable();
        final RiskModel model = SampleModels.makeSampleRiskModel(
                "Thoracic 30-day Mortality Estimate (FY2013)",
                new HashSet<DerivedTerm>(),
                procedureVar, dnrVar, ageVar, wbcVar, fsVar);
        
        // Behavior verification
        final Set<AbstractVariable> expectedVariables =
                ImmutableSet.of(procedureVar, dnrVar, ageVar, wbcVar, fsVar);
        // Note that Set.equals() does not consider iteration order.
        assertEquals(expectedVariables, model.getRequiredVariables());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testDisplayNameTooLong()
    {
        final RiskModel model = SampleModels.thoracicRiskModel();
        model.setDisplayName(
                // 81 characters
                "01234567890123456789012345678901234567890123456789012345678901234567890123456789X");
    }
    
    @Test
    public final void testToString()
    {
        final RiskModel model = SampleModels.thoracicRiskModel();
        
        assertEquals(
                "RiskModel \"Thoracic 30-day mortality estimate\" with ID=0, and 4 terms",
                model.toString());
    }
    
    @Test
    public final void testCalculate() throws Exception
    {
        // Setup
        final ProcedureVariable procedureVar = SampleModels.procedureVariable();
        final BooleanVariable dnrVar = SampleModels.dnrVariable();
        final NumericalVariable ageVar = SampleModels.ageVariable();
        final DiscreteNumericalVariable wbcVar = SampleModels.wbcVariable();
        final MultiSelectVariable fsVar = SampleModels.functionalStatusVariable();
        final Set<DerivedTerm> derivedTerms = new HashSet<DerivedTerm>();
        final ValueMatcher matcher = new ValueMatcher(procedureVar, "#this.value.complexity == \"Standard\"");
        final List<ValueMatcher> valueMatchers = new ArrayList<ValueMatcher>();
        valueMatchers.add(matcher);
        derivedTerms.add(new DerivedTerm(6.0f, new Rule(valueMatchers, "#coefficient", true)));
        final RiskModel model = SampleModels.makeSampleRiskModel(
                "Thoracic 30-day Mortality Estimate (FY2013)",
                derivedTerms,
                procedureVar, dnrVar, ageVar, wbcVar, fsVar);

        final Procedure selectedProcedure = procedureVar.getProcedures().get(1);
        final float age = 26;
        final List<Value> values = Arrays.asList(
                procedureVar.makeValue(selectedProcedure),
                dnrVar.makeValue(true),
                ageVar.makeValue(age),
                wbcVar.makeValue(25.0f),
                fsVar.makeValue(fsVar.getOptions().get(2)));
        
        // Behavior verification
        double expectedSum =
                1.0f * selectedProcedure.getRvu() +
                2.0f * age +
                3.0f +
                4.0f +
                5.0f + 
                6.0f;
        double expectedExp = Math.exp(expectedSum);
        double expectedResult = expectedExp / (1 + expectedExp);
        assertEquals(expectedResult, model.calculate(values), 0.01f);
    }
    
    @Test(expected = MissingValuesException.class)
    public final void testCalculateIncompleteValues() throws Exception
    {
        final RiskModel model = SampleModels.thoracicRiskModel();
        
        model.calculate(Arrays.asList(
                new BooleanValue(SampleModels.dnrVariable(), true),
                new NumericalValue(SampleModels.ageVariable(), 12)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testCalculateDuplicateValues() throws Exception
    {
        final BooleanVariable dnrVar = SampleModels.dnrVariable();
        final RiskModel model = SampleModels.makeSampleRiskModel(
                "model", new HashSet<DerivedTerm>(), dnrVar);
        
        model.calculate(Arrays.<Value>asList(
                dnrVar.makeValue(true), dnrVar.makeValue(false)));
    }
    
    @Test
    public final void testEquals()
    {
        EqualsVerifier.forClass(RiskModel.class).
         	suppress(Warning.NULL_FIELDS,Warning.NONFINAL_FIELDS).
         	verify();
    }

    @Test
    public final void testCompareTo()
    {
        final RiskModel lesser = new RiskModel("a");
        final RiskModel middle = new RiskModel("b");
        final RiskModel greater = new RiskModel("c");
        
        TestHelpers.verifyCompareToContract(lesser, middle, greater);
    }
}
