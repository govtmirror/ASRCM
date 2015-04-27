package gov.va.med.srcalc.vista;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import gov.va.med.srcalc.domain.Calculation;
import gov.va.med.srcalc.domain.Patient;
import gov.va.med.srcalc.domain.SampleObjects;
import gov.va.med.srcalc.domain.variable.BooleanValue;
import gov.va.med.srcalc.domain.variable.MultiSelectOption;
import gov.va.med.srcalc.domain.variable.MultiSelectValue;
import gov.va.med.srcalc.domain.variable.NumericalValue;
import gov.va.med.srcalc.domain.variable.ProcedureValue;
import gov.va.med.srcalc.domain.variable.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class RpcVistaPatientDaoTest
{
	private final static String RADIOLOGIST_DUZ = "11716";
    private final static String PATIENT_RPC_RETURN = "TESTPATIENT^50^M";
    private final static String INVALID_SIGNATURE_RETURN = "0^Incorrect Electronic Signature Code Entered.";
    private final static String VALID_SIGNATURE_RETURN = "1^Progress note was created and signed successfully.";
    private final static String ELECTRONIC_SIGNATURE = "TESTSIG";
    private final static String DUMMY_BODY = "Note Body";
    
    private final static int PATIENT_DFN = 500;

    @Test
    public final void testLoadVistaPatientValid()
    {
        // Setup
        final VistaProcedureCaller caller = mock(VistaProcedureCaller.class);
        when(caller.doRpc(RADIOLOGIST_DUZ, RemoteProcedure.GET_PATIENT, String.valueOf(PATIENT_DFN)))
            .thenReturn(Arrays.asList(PATIENT_RPC_RETURN));
        final RpcVistaPatientDao dao = new RpcVistaPatientDao(caller, RADIOLOGIST_DUZ);

        // Behavior verification
        final Patient patient = dao.getPatient(PATIENT_DFN);
        assertEquals(PATIENT_DFN, patient.getDfn());
        assertEquals("TESTPATIENT", patient.getName());
        assertEquals("Male", patient.getGender());
        assertEquals(50, patient.getAge());
    }
    
    @Test
    public final void testSaveNoteInvalidSignature() throws Exception
    {
    	final VistaProcedureCaller caller = mock(VistaProcedureCaller.class);
        when(caller.doRpc(anyString(), eq(RemoteProcedure.SAVE_PROGRESS_NOTE),
        		anyString(), anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(INVALID_SIGNATURE_RETURN));
        final RpcVistaPatientDao dao = new RpcVistaPatientDao(caller, RADIOLOGIST_DUZ);
    	// Get patient and specialty
    	final Patient patient = SampleObjects.dummyPatient(PATIENT_DFN);
    	final Calculation calculation = Calculation.forPatient(patient);
    	calculation.setSpecialty(SampleObjects.sampleThoracicSpecialty());
    	runCalculation(calculation);
    	
    	// The note body being used here should not matter since the doRpc() call is being
    	// mocked and is told what to return.
    	assertEquals("Invalid Electronic Signature Code",
    			dao.saveRiskCalculationNote(calculation.getPatient(), "BadSig", DUMMY_BODY));
    	assertEquals("Invalid Electronic Signature Code",
    			dao.saveRiskCalculationNote(calculation.getPatient(), "AlsoBad", DUMMY_BODY));
    }
    
    @Test
    public final void testSaveNoteSuccess() throws Exception
    {
    	final VistaProcedureCaller caller = mock(VistaProcedureCaller.class);
    	 when(caller.doRpc(anyString(), eq(RemoteProcedure.SAVE_PROGRESS_NOTE),
         		anyString(), anyString(), anyString(), anyString()))
             .thenReturn(Arrays.asList(VALID_SIGNATURE_RETURN));
        final RpcVistaPatientDao dao = new RpcVistaPatientDao(caller, RADIOLOGIST_DUZ);
    	// Get patient and specialty
    	final Patient patient = SampleObjects.dummyPatient(PATIENT_DFN);
    	final Calculation calculation = Calculation.forPatient(patient);
    	calculation.setSpecialty(SampleObjects.sampleThoracicSpecialty());
    	runCalculation(calculation);
    	
    	assertEquals("Success", dao.saveRiskCalculationNote(calculation.getPatient(), ELECTRONIC_SIGNATURE, DUMMY_BODY));
    }
    
    private static void runCalculation(final Calculation calculation) throws Exception
    {
    	calculation.calculate(getCalculationValues());
    }
    
    private static List<Value> getCalculationValues() throws Exception
    {
    	List<Value> values = new ArrayList<Value>();
    	values.add(new ProcedureValue(SampleObjects.sampleProcedureVariable(), SampleObjects.sampleRepairLeftProcedure()));
    	values.add(new NumericalValue(SampleObjects.sampleAgeVariable(), 45.0f));
    	values.add(new BooleanValue(SampleObjects.dnrVariable(), false)); 
    	values.add(new MultiSelectValue(SampleObjects.functionalStatusVariable(), new MultiSelectOption("Independent")));
    	return values;
    }
}
